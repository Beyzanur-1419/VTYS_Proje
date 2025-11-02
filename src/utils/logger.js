const winston = require('winston');
const Config = require('../config/config');

const logger = winston.createLogger({
  level: Config.NODE_ENV === 'production' ? 'info' : 'debug',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.json()
  ),
  transports: [
    // Console logging
    new winston.transports.Console({
      format: winston.format.combine(
        winston.format.colorize(),
        winston.format.simple()
      )
    }),
    // Error log file
    new winston.transports.File({ 
      filename: 'logs/error.log', 
      level: 'error',
      maxsize: 5242880, // 5MB
      maxFiles: 5
    }),
    // Combined log file
    new winston.transports.File({ 
      filename: 'logs/combined.log',
      maxsize: 5242880, // 5MB
      maxFiles: 5
    })
  ],
  exceptionHandlers: [
    new winston.transports.File({ filename: 'logs/exceptions.log' })
  ]
});

// Request logging middleware
const requestLogger = (req, res, next) => {
  const start = Date.now();

  res.on('finish', () => {
    const duration = Date.now() - start;
    logger.info({
      method: req.method,
      url: req.url,
      status: res.statusCode,
      duration: `${duration}ms`,
      userAgent: req.get('user-agent'),
      ip: req.ip
    });
  });

  next();
};

// Error logging middleware
const errorLogger = (err, req, res, next) => {
  logger.error({
    error: {
      message: err.message,
      stack: err.stack,
      statusCode: err.statusCode
    },
    request: {
      method: req.method,
      url: req.url,
      body: req.body,
      user: req.user ? req.user.id : null
    }
  });

  next(err);
};

// Performance monitoring
const performanceMonitor = (req, res, next) => {
  const start = process.hrtime();

  res.on('finish', () => {
    const [seconds, nanoseconds] = process.hrtime(start);
    const duration = seconds * 1000 + nanoseconds / 1000000;

    if (duration > 1000) { // 1 saniyeden uzun süren istekler
      logger.warn({
        type: 'performance_alert',
        method: req.method,
        url: req.url,
        duration: `${duration.toFixed(2)}ms`
      });
    }
  });

  next();
};

module.exports = {
  logger,
  requestLogger,
  errorLogger,
  performanceMonitor
};