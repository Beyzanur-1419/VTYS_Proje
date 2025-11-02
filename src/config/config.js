const dotenv = require('dotenv');
const path = require('path');

// Load environment variables from .env file
dotenv.config({ path: path.join(__dirname, '../../.env') });

class Config {
  static get PORT() {
    return process.env.PORT || 3000;
  }

  static get NODE_ENV() {
    return process.env.NODE_ENV || 'development';
  }

  static get DATABASE_URL() {
    return process.env.DATABASE_URL;
  }

  static get JWT_SECRET() {
    return process.env.JWT_SECRET;
  }

  static get JWT_EXPIRES_IN() {
    return process.env.JWT_EXPIRES_IN || '1h';
  }

  static get REFRESH_TOKEN_SECRET() {
    return process.env.REFRESH_TOKEN_SECRET;
  }

  static get REFRESH_TOKEN_EXPIRES_IN() {
    return process.env.REFRESH_TOKEN_EXPIRES_IN || '7d';
  }

  static get AWS_ACCESS_KEY_ID() {
    return process.env.AWS_ACCESS_KEY_ID;
  }

  static get AWS_SECRET_ACCESS_KEY() {
    return process.env.AWS_SECRET_ACCESS_KEY;
  }

  static get AWS_BUCKET_NAME() {
    return process.env.AWS_BUCKET_NAME;
  }

  static get AWS_REGION() {
    return process.env.AWS_REGION || 'eu-central-1';
  }

  static get MONGODB_URI() {
    return process.env.MONGODB_URI;
  }

  static get CORS_ORIGIN() {
    return process.env.CORS_ORIGIN || '*';
  }

  static get USE_REFRESH_TOKEN_COOKIE() {
    return process.env.USE_REFRESH_TOKEN_COOKIE === 'true' || false;
  }

  static get ML_SERVICE_URL() {
    return process.env.ML_SERVICE_URL || 'http://localhost:8000';
  }

  static get CLOUDINARY_CLOUD_NAME() {
    return process.env.CLOUDINARY_CLOUD_NAME;
  }

  static get CLOUDINARY_API_KEY() {
    return process.env.CLOUDINARY_API_KEY;
  }

  static get CLOUDINARY_API_SECRET() {
    return process.env.CLOUDINARY_API_SECRET;
  }

  static get STORAGE_PROVIDER() {
    // Priority: Cloudinary > S3 > Local
    if (this.CLOUDINARY_CLOUD_NAME) return 'cloudinary';
    if (this.AWS_ACCESS_KEY_ID) return 's3';
    return 'local';
  }
}

module.exports = Config;