const { DataTypes } = require('sequelize');
const sequelize = require('../config/db.postgres');
const User = require('./User');
const AnalysisHistory = require('./AnalysisHistory');

const Recommendation = sequelize.define('Recommendation', {
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
    analysis_id: {
        type: DataTypes.UUID,
        allowNull: true,
        references: {
            model: AnalysisHistory,
            key: 'id',
        },
    },
    recommended_products: {
        type: DataTypes.JSONB, // Storing array of product snippets or IDs
        allowNull: true,
    },
    reason: {
        type: DataTypes.STRING,
        allowNull: true,
    }
}, {
    tableName: 'recommendations',
    timestamps: false
});

// Relationships
AnalysisHistory.hasOne(Recommendation, { foreignKey: 'analysis_id' });
Recommendation.belongsTo(AnalysisHistory, { foreignKey: 'analysis_id' });

User.hasMany(Recommendation, { foreignKey: 'user_id' });
Recommendation.belongsTo(User, { foreignKey: 'user_id' });

module.exports = Recommendation;
