const sequelize = require('../config/db.postgres');
const User = require('../models/User');
const Product = require('../models/Product');
const AnalysisHistory = require('../models/AnalysisHistory');
const Log = require('../models/Log');
const Medication = require('../models/Medication');
const SkinHistory = require('../models/SkinHistory');
const Recommendation = require('../models/Recommendation');
const Image = require('../models/Image');

const connectPostgres = async () => {
  try {
    await sequelize.authenticate();
    console.log('✅ PostgreSQL Connected Successfully');

    // Sync models (Disabled auto-alter to prevent startup crashes/conflicts)
    // We use manual scripts (src/scripts/sync_schema_v3_erd.js) for schema updates.
    await sequelize.sync({});
    console.log('✅ PostgreSQL Models Synced (No-Alter Mode)');
  } catch (error) {
    console.warn('⚠️  PostgreSQL Connection Failed:', error.message);
    console.warn('⚠️  Server will continue without PostgreSQL. Some features may be unavailable.');
    console.warn('⚠️  To enable PostgreSQL, make sure it is running and check your .env configuration.');
    // Don't exit - allow server to continue without PostgreSQL
  }
};

module.exports = connectPostgres;
