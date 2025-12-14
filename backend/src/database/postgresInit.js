const sequelize = require('../config/db.postgres');
const User = require('../models/User');
const Product = require('../models/Product');
const AnalysisHistory = require('../models/AnalysisHistory');

const connectPostgres = async () => {
  try {
    await sequelize.authenticate();
    console.log('✅ PostgreSQL Connected Successfully');
    
    // Sync models (alter: true updates tables without dropping)
    await sequelize.sync({ alter: true });
    console.log('✅ PostgreSQL Models Synced');
  } catch (error) {
    console.warn('⚠️  PostgreSQL Connection Failed:', error.message);
    console.warn('⚠️  Server will continue without PostgreSQL. Some features may be unavailable.');
    console.warn('⚠️  To enable PostgreSQL, make sure it is running and check your .env configuration.');
    // Don't exit - allow server to continue without PostgreSQL
  }
};

module.exports = connectPostgres;
