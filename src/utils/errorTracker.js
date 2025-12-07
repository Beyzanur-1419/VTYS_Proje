const logger = require('../utils/logger');
const metrics = require('../config/metrics');

class ErrorTracker {
  constructor() {
    this.errors = [];
    this.maxErrors = 100; // Keep last 100 errors in memory
  }

  /**
   * Track an error
   */
  trackError(error, context = {}) {
    const errorInfo = {
      timestamp: new Date().toISOString(),
      message: error.message,
      stack: error.stack,
      type: error.name || 'Error',
      context: {
        route: context.route || 'unknown',
        method: context.method || 'unknown',
        userId: context.userId || 'anonymous',
        ...context
      }
    };

    // Add to in-memory store
    this.errors.unshift(errorInfo);
    if (this.errors.length > this.maxErrors) {
      this.errors.pop();
    }

    // Log error
    logger.error('Error tracked:', {
      message: error.message,
      route: context.route,
      userId: context.userId
    });

    // Record metric
    metrics.recordError(error.name || 'Error', context.route || 'unknown');

    return errorInfo;
  }

  /**
   * Get recent errors
   */
  getRecentErrors(limit = 10) {
    return this.errors.slice(0, limit);
  }

  /**
   * Get error statistics
   */
  getErrorStats() {
    const stats = {
      total: this.errors.length,
      byType: {},
      byRoute: {},
      last24Hours: 0
    };

    const oneDayAgo = new Date(Date.now() - 24 * 60 * 60 * 1000);

    this.errors.forEach(error => {
      // Count by type
      stats.byType[error.type] = (stats.byType[error.type] || 0) + 1;

      // Count by route
      const route = error.context.route;
      stats.byRoute[route] = (stats.byRoute[route] || 0) + 1;

      // Count last 24 hours
      if (new Date(error.timestamp) > oneDayAgo) {
        stats.last24Hours++;
      }
    });

    return stats;
  }

  /**
   * Clear errors
   */
  clearErrors() {
    this.errors = [];
    logger.info('Error history cleared');
  }
}

// Export singleton instance
module.exports = new ErrorTracker();
