const { DataTypes } = require("sequelize");
const { sequelize } = require("../database/postgres");
const crypto = require("crypto");

const TwoFactorAuth = sequelize.define("TwoFactorAuth", {
  id: {
    type: DataTypes.UUID,
    defaultValue: DataTypes.UUIDV4,
    primaryKey: true,
  },
  userId: {
    type: DataTypes.UUID,
    allowNull: false,
    unique: true,
  },
  secret: {
    type: DataTypes.STRING,
    allowNull: false,
  },
  isEnabled: {
    type: DataTypes.BOOLEAN,
    defaultValue: false,
  },
  backupCodes: {
    type: DataTypes.ARRAY(DataTypes.STRING),
    defaultValue: [],
  },
});

// Instance methods
TwoFactorAuth.prototype.generateSecret = function () {
  return crypto.randomBytes(32).toString("hex");
};

TwoFactorAuth.prototype.generateBackupCodes = function () {
  const codes = [];
  for (let i = 0; i < 10; i++) {
    codes.push(crypto.randomBytes(4).toString("hex"));
  }
  return codes;
};

module.exports = TwoFactorAuth;
