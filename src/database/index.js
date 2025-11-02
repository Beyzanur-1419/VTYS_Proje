const { connectPostgres, testConnection: testPostgres } = require("./postgres");
const { connectMongoDB } = require("./mongodb");

async function initializeDatabases() {
  try {
    // Try to connect to databases (non-blocking)
    // If databases are not available, return false but don't throw error
    // This allows the API server to start even if databases are not ready
    
    const [postgresConnected, mongoConnected] = await Promise.allSettled([
      connectPostgres(),
      connectMongoDB(),
    ]);

    const postgresStatus = postgresConnected.status === 'fulfilled' && postgresConnected.value;
    const mongoStatus = mongoConnected.status === 'fulfilled' && mongoConnected.value;

    if (postgresStatus) {
      console.log('✅ PostgreSQL connection established');
    } else {
      console.warn('⚠️  PostgreSQL connection failed:', postgresConnected.reason?.message || 'Connection refused');
    }

    if (mongoStatus) {
      console.log('✅ MongoDB connection established');
    } else {
      console.warn('⚠️  MongoDB connection failed:', mongoConnected.reason?.message || 'Connection refused');
    }

    if (postgresStatus && mongoStatus) {
      console.log('✅ All database connections established');
      return true;
    } else {
      console.warn('⚠️  Some database connections failed - API will work in limited mode');
      return false;
    }
  } catch (error) {
    console.error("Database initialization error:", error.message);
    return false;
  }
}

module.exports = {
  initializeDatabases,
};
