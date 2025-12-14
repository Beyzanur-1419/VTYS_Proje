const { Sequelize } = require('sequelize');
const config = require('./index');

const sequelize = new Sequelize(config.POSTGRES_URI, {
  dialect: 'postgres',
  logging: config.NODE_ENV === 'development' ? console.log : false,
  dialectOptions: {
    ssl: {
      require: true,
      rejectUnauthorized: false // Required for Neon/AWS SSL connections
    }
  },
  pool: {
    max: 5,
    min: 0,
    acquire: 30000,
    idle: 10000
  }
});

module.exports = sequelize;
