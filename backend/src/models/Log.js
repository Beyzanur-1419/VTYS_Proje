const { DataTypes } = require('sequelize');
const sequelize = require('../config/db.postgres');
const User = require('./User');

const Log = sequelize.define('Log', {
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
    action: {
        type: DataTypes.STRING,
        allowNull: false, // e.g., 'LOGIN', 'LOGOUT', 'ANALYSIS_CREATED'
    },
    timestamp: {
        type: DataTypes.DATE,
        defaultValue: DataTypes.NOW,
    },
}, {
    tableName: 'logs',
    timestamps: false // We use our own timestamp field
});

User.hasMany(Log, { foreignKey: 'user_id' });
Log.belongsTo(User, { foreignKey: 'user_id' });

module.exports = Log;
