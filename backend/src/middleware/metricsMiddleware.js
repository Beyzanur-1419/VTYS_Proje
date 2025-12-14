const metrics = require('../config/metrics');
const logger = require('../utils/logger');

/**
 * Metrics middleware to track HTTP requests
 */
const metricsMiddleware = (req, res, next) => {
  const start = Date.now();
  
  // Increment active requests
  metrics.incrementActiveRequests();

  // Store original end function
  const originalEnd = res.end;

  // Override end function to capture metrics
  res.end = function(...args) {
    // Calculate duration
    const duration = (Date.now() - start) / 1000; // Convert to seconds

    // Get route pattern (remove IDs and dynamic parts)
    const route = req.route 
      ? req.route.path 
      : req.path.replace(/\/[0-9a-f-]{36}/gi, '/:id'); // Replace UUIDs with :id

    // Record metrics
    metrics.recordHttpRequest(
      req.method,
      route,
      res.statusCode,
      duration
    );

    // Decrement active requests
    metrics.decrementActiveRequests();

    // Log slow requests
    if (duration > 2) {
      logger.warn(`Slow request: ${req.method} ${route} took ${duration.toFixed(2)}s`);
    }

    // Call original end
    originalEnd.apply(res, args);
  };

  next();
};

/**
 * Performance monitoring middleware
 */
const performanceMiddleware = require('response-time')((req, res, time) => {
  // Log response time in milliseconds
  const timeInSeconds = time / 1000;
  
  if (timeInSeconds > 1) {
    logger.warn(`Performance: ${req.method} ${req.path} - ${time.toFixed(2)}ms`);
  }
});

module.exports = {
  metricsMiddleware,
  performanceMiddleware
};
