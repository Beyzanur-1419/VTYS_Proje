const AnalysisHistoryService = require('../services/AnalysisHistoryService');
const AIImageService = require('../services/AIImageService');
const { AppError } = require('../utils/errors');

/**
 * Analysis History Controller
 * Handles HTTP requests for analysis history operations
 */
class AnalysisHistoryController {
  constructor() {
    this.analysisService = new AnalysisHistoryService();
    this.aiImageService = new AIImageService();
  }

  /**
   * GET /api/analysis-history
   * Get user's analysis history with pagination
   * 
   * Query Parameters:
   * - limit: Number of records per page (default: 20, max: 100)
   * - page: Page number (default: 1)
   * - offset: Offset for pagination (alternative to page)
   * 
   * Response:
   * {
   *   status: 'success',
   *   data: {
   *     analyses: Array<AnalysisHistory>,
   *     pagination: {
   *       total: number,
   *       limit: number,
   *       offset: number,
   *       currentPage: number,
   *       totalPages: number,
   *       hasNextPage: boolean,
   *       hasPreviousPage: boolean
   *     }
   *   }
   * }
   */
  async getHistory(req, res, next) {
    try {
      const userId = req.user.id;

      // Parse query parameters
      const limit = Math.min(parseInt(req.query.limit) || 20, 100); // Default 20, max 100
      const page = parseInt(req.query.page) || 1;
      const offset = req.query.offset !== undefined 
        ? parseInt(req.query.offset) 
        : (page - 1) * limit;

      // Validate parameters
      if (limit < 1) {
        throw new AppError('Limit must be greater than 0', 400);
      }
      if (offset < 0) {
        throw new AppError('Offset must be greater than or equal to 0', 400);
      }

      // Get analysis history with pagination
      const { analyses, pagination } = await this.analysisService.getUserAnalysisHistory(
        userId,
        { limit, offset }
      );

      // Enrich analyses with image information from MongoDB
      const enrichedAnalyses = await Promise.all(
        analyses.map(async (analysis) => {
          try {
            // Get image information from MongoDB
            const image = await this.aiImageService.findById(analysis.imageId);
            
            return {
              ...analysis.toJSON(),
              image: image ? {
                id: image._id,
                url: image.url,
                cloudUrl: image.cloudUrl,
                cloudProvider: image.cloudProvider,
                originalName: image.originalName,
                mimeType: image.mimeType,
                size: image.size,
                width: image.width,
                height: image.height,
                format: image.format,
              } : null,
            };
          } catch (error) {
            console.error(`Error fetching image for analysis ${analysis.id}:`, error.message);
            // Continue without image if fetch fails
            return {
              ...analysis.toJSON(),
              image: null,
            };
          }
        })
      );

      res.json({
        status: 'success',
        data: {
          analyses: enrichedAnalyses,
          pagination,
        },
      });
    } catch (err) {
      next(err);
    }
  }

  async getAnalysis(req, res, next) {
    try {
      const { id } = req.params;
      const userId = req.user.id;

      const analysis = await this.analysisService.getAnalysisById(id, userId);

      res.json({
        status: 'success',
        data: {
          analysis
        }
      });
    } catch (err) {
      if (err.message === 'Analysis not found') {
        return next(new AppError('Analysis not found', 404));
      }
      next(err);
    }
  }

  /**
   * POST /api/analysis-history
   * Create a new analysis history record
   * 
   * Body:
   * {
   *   imageId: string (MongoDB AIImage document ID),
   *   results: {
   *     confidence: number,
   *     conditions: Array<string>,
   *     recommendations: Array<string>,
   *     metadata: Object
   *   }
   * }
   * 
   * Response:
   * {
   *   status: 'success',
   *   data: {
   *     analysis: AnalysisHistory
   *   }
   * }
   */
  async createAnalysis(req, res, next) {
    try {
      const { imageId, results } = req.body;
      const userId = req.user.id;

      // Validate required fields
      if (!imageId) {
        throw new AppError('imageId is required', 400);
      }
      if (!results || typeof results !== 'object') {
        throw new AppError('results is required and must be an object', 400);
      }

      // Create analysis
      const analysis = await this.analysisService.createAnalysis(
        userId,
        imageId,
        results
      );

      // Enrich with image information
      let image = null;
      try {
        image = await this.aiImageService.findById(imageId);
      } catch (error) {
        console.error(`Error fetching image for new analysis:`, error.message);
      }

      const enrichedAnalysis = {
        ...analysis.toJSON(),
        image: image ? {
          id: image._id,
          url: image.url,
          cloudUrl: image.cloudUrl,
          cloudProvider: image.cloudProvider,
          originalName: image.originalName,
          mimeType: image.mimeType,
          size: image.size,
          width: image.width,
          height: image.height,
          format: image.format,
        } : null,
      };

      res.status(201).json({
        status: 'success',
        data: {
          analysis: enrichedAnalysis,
        },
      });
    } catch (err) {
      next(err);
    }
  }

  /**
   * DELETE /api/analysis-history/:id
   * Delete an analysis history record
   * 
   * Response:
   * {
   *   status: 'success',
   *   message: 'Analysis deleted successfully'
   * }
   */
  async deleteAnalysis(req, res, next) {
    try {
      const { id } = req.params;
      const userId = req.user.id;

      await this.analysisService.deleteAnalysis(id, userId);

      res.json({
        status: 'success',
        message: 'Analysis deleted successfully',
      });
    } catch (err) {
      if (err instanceof AppError && err.message === 'Analysis not found') {
        return next(new AppError('Analysis not found', 404));
      }
      next(err);
    }
  }

  /**
   * GET /api/analysis-history/stats/summary
   * Get user's analysis statistics summary
   * 
   * Response:
   * {
   *   status: 'success',
   *   data: {
   *     statistics: {
   *       totalAnalyses: number,
   *       averageConfidence: number,
   *       topConditions: Array<{ condition: string, count: number }>,
   *       topRecommendations: Array<{ recommendation: string, count: number }>
   *     }
   *   }
   * }
   */
  async getStatistics(req, res, next) {
    try {
      const userId = req.user.id;

      const statistics = await this.analysisService.getUserStatistics(userId);

      res.json({
        status: 'success',
        data: {
          statistics,
        },
      });
    } catch (err) {
      next(err);
    }
  }
}

module.exports = new AnalysisHistoryController();