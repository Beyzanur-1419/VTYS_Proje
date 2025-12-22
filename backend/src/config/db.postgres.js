const { Sequelize } = require('sequelize');
const config = require('./index');

const sequelize = new Sequelize(config.POSTGRES_URI, {
  dialect: 'postgres',
  logging: console.log, // Enable logging to see SQL queries
  dialectOptions: {
    ssl: process.env.DB_SSL === 'true' ? {
      require: true,
      rejectUnauthorized: false
    } : false
  },
  pool: {
    max: 5,
    min: 0,
    acquire: 30000,
    idle: 10000
  }
});

module.exports = sequelize;
