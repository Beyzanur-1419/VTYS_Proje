const axios = require('axios');
const config = require('../../config');
const FormData = require('form-data');
const fs = require('fs');

class MLServiceClient {
  constructor() {
    this.baseUrl = config.ML_SERVICE_URL;
    this.client = axios.create({
      baseURL: this.baseUrl,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  async analyzeSkinType(imageBuffer, filename) {
    try {
      const formData = new FormData();
      formData.append('image', imageBuffer, filename);

      const response = await axios.post(
        `${this.baseUrl}/analyze/skin-type`,
        formData,
        {
          headers: formData.getHeaders(),
          timeout: 30000,
        }
      );

      return response.data;
    } catch (error) {
      console.error('ML Service Error (Skin Type):', error.message);
      throw new Error('ML Service unavailable for skin type analysis');
    }
  }

  async analyzeDisease(imageBuffer, filename) {
    try {
      const formData = new FormData();
      formData.append('image', imageBuffer, filename);

      const response = await axios.post(
        `${this.baseUrl}/analyze/disease`,
        formData,
        {
          headers: formData.getHeaders(),
          timeout: 30000,
        }
      );

      return response.data;
    } catch (error) {
      console.error('ML Service Error (Disease):', error.message);
      throw new Error('ML Service unavailable for disease analysis');
    }
  }

  async healthCheck() {
    try {
      const response = await this.client.get('/health');
      return response.data;
    } catch (error) {
      return { status: 'unavailable', error: error.message };
    }
  }
}

module.exports = new MLServiceClient();
