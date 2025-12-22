const { DataTypes } = require('sequelize');
const sequelize = require('../config/db.postgres');
const User = require('./User');

const Medication = sequelize.define('Medication', {
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
    product_name: {
        type: DataTypes.STRING,
        allowNull: false,
    },
    start_date: {
        type: DataTypes.DATEONLY,
        allowNull: true,
    },
    end_date: {
        type: DataTypes.DATEONLY,
        allowNull: true,
    },
    notes: {
        type: DataTypes.TEXT,
        allowNull: true,
    },
}, {
    tableName: 'medications',
    timestamps: false // ERD doesn't strictly specify created_at/updated_at but we can add if needed. adhering to ERD for now.
});

User.hasMany(Medication, { foreignKey: 'user_id' });
Medication.belongsTo(User, { foreignKey: 'user_id' });

module.exports = Medication;
