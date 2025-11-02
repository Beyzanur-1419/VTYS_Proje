const express = require('express');
const router = express.Router();
const analysisHistoryController = require('../controllers/analysisHistoryController');
const { authenticateToken } = require('../middleware/auth');

// All routes require authentication
router.use(authenticateToken);

/**
 * GET /api/analysis-history
 * Get user's analysis history with pagination
 * Query params: limit, page, offset
 */
router.get('/', analysisHistoryController.getHistory.bind(analysisHistoryController));

/**
 * GET /api/analysis-history/stats/summary
 * Get user's analysis statistics
 */
router.get('/stats/summary', analysisHistoryController.getStatistics.bind(analysisHistoryController));

/**
 * GET /api/analysis-history/:id
 * Get a specific analysis by ID
 */
router.get('/:id', analysisHistoryController.getAnalysis.bind(analysisHistoryController));

/**
 * POST /api/analysis-history
 * Create a new analysis history record
 */
router.post('/', analysisHistoryController.createAnalysis.bind(analysisHistoryController));

/**
 * DELETE /api/analysis-history/:id
 * Delete an analysis history record
 */
router.delete('/:id', analysisHistoryController.deleteAnalysis.bind(analysisHistoryController));

module.exports = router;