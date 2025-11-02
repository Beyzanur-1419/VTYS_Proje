const axios = require("axios");
const Config = require("../config/config");
const FormData = require("form-data");
const fs = require("fs");
const { AppError } = require("../utils/errors");

class ModelService {
  constructor() {
    this.apiClient = axios.create({
      baseURL: Config.MODEL_API_URL,
      headers: {
        Authorization: `Bearer ${Config.MODEL_API_KEY}`,
      },
    });
  }

  async analyzeImage(imagePath, imageUrl) {
    try {
      let response;

      if (imageUrl) {
        // URL ile analiz
        response = await this.apiClient.post("/analyze", {
          imageUrl: imageUrl,
        });
      } else {
        // Dosya ile analiz
        const formData = new FormData();
        formData.append("image", fs.createReadStream(imagePath));

        response = await this.apiClient.post("/analyze", formData, {
          headers: {
            ...formData.getHeaders(),
          },
        });
      }

      if (!response.data || response.data.error) {
        throw new AppError(response.data?.error || "Analysis failed", 400);
      }

      return {
        conditions: response.data.conditions || [],
        confidence: response.data.confidence,
        recommendations: response.data.recommendations || [],
        rawResult: response.data,
      };
    } catch (error) {
      if (error.isOperational) {
        throw error;
      }

      if (error.response) {
        throw new AppError(
          error.response.data?.message || "Model API error",
          error.response.status || 500
        );
      }

      throw new AppError("Failed to analyze image", 500);
    }
  }

  async getModelInfo() {
    try {
      const response = await this.apiClient.get("/info");
      return response.data;
    } catch (error) {
      console.error("Error getting model info:", error);
      return null;
    }
  }
}

module.exports = new ModelService();
