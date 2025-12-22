const { DataTypes } = require('sequelize');
const sequelize = require('../config/db.postgres');

const Product = sequelize.define('Product', {
  id: {
    type: DataTypes.UUID,
    defaultValue: DataTypes.UUIDV4,
    primaryKey: true,
  },
  title: {
    type: DataTypes.STRING,
    allowNull: false,
  },
  brand: {
    type: DataTypes.STRING,
    allowNull: false,
  },
  price: {
    type: DataTypes.DECIMAL(10, 2),
    allowNull: true,
  },
  ingredients: {
    type: DataTypes.TEXT,
    allowNull: true,
  },
  suitable_skin_types: {
    type: DataTypes.JSONB, // Array of strings e.g. ["Oily", "Acne"]
    allowNull: true,
  },
  category: {
    type: DataTypes.STRING,
    allowNull: true,
  },
  source: {
    type: DataTypes.STRING,
    allowNull: true,
  },
  image_url: {
    type: DataTypes.STRING,
    allowNull: true,
  },
}, {
  timestamps: true,
  createdAt: 'created_at',
  updatedAt: 'updated_at',
  indexes: [
    {
      fields: ['brand'],
      name: 'idx_product_brand'
    },
    {
      fields: ['title'],
      name: 'idx_product_title'
    },
    {
      fields: ['price'],
      name: 'idx_product_price'
    },
    {
      fields: ['created_at'],
      name: 'idx_product_created_at'
    }
  ]
});

module.exports = Product;
