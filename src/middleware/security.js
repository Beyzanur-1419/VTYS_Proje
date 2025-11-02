const rateLimit = require('express-rate-limit');
const helmet = require('helmet');
const xss = require('xss-clean');
const hpp = require('hpp');
const mongoSanitize = require('express-mongo-sanitize');

const securityMiddleware = {
  // Rate limiting
  rateLimiter: rateLimit({
    windowMs: 15 * 60 * 1000, // 15 dakika
    max: 100, // IP başına max istek
    message: 'Çok fazla istek yapıldı, lütfen 15 dakika sonra tekrar deneyin.'
  }),

  // Auth endpoints için özel rate limiting
  authLimiter: rateLimit({
    windowMs: 60 * 60 * 1000, // 1 saat
    max: 5, // IP başına max 5 başarısız giriş
    message: 'Çok fazla başarısız giriş denemesi, lütfen 1 saat sonra tekrar deneyin.'
  }),

  // Upload endpoints için özel rate limiting
  uploadLimiter: rateLimit({
    windowMs: 60 * 60 * 1000, // 1 saat
    max: 10, // IP başına max 10 upload
    message: 'Çok fazla dosya yükleme denemesi, lütfen 1 saat sonra tekrar deneyin.'
  }),

  // Security headers
  setupSecurityHeaders: (app) => {
    // Basic security headers
    app.use(helmet());

    // XSS protection
    app.use(xss());

    // Parameter pollution protection
    app.use(hpp());

    // NoSQL injection protection
    app.use(mongoSanitize());

    // CORS headers
    app.use((req, res, next) => {
      res.setHeader('Access-Control-Allow-Origin', Config.CORS_ORIGIN);
      res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
      res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
      res.setHeader('Access-Control-Allow-Credentials', true);
      next();
    });

    // Content Security Policy
    app.use(helmet.contentSecurityPolicy({
      directives: {
        defaultSrc: ["'self'"],
        imgSrc: ["'self'", 'data:', 'blob:', Config.UPLOAD_DOMAIN],
        scriptSrc: ["'self'"],
        styleSrc: ["'self'", "'unsafe-inline'"],
        fontSrc: ["'self'"],
        objectSrc: ["'none'"],
        upgradeInsecureRequests: []
      }
    }));
  }
};

module.exports = securityMiddleware;