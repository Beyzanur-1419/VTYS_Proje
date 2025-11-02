const { DataTypes } = require('sequelize');
const { sequelize } = require('../database/connection');

const AnalysisHistory = sequelize.define('AnalysisHistory', {
  id: {
    type: DataTypes.UUID,
    defaultValue: DataTypes.UUIDV4,
    primaryKey: true
  },
  userId: {
    type: DataTypes.UUID,
    allowNull: false,
    references: {
      model: 'Users',
      key: 'id'
    }
  },
  imageId: {
    type: DataTypes.STRING, // MongoDB ObjectId
    allowNull: false
  },
  skinConditions: {
    type: DataTypes.JSONB,
    allowNull: false
  },
  confidence: {
    type: DataTypes.FLOAT,
    allowNull: false
  },
  recommendations: {
    type: DataTypes.JSONB,
    allowNull: false
  },
  metadata: {
    type: DataTypes.JSONB,
    allowNull: true
  }
}, {
  indexes: [
    { fields: ['userId'] },
    { fields: ['imageId'] },
    { fields: ['createdAt'] }
  ]
});

module.exports = AnalysisHistory;