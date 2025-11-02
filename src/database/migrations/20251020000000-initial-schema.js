const { Sequelize, DataTypes } = require('sequelize');

module.exports = {
  up: async (queryInterface, Sequelize) => {
    // Users table
    await queryInterface.createTable('Users', {
      id: {
        type: DataTypes.UUID,
        defaultValue: Sequelize.UUIDV4,
        primaryKey: true
      },
      email: {
        type: DataTypes.STRING,
        allowNull: false,
        unique: true
      },
      password: {
        type: DataTypes.STRING,
        allowNull: false
      },
      name: {
        type: DataTypes.STRING,
        allowNull: false
      },
      refreshToken: {
        type: DataTypes.STRING,
        allowNull: true
      },
      resetToken: {
        type: DataTypes.STRING,
        allowNull: true
      },
      resetTokenExpires: {
        type: DataTypes.DATE,
        allowNull: true
      },
      createdAt: {
        type: DataTypes.DATE,
        allowNull: false
      },
      updatedAt: {
        type: DataTypes.DATE,
        allowNull: false
      }
    });

    // Analysis History table
    await queryInterface.createTable('AnalysisHistories', {
      id: {
        type: DataTypes.UUID,
        defaultValue: Sequelize.UUIDV4,
        primaryKey: true
      },
      userId: {
        type: DataTypes.UUID,
        allowNull: false,
        references: {
          model: 'Users',
          key: 'id'
        },
        onDelete: 'CASCADE'
      },
      imageId: {
        type: DataTypes.STRING, // MongoDB ObjectId as string
        allowNull: false
      },
      results: {
        type: DataTypes.JSONB,
        allowNull: false
      },
      timestamp: {
        type: DataTypes.DATE,
        allowNull: false
      },
      createdAt: {
        type: DataTypes.DATE,
        allowNull: false
      },
      updatedAt: {
        type: DataTypes.DATE,
        allowNull: false
      }
    });

    // Add indexes
    await queryInterface.addIndex('Users', ['email']);
    await queryInterface.addIndex('AnalysisHistories', ['userId']);
    await queryInterface.addIndex('AnalysisHistories', ['timestamp']);
  },

  down: async (queryInterface, Sequelize) => {
    await queryInterface.dropTable('AnalysisHistories');
    await queryInterface.dropTable('Users');
  }
};