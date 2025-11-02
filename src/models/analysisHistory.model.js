const { DataTypes } = require("sequelize");
const { sequelize } = require("../database/postgres");
const User = require("./user.model");

const AnalysisHistory = sequelize.define(
  "AnalysisHistory",
  {
    id: {
      type: DataTypes.UUID,
      defaultValue: DataTypes.UUIDV4,
      primaryKey: true,
    },
    userId: {
      type: DataTypes.UUID,
      allowNull: false,
      references: {
        model: User,
        key: "id",
      },
    },
    imageId: {
      type: DataTypes.STRING, // MongoDB AIImage._id
      allowNull: false,
    },
    imageUrl: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    analysisResult: {
      type: DataTypes.JSONB,
      allowNull: false,
    },
    confidence: {
      type: DataTypes.FLOAT,
      allowNull: true,
    },
    conditions: {
      type: DataTypes.JSONB,
      defaultValue: [],
      allowNull: false,
    },
    recommendations: {
      type: DataTypes.JSONB,
      defaultValue: [],
      allowNull: false,
    },
    status: {
      type: DataTypes.ENUM("pending", "completed", "failed"),
      defaultValue: "pending",
    },
    errorMessage: {
      type: DataTypes.TEXT,
      allowNull: true,
    },
  },
  {
    indexes: [
      {
        fields: ["userId"],
      },
      {
        fields: ["imageId"],
      },
      {
        fields: ["status"],
      },
      {
        fields: ["createdAt"],
      },
    ],
  }
);

// İlişkiler
AnalysisHistory.belongsTo(User, { foreignKey: "userId" });
User.hasMany(AnalysisHistory, { foreignKey: "userId" });

module.exports = AnalysisHistory;
