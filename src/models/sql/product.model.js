const { DataTypes } = require('sequelize');
const { sequelize } = require('../database/connection');

const Product = sequelize.define('Product', {
  id: {
    type: DataTypes.UUID,
    defaultValue: DataTypes.UUIDV4,
    primaryKey: true
  },
  name: {
    type: DataTypes.STRING,
    allowNull: false
  },
  brand: {
    type: DataTypes.STRING,
    allowNull: false
  },
  category: {
    type: DataTypes.STRING,
    allowNull: false
  },
  description: {
    type: DataTypes.TEXT,
    allowNull: true
  },
  price: {
    type: DataTypes.DECIMAL(10, 2),
    allowNull: false
  },
  ingredients: {
    type: DataTypes.ARRAY(DataTypes.STRING),
    allowNull: true
  },
  suitable_for: {
    type: DataTypes.ARRAY(DataTypes.STRING),
    allowNull: true
  },
  size: {
    type: DataTypes.STRING,
    allowNull: true
  },
  source: {
    type: DataTypes.STRING,
    allowNull: false
  },
  sourceUrl: {
    type: DataTypes.STRING,
    allowNull: false
  },
  viewCount: {
    type: DataTypes.INTEGER,
    defaultValue: 0
  },
  metadata: {
    type: DataTypes.JSONB,
    allowNull: true
  }
}, {
  indexes: [
    { fields: ['brand'] },
    { fields: ['category'] },
    { fields: ['source'] },
    { fields: ['viewCount'] }
  ]
});

module.exports = Product;