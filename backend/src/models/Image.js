const { DataTypes } = require('sequelize');
const sequelize = require('../config/db.postgres');
const User = require('./User');
// analysis_id could be circular dependency if we import AnalysisHistory here directly at top level if AnalysisHistory also imports Image.
// But mostly AnalysisHistory imports User. Let's lazily handle or just define foreign key without hard model import if creating circularity issues.
// For now, let's treat it safely.

const Image = sequelize.define('Image', {
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
    uploaded_at: {
        type: DataTypes.DATE,
        defaultValue: DataTypes.NOW,
    },
    analysis_id: {
        type: DataTypes.UUID,
        allowNull: true,
        // We can define the reference constraints in DB without importing the class to avoid circular dep loops in JS request
    }
}, {
    tableName: 'all_images',
    timestamps: false
});

User.hasMany(Image, { foreignKey: 'user_id' });
Image.belongsTo(User, { foreignKey: 'user_id' });

module.exports = Image;
