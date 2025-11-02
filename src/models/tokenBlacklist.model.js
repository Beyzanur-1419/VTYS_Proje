const { DataTypes } = require("sequelize");
const { sequelize } = require("../database/postgres");

const TokenBlacklist = sequelize.define(
  "TokenBlacklist",
  {
    id: {
      type: DataTypes.UUID,
      defaultValue: DataTypes.UUIDV4,
      primaryKey: true,
    },
    token: {
      type: DataTypes.STRING,
      allowNull: false,
      unique: true,
    },
    reason: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    expiresAt: {
      type: DataTypes.DATE,
      allowNull: false,
    },
  },
  {
    indexes: [
      {
        fields: ["token"],
      },
      {
        fields: ["expiresAt"],
      },
    ],
  }
);

// Otomatik cleanup için helper
TokenBlacklist.cleanup = async function () {
  try {
    const result = await this.destroy({
      where: {
        expiresAt: {
          [sequelize.Op.lt]: new Date(),
        },
      },
    });
    console.log(`Cleaned up ${result} expired blacklist tokens`);
  } catch (error) {
    console.error("Token blacklist cleanup failed:", error);
  }
};

// Her saat başı cleanup işlemi
setInterval(() => TokenBlacklist.cleanup(), 60 * 60 * 1000);

module.exports = TokenBlacklist;
