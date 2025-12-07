const ImageLog = require('../models/ImageLog');
const fs = require('fs').promises;
const path = require('path');

class ImageService {
  /**
   * Get all images for a user
   */
  async getUserImages(userId) {
    try {
      const images = await ImageLog.find({ userId })
        .sort({ created_at: -1 })
        .limit(50);
      return images;
    } catch (error) {
      throw new Error(`Failed to fetch user images: ${error.message}`);
    }
  }

  /**
   * Get image by ID
   */
  async getImageById(imageId) {
    try {
      const image = await ImageLog.findById(imageId);
      if (!image) {
        throw new Error('Image not found');
      }
      return image;
    } catch (error) {
      throw new Error(`Failed to fetch image: ${error.message}`);
    }
  }

  /**
   * Delete image by ID
   */
  async deleteImage(imageId, userId) {
    try {
      const image = await ImageLog.findOne({ _id: imageId, userId });
      
      if (!image) {
        throw new Error('Image not found or unauthorized');
      }

      // Delete physical file if exists
      if (image.filename) {
        const filePath = path.join(process.cwd(), 'uploads', image.filename);
        try {
          await fs.unlink(filePath);
        } catch (err) {
          console.warn(`Failed to delete physical file: ${err.message}`);
        }
      }

      // Delete from database
      await ImageLog.findByIdAndDelete(imageId);
      
      return { message: 'Image deleted successfully' };
    } catch (error) {
      throw new Error(`Failed to delete image: ${error.message}`);
    }
  }

  /**
   * Create image log
   */
  async createImageLog(data) {
    try {
      const imageLog = await ImageLog.create(data);
      return imageLog;
    } catch (error) {
      throw new Error(`Failed to create image log: ${error.message}`);
    }
  }

  /**
   * Get image statistics for user
   */
  async getUserImageStats(userId) {
    try {
      const totalImages = await ImageLog.countDocuments({ userId });
      const recentImages = await ImageLog.find({ userId })
        .sort({ created_at: -1 })
        .limit(5);

      return {
        totalImages,
        recentImages,
      };
    } catch (error) {
      throw new Error(`Failed to fetch image stats: ${error.message}`);
    }
  }
}

module.exports = new ImageService();
