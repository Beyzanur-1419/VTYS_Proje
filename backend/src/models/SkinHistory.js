const { DataTypes } = require('sequelize');
const sequelize = require('../config/db.postgres');
const User = require('./User');

const SkinHistory = sequelize.define('SkinHistory', {
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
    condition: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    diagnosis_date: {
        type: DataTypes.DATEONLY,
        allowNull: true,
    },
    treatment: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    notes: {
        type: DataTypes.TEXT,
        allowNull: true,
    },
    created_at: {
        type: DataTypes.DATE,
        defaultValue: DataTypes.NOW,
    }
}, {
    tableName: 'skin_history',
    timestamps: false // Managed manually via created_at
});

User.hasMany(SkinHistory, { foreignKey: 'user_id' });
SkinHistory.belongsTo(User, { foreignKey: 'user_id' });

module.exports = SkinHistory;
