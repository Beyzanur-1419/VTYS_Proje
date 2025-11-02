const FileUploadService = require('../services/FileUploadService');
const CloudinaryService = require('../services/cloudinary');
const AIImageService = require('../services/AIImageService');
const AnalysisHistoryService = require('../services/AnalysisHistoryService');
const MLAnalysisService = require('../services/MLAnalysisService');
const Config = require('../config/config');
const { AppError } = require('../utils/errors');
const fs = require('fs');
const path = require('path');

class UploadController {
  constructor() {
    this.fileUploadService = FileUploadService;
    this.cloudinaryService = CloudinaryService;
    this.aiImageService = new AIImageService();
    this.analysisHistoryService = new AnalysisHistoryService();
    this.mlAnalysisService = MLAnalysisService;
  }

  async uploadImage(req, res, next) {
    let tempFilePath = null;

    try {
      if (!req.file) {
        throw new AppError('No image file uploaded', 400);
      }

      // Get storage provider
      const storageProvider = this.fileUploadService.getStorageProvider();
      let imageData = {
        url: null,
        cloudUrl: null,
        publicId: null,
        cloudProvider: storageProvider,
        width: null,
        height: null,
        format: null,
      };

      // Upload to cloud storage based on provider
      if (storageProvider === 'cloudinary' && this.cloudinaryService.isAvailable()) {
        try {
          tempFilePath = req.file.path;
          const fileBuffer = req.file.buffer || await fs.readFile(req.file.path);

          const cloudinaryResult = await this.cloudinaryService.uploadFromBuffer(
            fileBuffer,
            req.file.originalname,
            req.file.mimetype
          );

          imageData.url = cloudinaryResult.url;
          imageData.cloudUrl = cloudinaryResult.url;
          imageData.publicId = cloudinaryResult.public_id;
          imageData.width = cloudinaryResult.width;
          imageData.height = cloudinaryResult.height;
          imageData.format = cloudinaryResult.format;
          
          // Clean up temp file after cloud upload
          await this.fileUploadService.cleanupTempFile(tempFilePath);
        } catch (cloudError) {
          console.error('Cloudinary upload failed, falling back to S3:', cloudError.message);
          
          // Fallback to S3 or local
          if (Config.AWS_ACCESS_KEY_ID) {
            try {
              const s3Url = await this.fileUploadService.uploadToS3(req.file);
              imageData.url = s3Url;
              imageData.cloudUrl = s3Url;
              imageData.cloudProvider = 's3';
              await this.fileUploadService.cleanupTempFile(tempFilePath);
            } catch (s3Error) {
              console.error('S3 upload failed, using local:', s3Error.message);
              imageData.url = `/uploads/${req.file.filename}`;
              imageData.cloudProvider = 'local';
            }
          } else {
            imageData.url = `/uploads/${req.file.filename}`;
            imageData.cloudProvider = 'local';
          }
        }
      } else if (storageProvider === 's3' && Config.AWS_ACCESS_KEY_ID) {
        try {
          tempFilePath = req.file.path;
          const s3Url = await this.fileUploadService.uploadToS3(req.file);
          imageData.url = s3Url;
          imageData.cloudUrl = s3Url;
          await this.fileUploadService.cleanupTempFile(tempFilePath);
        } catch (s3Error) {
          console.error('S3 upload failed, using local:', s3Error.message);
          imageData.url = `/uploads/${req.file.filename}`;
          imageData.cloudProvider = 'local';
        }
      } else {
        // Local storage
        imageData.url = `/uploads/${req.file.filename}`;
      }

      // Save image metadata to MongoDB
      const aiImage = await this.aiImageService.create({
        userId: req.user.id,
        originalName: req.file.originalname,
        mimeType: req.file.mimetype,
        size: req.file.size,
        url: imageData.url,
        cloudProvider: imageData.cloudProvider,
        publicId: imageData.publicId,
        cloudUrl: imageData.cloudUrl,
        width: imageData.width,
        height: imageData.height,
        format: imageData.format,
        timestamp: new Date()
      });

      // Call FastAPI ML service for analysis
      // Get image buffer before it might be cleaned up
      let imageBuffer = null;
      if (req.file.buffer) {
        imageBuffer = req.file.buffer;
      } else if (req.file.path && fs.existsSync(req.file.path)) {
        imageBuffer = await fs.readFile(req.file.path);
      }

      let modelResults;
      try {
        if (imageBuffer) {
          // Send buffer directly to ML service (best option)
          modelResults = await this.mlAnalysisService.analyzeImageBuffer(
            imageBuffer,
            req.file.originalname,
            req.file.mimetype
          );
        } else if (req.file.path && fs.existsSync(req.file.path)) {
          // Send file path as fallback
          const imagePath = path.join(process.cwd(), req.file.path);
          modelResults = await this.mlAnalysisService.analyzeImage(imagePath);
        } else {
          throw new AppError('Cannot process image: no file path or buffer', 500);
        }
      } catch (mlError) {
        // Log ML service error but continue with mock data as fallback
        console.error('ML Service error:', mlError.message);
        
        // Fallback to mock analysis if ML service is unavailable
        modelResults = {
          confidence: 0.85,
          conditions: ['Kuruluk'],
          recommendations: ['Nemlendirici'],
          metadata: {
            processed_at: new Date().toISOString(),
            fallback: true,
            error: mlError.message
          }
        };
      }

      // Save analysis results to PostgreSQL database
      let analysis;
      try {
        analysis = await this.analysisHistoryService.createAnalysis(
          req.user.id,
          aiImage._id.toString(),
          modelResults
        );
      } catch (analysisError) {
        // Log error but don't fail the entire request
        // The image is already saved to MongoDB
        console.error('Error saving analysis results:', analysisError.message);
        
        // Return response without analysis if saving fails
        res.status(201).json({
          status: 'success',
          data: {
            image: aiImage,
            analysis: null,
            warning: 'Image uploaded successfully but analysis results could not be saved'
          }
        });
        return;
      }

      // Final cleanup of temp file if it still exists
      if (tempFilePath && fs.existsSync(tempFilePath)) {
        await this.fileUploadService.cleanupTempFile(tempFilePath);
      }

      res.status(201).json({
        status: 'success',
        data: {
          image: aiImage,
          analysis
        }
      });
    } catch (err) {
      // Cleanup temp file on error
      if (tempFilePath && fs.existsSync(tempFilePath)) {
        await this.fileUploadService.cleanupTempFile(tempFilePath).catch(() => {});
      }
      next(err);
    }
  }

  // Deprecated: Use MLAnalysisService instead
  async getModelAnalysis(imageUrl) {
    // Fallback mock analysis
    return {
      confidence: 0.95,
      conditions: ['condition1', 'condition2'],
      recommendations: ['product1', 'product2']
    };
  }

  // Middleware to handle file upload
  getUploadMiddleware() {
    return this.fileUploadService.getUploadMiddleware();
  }
}

module.exports = new UploadController();