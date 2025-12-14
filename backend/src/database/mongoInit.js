const connectMongo = require('../config/db.mongo');

const initMongo = async () => {
  try {
    await connectMongo();
  } catch (error) {
    console.warn('⚠️  MongoDB Connection Failed:', error.message);
    console.warn('⚠️  Server will continue without MongoDB. Some features may be unavailable.');
  }
};

module.exports = initMongo;
