const BaseService = require("./BaseService");
const AnalysisHistory = require("../models/AnalysisHistory");
const { AppError } = require("../utils/errors");

/**
 * Analysis History Service
 * Handles CRUD operations for analysis history records
 */
class AnalysisHistoryService extends BaseService {
  constructor() {
    super(AnalysisHistory);
  }

  /**
   * Create a new analysis history record
   * @param {string} userId - User UUID
   * @param {string} imageId - MongoDB AIImage document ID
   * @param {Object} results - ML service analysis results
   * @returns {Promise<Object>} Created analysis history record
   */
  async createAnalysis(userId, imageId, results) {
    try {
      // Validate required fields
      if (!userId) {
        throw new AppError("User ID is required", 400);
      }

      if (!imageId) {
        throw new AppError("Image ID is required", 400);
      }

      if (!results || typeof results !== "object") {
        throw new AppError("Analysis results are required", 400);
      }

      // Validate results structure
      const validatedResults = this.validateResults(results);

      // Create analysis history record
      const analysis = await this.create({
        userId,
        imageId,
        results: validatedResults,
        timestamp: new Date(),
      });

      return analysis;
    } catch (error) {
      if (error instanceof AppError) {
        throw error;
      }
      console.error("Error creating analysis history:", error);
      throw new AppError(
        `Failed to create analysis history: ${error.message}`,
        500
      );
    }
  }

  /**
   * Validate and normalize analysis results
   * @param {Object} results - Raw analysis results from ML service
   * @returns {Object} Validated and normalized results
   */
  validateResults(results) {
    // Ensure results have required structure
    const validated = {
      confidence: results.confidence || 0,
      conditions: Array.isArray(results.conditions) ? results.conditions : [],
      recommendations: Array.isArray(results.recommendations)
        ? results.recommendations
        : [],
      metadata: results.metadata || {},
    };

    // Validate confidence (0-1 range)
    if (
      typeof validated.confidence !== "number" ||
      validated.confidence < 0 ||
      validated.confidence > 1
    ) {
      validated.confidence = 0;
    }

    // Add processing timestamp if not present
    if (!validated.metadata.processed_at) {
      validated.metadata.processed_at = new Date().toISOString();
    }

    return validated;
  }

  /**
   * Get user's analysis history with pagination and metadata
   * @param {string} userId - User UUID
   * @param {Object} options - Query options (limit, offset, etc.)
   * @returns {Promise<Object>} Analysis history with pagination metadata
   */
  async getUserAnalysisHistory(userId, options = {}) {
    try {
      const limit = Math.min(options.limit || 50, 100); // Max 100 records
      const offset = options.offset || 0;

      // Get total count
      const totalCount = await this.model.count({
        where: { userId },
      });

      // Get analyses
      const analyses = await this.model.findAll({
        where: { userId },
        order: [["timestamp", "DESC"]],
        limit,
        offset,
      });

      // Calculate pagination metadata
      const totalPages = Math.ceil(totalCount / limit);
      const currentPage = Math.floor(offset / limit) + 1;
      const hasNextPage = offset + limit < totalCount;
      const hasPreviousPage = offset > 0;

      return {
        analyses,
        pagination: {
          total: totalCount,
          limit,
          offset,
          currentPage,
          totalPages,
          hasNextPage,
          hasPreviousPage,
        },
      };
    } catch (error) {
      console.error("Error getting user analysis history:", error);
      throw new AppError(
        `Failed to get analysis history: ${error.message}`,
        500
      );
    }
  }

  /**
   * Get user's analysis history as array (for backward compatibility)
   * @param {string} userId - User UUID
   * @param {Object} options - Query options (limit, offset, etc.)
   * @returns {Promise<Array>} Array of analysis history records
   */
  async getUserAnalysisHistoryArray(userId, options = {}) {
    try {
      const limit = Math.min(options.limit || 50, 100); // Max 100 records
      const offset = options.offset || 0;

      const analyses = await this.model.findAll({
        where: { userId },
        order: [["timestamp", "DESC"]],
        limit,
        offset,
      });

      return analyses;
    } catch (error) {
      console.error("Error getting user analysis history:", error);
      throw new AppError(
        `Failed to get analysis history: ${error.message}`,
        500
      );
    }
  }

  /**
   * Get analysis by ID with user validation
   * @param {string} id - Analysis UUID
   * @param {string} userId - User UUID
   * @returns {Promise<Object>} Analysis history record
   */
  async getAnalysisById(id, userId) {
    try {
      const analysis = await this.model.findOne({
        where: { id, userId },
      });

      if (!analysis) {
        throw new AppError("Analysis not found", 404);
      }

      return analysis;
    } catch (error) {
      if (error instanceof AppError) {
        throw error;
      }
      console.error("Error getting analysis by ID:", error);
      throw new AppError(
        `Failed to get analysis: ${error.message}`,
        500
      );
    }
  }

  /**
   * Get analysis by image ID
   * @param {string} imageId - MongoDB AIImage document ID
   * @param {string} userId - User UUID (optional, for validation)
   * @returns {Promise<Object>} Analysis history record
   */
  async getAnalysisByImageId(imageId, userId = null) {
    try {
      const where = { imageId };
      if (userId) {
        where.userId = userId;
      }

      const analysis = await this.model.findOne({
        where,
        order: [["timestamp", "DESC"]],
      });

      if (!analysis) {
        throw new AppError("Analysis not found", 404);
      }

      return analysis;
    } catch (error) {
      if (error instanceof AppError) {
        throw error;
      }
      console.error("Error getting analysis by image ID:", error);
      throw new AppError(
        `Failed to get analysis: ${error.message}`,
        500
      );
    }
  }

  /**
   * Delete analysis history
   * @param {string} id - Analysis UUID
   * @param {string} userId - User UUID (for validation)
   * @returns {Promise<boolean>} Success status
   */
  async deleteAnalysis(id, userId) {
    try {
      // Verify ownership
      const analysis = await this.getAnalysisById(id, userId);

      await analysis.destroy();

      return true;
    } catch (error) {
      if (error instanceof AppError) {
        throw error;
      }
      console.error("Error deleting analysis:", error);
      throw new AppError(
        `Failed to delete analysis: ${error.message}`,
        500
      );
    }
  }

  /**
   * Get analysis statistics for a user
   * @param {string} userId - User UUID
   * @returns {Promise<Object>} Analysis statistics
   */
  async getUserStatistics(userId) {
    try {
      const analyses = await this.model.findAll({
        where: { userId },
        attributes: ["results", "timestamp"],
      });

      const totalAnalyses = analyses.length;
      const conditions = {};
      const recommendations = {};
      let totalConfidence = 0;

      analyses.forEach((analysis) => {
        const { results } = analysis;
        if (results.confidence) {
          totalConfidence += results.confidence;
        }
        if (Array.isArray(results.conditions)) {
          results.conditions.forEach((condition) => {
            conditions[condition] = (conditions[condition] || 0) + 1;
          });
        }
        if (Array.isArray(results.recommendations)) {
          results.recommendations.forEach((recommendation) => {
            recommendations[recommendation] =
              (recommendations[recommendation] || 0) + 1;
          });
        }
      });

      return {
        totalAnalyses,
        averageConfidence:
          totalAnalyses > 0 ? totalConfidence / totalAnalyses : 0,
        topConditions: Object.entries(conditions)
          .sort(([, a], [, b]) => b - a)
          .slice(0, 5)
          .map(([condition, count]) => ({ condition, count })),
        topRecommendations: Object.entries(recommendations)
          .sort(([, a], [, b]) => b - a)
          .slice(0, 5)
          .map(([recommendation, count]) => ({ recommendation, count })),
      };
    } catch (error) {
      console.error("Error getting user statistics:", error);
      throw new AppError(
        `Failed to get statistics: ${error.message}`,
        500
      );
    }
  }
}

module.exports = AnalysisHistoryService;
