const mongoose = require('mongoose');
const { Sequelize } = require('sequelize');
require('dotenv').config();

// Test MongoDB Connection
async function testMongoDB() {
  try {
    console.log('\n🔍 Testing MongoDB Connection...');
    console.log('MongoDB URI:', process.env.MONGODB_URI ? 'SET ✓' : 'NOT SET ✗');
    
    if (!process.env.MONGODB_URI) {
      throw new Error('MONGODB_URI is not defined in .env file');
    }

    await mongoose.connect(process.env.MONGODB_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });
    
    console.log('✅ MongoDB Connected Successfully');
    console.log('   Database:', mongoose.connection.db.databaseName);
    console.log('   Host:', mongoose.connection.host);
    
    await mongoose.connection.close();
    console.log('✅ MongoDB Connection Closed');
    return true;
  } catch (error) {
    console.error('❌ MongoDB Connection Failed:');
    console.error('   Error:', error.message);
    return false;
  }
}

// Test PostgreSQL Connection
async function testPostgreSQL() {
  try {
    console.log('\n🔍 Testing PostgreSQL Connection...');
    console.log('PostgreSQL URI:', process.env.DATABASE_URL ? 'SET ✓' : 'NOT SET ✗');
    
    if (!process.env.DATABASE_URL) {
      throw new Error('DATABASE_URL is not defined in .env file');
    }

    const sequelize = new Sequelize(process.env.DATABASE_URL, {
      dialect: 'postgres',
      logging: false,
      dialectOptions: {
        ssl: {
          require: true,
          rejectUnauthorized: false
        }
      }
    });

    await sequelize.authenticate();
    console.log('✅ PostgreSQL Connected Successfully');
    
    const [results] = await sequelize.query('SELECT version()');
    console.log('   Version:', results[0].version.split(' ')[0], results[0].version.split(' ')[1]);
    
    await sequelize.close();
    console.log('✅ PostgreSQL Connection Closed');
    return true;
  } catch (error) {
    console.error('❌ PostgreSQL Connection Failed:');
    console.error('   Error:', error.message);
    return false;
  }
}

// Run all tests
async function runTests() {
  console.log('═══════════════════════════════════════════════════════');
  console.log('           DATABASE CONNECTION TEST');
  console.log('═══════════════════════════════════════════════════════');
  
  const mongoResult = await testMongoDB();
  const postgresResult = await testPostgreSQL();
  
  console.log('\n═══════════════════════════════════════════════════════');
  console.log('                    SUMMARY');
  console.log('═══════════════════════════════════════════════════════');
  console.log('MongoDB:    ', mongoResult ? '✅ CONNECTED' : '❌ FAILED');
  console.log('PostgreSQL: ', postgresResult ? '✅ CONNECTED' : '❌ FAILED');
  console.log('═══════════════════════════════════════════════════════\n');
  
  process.exit(mongoResult && postgresResult ? 0 : 1);
}

runTests();
