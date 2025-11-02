const { DataTypes } = require("sequelize");
const { sequelize } = require("../database/postgres");

const SecurityLog = sequelize.define(
  "SecurityLog",
  {
    id: {
      type: DataTypes.UUID,
      defaultValue: DataTypes.UUIDV4,
      primaryKey: true,
    },
    userId: {
      type: DataTypes.UUID,
      allowNull: true,
      index: true,
    },
    ip: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    eventType: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    eventData: {
      type: DataTypes.JSONB,
      defaultValue: {},
    },
    userAgent: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    severity: {
      type: DataTypes.ENUM("info", "warning", "error", "critical"),
      defaultValue: "info",
    },
    status: {
      type: DataTypes.ENUM("success", "failure", "blocked"),
      defaultValue: "success",
    },
  },
  {
    indexes: [
      {
        fields: ["ip"],
      },
      {
        fields: ["eventType"],
      },
      {
        fields: ["createdAt"],
      },
    ],
  }
);

module.exports = SecurityLog;
