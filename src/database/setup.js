const { Client } = require('pg');
const { execSync } = require('child_process');
const Config = require('../config/config');

async function setupDatabase() {
  // PostgreSQL connection for creating database
  const client = new Client({
    host: 'localhost',
    port: 5432,
    user: 'postgres',
    password: '1234'
  });

  try {
    await client.connect();
    console.log('✅ Connected to PostgreSQL server');

    // Check if database exists
    const res = await client.query(
      "SELECT 1 FROM pg_database WHERE datname = 'glowmance'"
    );

    if (res.rowCount === 0) {
      // Create database if it doesn't exist
      await client.query('CREATE DATABASE glowmance');
      console.log('✅ Database "glowmance" created');
    } else {
      console.log('ℹ️ Database "glowmance" already exists');
    }

    // Run migrations
    console.log('🔄 Running migrations...');
    execSync('npx sequelize-cli db:migrate', { stdio: 'inherit' });
    console.log('✅ Migrations completed');

    // Run seeders if specified
    if (process.argv.includes('--seed')) {
      console.log('🔄 Running seeders...');
      execSync('node src/database/seeders/seed.js', { stdio: 'inherit' });
      console.log('✅ Seeding completed');
    }

    console.log('✨ Database setup completed successfully');
  } catch (error) {
    console.error('❌ Error setting up database:', error);
    process.exit(1);
  } finally {
    await client.end();
  }
}

if (require.main === module) {
  setupDatabase()
    .then(() => process.exit(0))
    .catch(error => {
      console.error(error);
      process.exit(1);
    });
}

module.exports = setupDatabase;