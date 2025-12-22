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
      formData.append('file', imageBuffer, filename); // Python expects 'file'

      const response = await axios.post(
        `${this.baseUrl}/predict`,
        formData,
        {
          headers: {
            ...formData.getHeaders(),
          },
          timeout: 30000,
        }
      );

      // Map Python response to what controller expects
      const data = response.data.data;
      return {
        skin_type: data.skinType || 'Normal', // Map 'skinType' to 'skin_type'
        confidence: 0.9, // Mock confidence
        ...data // include other data if needed
      };
    } catch (error) {
      console.error('ML Service Error (Skin Type):', error.message);
      // Fallback or rethrow based on preference. Controller handles throw.
      throw new Error('ML Service unavailable for skin type analysis');
    }
  }

  async analyzeDisease(imageBuffer, filename) {
    try {
      const formData = new FormData();
      formData.append('file', imageBuffer, filename); // Python expects 'file'

      const response = await axios.post(
        `${this.baseUrl}/predict`,
        formData,
        {
          headers: {
            ...formData.getHeaders(),
          },
          timeout: 30000,
        }
      );

      const data = response.data.data;
      return {
        disease: data.disease_prediction || 'Healthy', // Map 'disease_prediction' to 'disease'
        confidence: 0.9,
        // Map detailed flags for controller's immediate use if needed, 
        // though controller extracts from result_json too.
        ...data
      };
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
