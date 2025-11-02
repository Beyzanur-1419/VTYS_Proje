const cloudinary = require("cloudinary").v2;
const Config = require("../config/config");
const { AppError } = require("../utils/errors");
const { Readable } = require("stream");

/**
 * Cloudinary Service
 * Handles image uploads to Cloudinary cloud storage
 */
class CloudinaryService {
  constructor() {
    this.isConfigured = this.initialize();
  }

  initialize() {
    const cloudName = Config.CLOUDINARY_CLOUD_NAME;
    const apiKey = Config.CLOUDINARY_API_KEY;
    const apiSecret = Config.CLOUDINARY_API_SECRET;

    if (cloudName && apiKey && apiSecret) {
      cloudinary.config({
        cloud_name: cloudName,
        api_key: apiKey,
        api_secret: apiSecret,
      });
      return true;
    }

    return false;
  }

  /**
   * Check if Cloudinary is configured
   */
  isAvailable() {
    return this.isConfigured;
  }

  /**
   * Upload image from file path
   * @param {string} filePath - Local file path
   * @param {string} originalName - Original filename
   * @returns {Promise<Object>} Upload result with url and public_id
   */
  async uploadFromPath(filePath, originalName) {
    if (!this.isConfigured) {
      throw new AppError("Cloudinary is not configured", 500);
    }

    try {
      const result = await cloudinary.uploader.upload(filePath, {
        folder: "glowmance/skin-images",
        resource_type: "auto",
        use_filename: true,
        unique_filename: true,
        overwrite: false,
      });

      return {
        url: result.secure_url,
        public_id: result.public_id,
        format: result.format,
        width: result.width,
        height: result.height,
        bytes: result.bytes,
        created_at: result.created_at,
      };
    } catch (error) {
      console.error("Cloudinary upload error:", error);
      throw new AppError(`Cloudinary upload failed: ${error.message}`, 500);
    }
  }

  /**
   * Upload image from buffer
   * @param {Buffer} buffer - Image buffer
   * @param {string} originalName - Original filename
   * @param {string} mimeType - Image mimetype
   * @returns {Promise<Object>} Upload result with url and public_id
   */
  async uploadFromBuffer(buffer, originalName, mimeType) {
    if (!this.isConfigured) {
      throw new AppError("Cloudinary is not configured", 500);
    }

    try {
      return new Promise((resolve, reject) => {
        const uploadStream = cloudinary.uploader.upload_stream(
          {
            folder: "glowmance/skin-images",
            resource_type: "auto",
            use_filename: true,
            unique_filename: true,
            overwrite: false,
          },
          (error, result) => {
            if (error) {
              console.error("Cloudinary upload error:", error);
              reject(new AppError(`Cloudinary upload failed: ${error.message}`, 500));
            } else {
              resolve({
                url: result.secure_url,
                public_id: result.public_id,
                format: result.format,
                width: result.width,
                height: result.height,
                bytes: result.bytes,
                created_at: result.created_at,
              });
            }
          }
        );

        // Convert buffer to stream and pipe to upload stream
        const readable = new Readable();
        readable.push(buffer);
        readable.push(null);
        readable.pipe(uploadStream);
      });
    } catch (error) {
      console.error("Cloudinary buffer upload error:", error);
      throw new AppError(`Cloudinary buffer upload failed: ${error.message}`, 500);
    }
  }

  /**
   * Delete image from Cloudinary
   * @param {string} publicId - Cloudinary public_id
   * @returns {Promise<Object>} Deletion result
   */
  async deleteImage(publicId) {
    if (!this.isConfigured) {
      throw new AppError("Cloudinary is not configured", 500);
    }

    try {
      const result = await cloudinary.uploader.destroy(publicId);
      return result;
    } catch (error) {
      console.error("Cloudinary delete error:", error);
      throw new AppError(`Cloudinary delete failed: ${error.message}`, 500);
    }
  }

  /**
   * Get image URL from public_id
   * @param {string} publicId - Cloudinary public_id
   * @param {Object} options - Transformation options
   * @returns {string} Image URL
   */
  getImageUrl(publicId, options = {}) {
    if (!publicId) return null;

    try {
      return cloudinary.url(publicId, {
        secure: true,
        ...options,
      });
    } catch (error) {
      console.error("Cloudinary URL generation error:", error);
      return null;
    }
  }
}

module.exports = new CloudinaryService();
