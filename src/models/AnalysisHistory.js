const { Sequelize, DataTypes } = require("sequelize");
const { sequelize } = require("../database/postgres");

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
        model: "Users",
        key: "id",
      },
    },
    imageId: {
      type: DataTypes.STRING,
      allowNull: false,
      comment: "MongoDB AIImage document ID",
    },
    results: {
      type: DataTypes.JSONB,
      allowNull: false,
      comment: "ML model analysis result",
    },
    timestamp: {
      type: DataTypes.DATE,
      allowNull: false,
      defaultValue: DataTypes.NOW,
    },
  },
  {
    timestamps: true,
    indexes: [
      {
        fields: ["userId"],
      },
      {
        fields: ["imageId"],
      },
    ],
  }
);

module.exports = AnalysisHistory;
