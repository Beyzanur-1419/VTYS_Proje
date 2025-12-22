const dotenv = require('dotenv');
const path = require('path');

dotenv.config({ path: path.join(__dirname, '../../.env') });

module.exports = {
  PORT: 3000,
  NODE_ENV: process.env.NODE_ENV || 'development',

  // PostgreSQL
  POSTGRES_URI: process.env.DATABASE_URL,

  // MongoDB
  MONGO_URI: process.env.MONGODB_URI,

  // JWT
  JWT_SECRET: process.env.JWT_SECRET || 'secret_key',
  JWT_EXPIRES_IN: process.env.JWT_EXPIRES_IN || '1d',

  // AWS S3
  AWS_ACCESS_KEY_ID: process.env.AWS_ACCESS_KEY_ID,
  AWS_SECRET_ACCESS_KEY: process.env.AWS_SECRET_ACCESS_KEY,
  AWS_BUCKET_NAME: process.env.AWS_BUCKET_NAME,
  AWS_REGION: process.env.AWS_REGION,

  // Cloudinary
  CLOUDINARY_CLOUD_NAME: process.env.CLOUDINARY_CLOUD_NAME,
  CLOUDINARY_API_KEY: process.env.CLOUDINARY_API_KEY,
  CLOUDINARY_API_SECRET: process.env.CLOUDINARY_API_SECRET,

  // ML Service
  ML_SERVICE_URL: process.env.ML_SERVICE_URL || 'http://localhost:8000',

  // Product API
  USE_MOCK_PRODUCTS: process.env.USE_MOCK_PRODUCTS || 'true',
  PRODUCT_API_URL: process.env.PRODUCT_API_URL,
  PRODUCT_API_KEY: process.env.PRODUCT_API_KEY,

  // CORS
  CORS_ORIGIN: process.env.CORS_ORIGIN || '*'
};
