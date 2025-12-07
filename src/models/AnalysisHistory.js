const { DataTypes } = require('sequelize');
const sequelize = require('../config/db.postgres');
const User = require('./User');

const AnalysisHistory = sequelize.define('AnalysisHistory', {
  id: {
    type: DataTypes.UUID,
    defaultValue: DataTypes.UUIDV4,
    primaryKey: true,
  },
  user_id: {
    type: DataTypes.UUID,
    allowNull: false,
    references: {
      model: User,
      key: 'id',
    },
  },
  image_url: {
    type: DataTypes.STRING,
    allowNull: false,
  },
  result_json: {
    type: DataTypes.JSONB,
    allowNull: false,
  },
}, {
  timestamps: true,
  createdAt: 'created_at',
  updatedAt: 'updated_at',
  indexes: [
    {
      fields: ['user_id', 'created_at'],
      name: 'idx_analysis_user_date'
    },
    {
      fields: ['created_at'],
      name: 'idx_analysis_created_at'
    },
    {
      fields: ['result_json'],
      using: 'GIN',
      name: 'idx_analysis_result_json'
    }
  ]
});

User.hasMany(AnalysisHistory, { foreignKey: 'user_id' });
AnalysisHistory.belongsTo(User, { foreignKey: 'user_id' });

module.exports = AnalysisHistory;
