const redisClient = require('../config/redis');
const logger = require('../utils/logger');

/**
 * Cache middleware for Express routes
 * @param {number} ttl - Time to live in seconds (default: 300 = 5 minutes)
 * @param {function} keyGenerator - Optional custom key generator function
 */
const cache = (ttl = 300, keyGenerator = null) => {
  return async (req, res, next) => {
    // Skip caching if Redis is not connected
    if (!redisClient.isConnected) {
      return next();
    }

    try {
      // Generate cache key
      const cacheKey = keyGenerator 
        ? keyGenerator(req)
        : `cache:${req.method}:${req.originalUrl}:${req.user?.id || 'anonymous'}`;

      // Try to get from cache
      const cachedData = await redisClient.get(cacheKey);
      
      if (cachedData) {
        logger.debug(`Cache HIT: ${cacheKey}`);
        return res.status(200).json({
          ...cachedData,
          cached: true,
          cacheKey
        });
      }

      logger.debug(`Cache MISS: ${cacheKey}`);

      // Store original res.json function
      const originalJson = res.json.bind(res);

      // Override res.json to cache the response
      res.json = function(data) {
        // Only cache successful responses
        if (res.statusCode === 200 && data.success !== false) {
          redisClient.set(cacheKey, data, ttl).catch(err => {
            logger.error('Failed to cache response:', err);
          });
        }
        
        // Call original json function
        return originalJson(data);
      };

      next();
    } catch (error) {
      logger.error('Cache middleware error:', error);
      next();
    }
  };
};

/**
 * Clear cache by pattern
 */
const clearCache = (pattern) => {
  return async (req, res, next) => {
    try {
      await redisClient.delPattern(pattern);
      logger.info(`Cache cleared: ${pattern}`);
    } catch (error) {
      logger.error('Clear cache error:', error);
    }
    next();
  };
};

/**
 * Clear user-specific cache
 */
const clearUserCache = async (userId) => {
  try {
    await redisClient.delPattern(`cache:*:*${userId}*`);
    logger.info(`User cache cleared: ${userId}`);
  } catch (error) {
    logger.error('Clear user cache error:', error);
  }
};

module.exports = {
  cache,
  clearCache,
  clearUserCache
};
