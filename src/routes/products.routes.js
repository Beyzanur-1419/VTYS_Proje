const express = require('express');
const router = express.Router();
const { authenticateToken } = require('../middleware/auth');
const ProductScraperService = require('../services/ProductScraperService');
const RecommendationServiceClass = require('../services/RecommendationService');
const RecommendationService = new RecommendationServiceClass();

// Manuel ürün tarama endpoint'i (sadece test için)
router.post('/scrape', authenticateToken, async (req, res, next) => {
  try {
    const { category, condition } = req.body;
    if (!category || !condition) {
      return res.status(400).json({
        error: 'category ve condition parametreleri gerekli'
      });
    }

    const products = await ProductScraperService.scrapeProducts(category, condition);
    res.json(products);
  } catch (error) {
    next(error);
  }
});

/**
 * GET /api/products/recommendations
 * Get personalized product recommendations based on user's analysis history
 * 
 * Query Parameters:
 * - limit: Number of recommendations (default: 10, max: 50)
 * - includeTrending: Include trending products if personalized are less (default: true)
 * 
 * Response:
 * {
 *   recommendations: Array<Product>,
 *   source: 'personalized' | 'trending',
 *   message: string,
 *   basedOnHistory: boolean,
 *   insights?: {
 *     conditions: Array<string>,
 *     recommendations: Array<string>,
 *     totalAnalyses: number
 *   }
 * }
 */
router.get('/recommendations', authenticateToken, async (req, res, next) => {
  try {
    const limit = Math.min(parseInt(req.query.limit) || 10, 50); // Max 50
    const includeTrending = req.query.includeTrending !== 'false';

    const recommendations = await RecommendationService.getRecommendations(
      req.user.id,
      {
        limit,
        includeTrending,
      }
    );

    res.json({
      status: 'success',
      data: recommendations,
    });
  } catch (error) {
    next(error);
  }
});

/**
 * GET /api/products/trending
 * Get trending/popular products
 * 
 * Query Parameters:
 * - limit: Number of trending products (default: 10, max: 50)
 * 
 * Response:
 * {
 *   status: 'success',
 *   data: Array<Product>
 * }
 */
router.get('/trending', async (req, res, next) => {
  try {
    const limit = Math.min(parseInt(req.query.limit) || 10, 50); // Max 50

    const trending = await RecommendationService.getTrendingProducts({ limit });

    res.json({
      status: 'success',
      data: trending,
    });
  } catch (error) {
    next(error);
  }
});

module.exports = router;