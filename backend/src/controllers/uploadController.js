const imageService = require('../services/imageService');
const upload = require('../utils/uploader');

class UploadController {
  /**
   * Upload a single image
   * POST /api/v1/upload
   */
  async uploadImage(req, res, next) {
    try {
      if (!req.file) {
        return res.status(400).json({ 
          success: false, 
          message: 'No image file uploaded' 
        });
      }

      // Create image log in MongoDB
      const imageLog = await imageService.createImageLog({
        userId: req.user.id,
        filename: req.file.filename,
        originalName: req.file.originalname,
        mimeType: req.file.mimetype,
        size: req.file.size,
        path: req.file.path,
        url: `/uploads/${req.file.filename}`,
      });

      res.status(201).json({
        success: true,
        data: {
          id: imageLog._id,
          filename: imageLog.filename,
          url: imageLog.url,
          size: imageLog.size,
          uploadedAt: imageLog.created_at,
        },
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Get user's uploaded images
   * GET /api/v1/upload/list
   */
  async getUserImages(req, res, next) {
    try {
      const images = await imageService.getUserImages(req.user.id);

      res.status(200).json({
        success: true,
        count: images.length,
        data: images,
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Delete an image
   * DELETE /api/v1/upload/:id
   */
  async deleteImage(req, res, next) {
    try {
      const { id } = req.params;
      const result = await imageService.deleteImage(id, req.user.id);

      res.status(200).json({
        success: true,
        message: result.message,
      });
    } catch (error) {
      if (error.message.includes('not found')) {
        return res.status(404).json({
          success: false,
          message: error.message,
        });
      }
      next(error);
    }
  }

  /**
   * Get image statistics
   * GET /api/v1/upload/stats
   */
  async getImageStats(req, res, next) {
    try {
      const stats = await imageService.getUserImageStats(req.user.id);

      res.status(200).json({
        success: true,
        data: stats,
      });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new UploadController();
