const app = require('./app');
const config = require('./config');
const connectPostgres = require('./database/postgresInit');
const initMongo = require('./database/mongoInit');
const logger = require('./utils/logger');

const startServer = async () => {
  try {
    // Connect to Databases (Optional for dev startup)
    try {
      await connectPostgres();
    } catch (dbError) {
      logger.warn('⚠️  PostgreSQL Connection Failed:', dbError.message);
      logger.warn('⚠️  Server will continue without PostgreSQL. Some features may be unavailable.');
    }

    try {
      await initMongo();
    } catch (dbError) {
      logger.warn('⚠️  MongoDB Connection Failed:', dbError.message);
      logger.warn('⚠️  Server will continue without MongoDB. Some features may be unavailable.');
    }

    // Connect to Redis (optional)
    try {
      const redisClient = require('./config/redis');
      await redisClient.connect();
    } catch (redisError) {
      logger.warn('⚠️  Redis Connection Failed:', redisError.message);
      logger.warn('⚠️  Server will continue without Redis cache.');
    }

    app.listen(config.PORT, () => {
      logger.info(`Server running in ${config.NODE_ENV} mode on port ${config.PORT}`);
    });
  } catch (error) {
    logger.error('Failed to start server:', error);
    process.exit(1);
  }
};

startServer();
