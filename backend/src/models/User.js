const { DataTypes } = require('sequelize');
const sequelize = require('../config/db.postgres');
const bcrypt = require('bcryptjs');

const User = sequelize.define('User', {
  id: {
    type: DataTypes.UUID,
    defaultValue: DataTypes.UUIDV4,
    primaryKey: true,
  },
  name: {
    type: DataTypes.STRING,
    allowNull: false,
  },
  email: {
    type: DataTypes.STRING,
    allowNull: false,
    unique: true,
    validate: {
      isEmail: true,
    },
  },
  password: {
    type: DataTypes.STRING,
    allowNull: false,
  },
  profileImageUrl: {
    type: DataTypes.STRING,
    allowNull: true,
  },
  skinType: {
    type: DataTypes.STRING,
    allowNull: true,
    comment: 'Cilt tipi (Karma, Kuru, Yağlı, Hassas, Normal)',
  },
  skinGoal: {
    type: DataTypes.STRING,
    allowNull: true,
    comment: 'Ana hedef (Leke Karşıtı, Nem, Anti-Aging, vb.)',
  },
  age: {
    type: DataTypes.INTEGER,
    allowNull: true,
  },
  notificationEnabled: {
    type: DataTypes.BOOLEAN,
    defaultValue: true,
    allowNull: false,
  },
  emailNotifications: {
    type: DataTypes.BOOLEAN,
    defaultValue: true,
    allowNull: false,
  },
}, {
  timestamps: true,
  createdAt: 'created_at',
  updatedAt: 'updated_at',
  hooks: {
    beforeCreate: async (user) => {
      if (user.password) {
        const salt = await bcrypt.genSalt(10);
        user.password = await bcrypt.hash(user.password, salt);
      }
    },
    beforeUpdate: async (user) => {
      if (user.changed('password')) {
        const salt = await bcrypt.genSalt(10);
        user.password = await bcrypt.hash(user.password, salt);
      }
    },
  },
});

User.prototype.matchPassword = async function (enteredPassword) {
  return await bcrypt.compare(enteredPassword, this.password);
};

module.exports = User;
