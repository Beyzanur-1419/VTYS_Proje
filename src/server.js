const app = require('./app');
const Config = require('./config/config');
const { initializeDatabases } = require('./database');

// Initialize database connections
async function startServer() {
  try {
    // Connect to databases (optional - if databases are not available, server can still start)
    // Backend API can work without databases for testing
    try {
      const dbConnected = await initializeDatabases();
      if (dbConnected) {
        console.log('✅ Database connections established');
      } else {
        console.warn('⚠️  Database connections failed - API will run in limited mode');
        console.warn('⚠️  Some endpoints may not work without database connections');
      }
    } catch (dbError) {
      console.warn('⚠️  Database initialization error:', dbError.message);
      console.warn('⚠️  Server will start but database features will be unavailable');
    }

    // Start server
    const server = app.listen(Config.PORT, () => {
      console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
      console.log(`🚀 GLOWMANCE Backend API Server`);
      console.log(`━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━`);
      console.log(`📡 Server running on: http://localhost:${Config.PORT}`);
      console.log(`🌍 Environment: ${Config.NODE_ENV}`);
      console.log(`📚 API Documentation: http://localhost:${Config.PORT}/api/docs`);
      console.log(`🏥 Health Check: http://localhost:${Config.PORT}/health`);
      console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
    });

    return server;
  } catch (error) {
    console.error('❌ Error starting server:', error);
    process.exit(1);
  }
}

const server = startServer();

// Handle unhandled rejections
process.on('unhandledRejection', (err) => {
  console.error('UNHANDLED REJECTION! 💥 Shutting down...');
  console.error(err.name, err.message);
  server.close(() => {
    process.exit(1);
  });
});

// Handle uncaught exceptions
process.on('uncaughtException', (err) => {
  console.error('UNCAUGHT EXCEPTION! 💥 Shutting down...');
  console.error(err.name, err.message);
  process.exit(1);
});