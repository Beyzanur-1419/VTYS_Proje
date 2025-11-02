const axios = require('axios');
const fs = require('fs');
const path = require('path');
const FormData = require('form-data');
const Config = require('../config/config');
const { AppError } = require('../utils/errors');

/**
 * ML Analysis Service
 * Handles communication with FastAPI ML service for skin image analysis
 */
class MLAnalysisService {
  constructor() {
    // ML Service URL from config or default
    this.mlServiceUrl = process.env.ML_SERVICE_URL || 'http://localhost:8000';
    this.timeout = 30000; // 30 seconds timeout
  }

  /**
   * Send image to ML service for analysis
   * @param {string} imagePath - Local file path to image
   * @returns {Promise<Object>} Analysis results
   */
  async analyzeImage(imagePath) {
    try {
      // Check if file exists
      if (!fs.existsSync(imagePath)) {
        throw new AppError('Image file not found', 404);
      }

      // Check ML service health
      await this.checkServiceHealth();

      // Prepare form data
      const formData = new FormData();
      const fileStream = fs.createReadStream(imagePath);
      const filename = path.basename(imagePath);

      formData.append('image', fileStream, {
        filename: filename,
        contentType: 'image/jpeg' // Adjust based on file type
      });

      // Send request to ML service
      const response = await axios.post(
        `${this.mlServiceUrl}/analyze`,
        formData,
        {
          headers: formData.getHeaders(),
          timeout: this.timeout,
          maxContentLength: Infinity,
          maxBodyLength: Infinity
        }
      );

      // Handle both response formats:
      // 1. { status: 'success', data: { ... } }
      // 2. Direct data format: { confidence, conditions, recommendations, metadata }
      if (response.data.status === 'success') {
        return response.data.data;
      } else if (response.data.confidence !== undefined) {
        // Direct data format (without status wrapper)
        return response.data;
      }

      throw new AppError('ML service returned error', 500);
    } catch (error) {
      if (error.response) {
        // ML service returned an error
        throw new AppError(
          error.response.data?.detail || 'ML service error',
          error.response.status || 500
        );
      } else if (error.request) {
        // Request was made but no response received
        throw new AppError('ML service is not responding', 503);
      } else {
        // Error in setting up request
        throw new AppError(`ML analysis failed: ${error.message}`, 500);
      }
    }
  }

  /**
   * Send image buffer to ML service for analysis
   * @param {Buffer} imageBuffer - Image buffer
   * @param {string} filename - Original filename
   * @param {string} mimetype - Image mimetype
   * @returns {Promise<Object>} Analysis results
   */
  async analyzeImageBuffer(imageBuffer, filename, mimetype) {
    try {
      // Check ML service health
      await this.checkServiceHealth();

      // Prepare form data
      const formData = new FormData();
      formData.append('image', imageBuffer, {
        filename: filename,
        contentType: mimetype
      });

      // Send request to ML service
      const response = await axios.post(
        `${this.mlServiceUrl}/analyze`,
        formData,
        {
          headers: formData.getHeaders(),
          timeout: this.timeout,
          maxContentLength: Infinity,
          maxBodyLength: Infinity
        }
      );

      // Handle both response formats:
      // 1. { status: 'success', data: { ... } }
      // 2. Direct data format: { confidence, conditions, recommendations, metadata }
      if (response.data.status === 'success') {
        return response.data.data;
      } else if (response.data.confidence !== undefined) {
        // Direct data format (without status wrapper)
        return response.data;
      }

      throw new AppError('ML service returned error', 500);
    } catch (error) {
      if (error.response) {
        throw new AppError(
          error.response.data?.detail || 'ML service error',
          error.response.status || 500
        );
      } else if (error.request) {
        throw new AppError('ML service is not responding', 503);
      } else {
        throw new AppError(`ML analysis failed: ${error.message}`, 500);
      }
    }
  }

  /**
   * Send base64 encoded image to ML service
   * @param {string} base64Image - Base64 encoded image
   * @returns {Promise<Object>} Analysis results
   */
  async analyzeImageBase64(base64Image) {
    try {
      // Check ML service health
      await this.checkServiceHealth();

      // Send request to ML service
      const response = await axios.post(
        `${this.mlServiceUrl}/analyze/base64`,
        {
          image: base64Image
        },
        {
          headers: {
            'Content-Type': 'application/json'
          },
          timeout: this.timeout
        }
      );

      // Handle both response formats:
      // 1. { status: 'success', data: { ... } }
      // 2. Direct data format: { confidence, conditions, recommendations, metadata }
      if (response.data.status === 'success') {
        return response.data.data;
      } else if (response.data.confidence !== undefined) {
        // Direct data format (without status wrapper)
        return response.data;
      }

      throw new AppError('ML service returned error', 500);
    } catch (error) {
      if (error.response) {
        throw new AppError(
          error.response.data?.detail || 'ML service error',
          error.response.status || 500
        );
      } else if (error.request) {
        throw new AppError('ML service is not responding', 503);
      } else {
        throw new AppError(`ML analysis failed: ${error.message}`, 500);
      }
    }
  }

  /**
   * Check if ML service is healthy
   * @returns {Promise<boolean>}
   */
  async checkServiceHealth() {
    try {
      const response = await axios.get(`${this.mlServiceUrl}/health`, {
        timeout: 5000
      });
      return response.data.status === 'healthy';
    } catch (error) {
      throw new AppError('ML service is unavailable', 503);
    }
  }

  /**
   * Get ML service info
   * @returns {Promise<Object>}
   */
  async getServiceInfo() {
    try {
      const response = await axios.get(`${this.mlServiceUrl}/`, {
        timeout: 5000
      });
      return response.data;
    } catch (error) {
      throw new AppError('Cannot get ML service info', 503);
    }
  }
}

module.exports = new MLAnalysisService();

