const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const rateLimit = require('express-rate-limit');
const config = require('./config');
const errorHandler = require('./middleware/errorHandler');
const { swaggerUi, swaggerSpec } = require('./config/swagger');
const { metricsMiddleware, performanceMiddleware } = require('./middleware/metricsMiddleware');

// Rate Limiter Configuration
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // Limit each IP to 100 requests per windowMs
  standardHeaders: true, // Return rate limit info in the `RateLimit-*` headers
  legacyHeaders: false, // Disable the `X-RateLimit-*` headers
  message: {
    success: false,
    message: 'Too many requests from this IP, please try again after 15 minutes',
  },
});

const authLimiter = rateLimit({
  windowMs: 60 * 60 * 1000, // 1 hour
  max: 10, // Limit each IP to 10 login/register requests per hour
  message: {
    success: false,
    message: 'Too many login attempts, please try again after an hour',
  },
});

// Import Routes
const authRoutes = require('./routes/authRoutes');
const userRoutes = require('./routes/userRoutes');
const productRoutes = require('./routes/productRoutes');
const analysisRoutes = require('./routes/analysisRoutes');
const uploadRoutes = require('./routes/uploadRoutes');
const inciRoutes = require('./routes/inciRoutes');
const monitoringRoutes = require('./routes/monitoringRoutes');

const app = express();

// Middleware
app.use(helmet()); // Set security headers
app.use(cors({ origin: config.CORS_ORIGIN, credentials: true })); // Enable CORS with credentials
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(limiter); // Apply global rate limiter
app.use(metricsMiddleware); // Track all requests
app.use(performanceMiddleware); // Monitor performance

if (config.NODE_ENV === 'development') {
  app.use(morgan('dev'));
}

// Serve static files
app.use('/uploads', express.static('uploads'));

// Swagger Documentation
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));

// Routes
app.use('/api/v1/auth', authLimiter, authRoutes); // Apply stricter limit to auth routes
app.use('/api/v1/user', userRoutes);
app.use('/api/v1/products', productRoutes);
app.use('/api/v1/analysis', analysisRoutes);
app.use('/api/v1/upload', uploadRoutes);
app.use('/api/v1', inciRoutes); // INCI ingredient lookup
app.use('/api/v1', monitoringRoutes); // Monitoring endpoints

// Health Check
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'ok', timestamp: new Date() });
});

// API Info
app.get('/', (req, res) => {
  res.json({
    message: 'GLOWMANCE API',
    version: '1.0.0',
    documentation: '/api-docs',
  });
});

// 404 Handler
app.use((req, res, next) => {
  res.status(404).json({
    success: false,
    message: 'Not Found',
  });
});

// Error Handler
app.use(errorHandler);

module.exports = app;
