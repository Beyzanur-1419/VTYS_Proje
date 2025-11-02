const AWS = require('aws-sdk');
const multer = require('multer');
const path = require('path');
const fs = require('fs').promises;
const Config = require('../config/config');
const { AppError } = require('../utils/errors');

/**
 * File Upload Service
 * Handles file uploads with support for Cloudinary, S3, and Local storage
 */
class FileUploadService {
  constructor() {
    // Initialize AWS S3 if configured
    if (Config.AWS_ACCESS_KEY_ID) {
      this.s3 = new AWS.S3({
        accessKeyId: Config.AWS_ACCESS_KEY_ID,
        secretAccessKey: Config.AWS_SECRET_ACCESS_KEY,
        region: Config.AWS_REGION
      });
    }

    // Setup uploads directory
    this.uploadsDir = path.join(process.cwd(), 'uploads');
    this.setupUploadsDir();

    // Configure local storage as fallback
    this.storage = multer.diskStorage({
      destination: async (req, file, cb) => {
        try {
          await this.setupUploadsDir();
          cb(null, this.uploadsDir);
        } catch (error) {
          cb(error);
        }
      },
      filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        const ext = path.extname(file.originalname);
        const name = path.basename(file.originalname, ext);
        cb(null, `${uniqueSuffix}-${name}${ext}`);
      }
    });

    // Always use disk storage to save file temporarily
    // Then we'll upload to cloud storage if configured
    this.upload = multer({
      storage: this.storage,
      limits: {
        fileSize: 5 * 1024 * 1024 // 5MB limit
      },
      fileFilter: this._fileFilter.bind(this)
    });
  }

  async setupUploadsDir() {
    try {
      await fs.access(this.uploadsDir);
    } catch {
      await fs.mkdir(this.uploadsDir, { recursive: true });
    }
  }

  _fileFilter(req, file, cb) {
    // Allowed image types
    const allowedMimeTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp', 'image/gif'];
    
    if (allowedMimeTypes.includes(file.mimetype.toLowerCase())) {
      cb(null, true);
    } else {
      cb(new AppError('Only image files are allowed! Supported formats: JPEG, PNG, WebP, GIF', 400), false);
    }
  }

  /**
   * Upload file to S3
   * @param {Object} file - Multer file object
   * @returns {Promise<string>} S3 URL
   */
  async uploadToS3(file) {
    if (!Config.AWS_ACCESS_KEY_ID || !this.s3) {
      throw new AppError('AWS credentials not configured', 500);
    }

    try {
      const fileBuffer = file.buffer || await fs.readFile(file.path);
      const key = `skin-images/${Date.now()}-${file.originalname}`;

      const params = {
        Bucket: Config.AWS_BUCKET_NAME,
        Key: key,
        Body: fileBuffer,
        ContentType: file.mimetype,
        ACL: 'public-read'
      };

      const result = await this.s3.upload(params).promise();
      return result.Location;
    } catch (error) {
      console.error('S3 upload error:', error);
      throw new AppError(`Error uploading file to S3: ${error.message}`, 500);
    }
  }

  /**
   * Get storage provider (priority: Cloudinary > S3 > Local)
   * @returns {string} Storage provider name
   */
  getStorageProvider() {
    return Config.STORAGE_PROVIDER;
  }

  /**
   * Get upload middleware
   * @returns {Function} Multer middleware
   */
  getUploadMiddleware() {
    return this.upload.single('image');
  }

  /**
   * Clean up temporary file
   * @param {string} filePath - File path to delete
   */
  async cleanupTempFile(filePath) {
    try {
      if (filePath && filePath.startsWith(this.uploadsDir)) {
        await fs.unlink(filePath);
      }
    } catch (error) {
      // Ignore cleanup errors
      console.error('Cleanup error:', error.message);
    }
  }
}

module.exports = new FileUploadService();
module.exports = new FileUploadService();