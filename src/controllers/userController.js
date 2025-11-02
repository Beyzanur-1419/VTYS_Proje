const UserService = require('../services/UserService');
const { AppError } = require('../utils/errors');

class UserController {
  constructor() {
    this.userService = new UserService();
  }

  async getProfile(req, res, next) {
    try {
      const user = await this.userService.findById(req.user.id);
      
      if (!user) {
        throw new AppError('User not found', 404);
      }

      res.json({
        status: 'success',
        data: {
          user: {
            id: user.id,
            email: user.email,
            name: user.name,
            createdAt: user.createdAt,
            updatedAt: user.updatedAt,
          }
        }
      });
    } catch (err) {
      next(err);
    }
  }

  async updateProfile(req, res, next) {
    try {
      const { name, email } = req.body;
      const userId = req.user.id;

      // Email değişikliği kontrolü
      if (email && email !== req.user.email) {
        const existingUser = await this.userService.findByEmail(email);
        if (existingUser) {
          throw new AppError('Email already in use', 400);
        }
      }

      const updateData = {};
      if (name !== undefined) updateData.name = name;
      if (email !== undefined) updateData.email = email;

      const user = await this.userService.update(userId, updateData);

      res.json({
        status: 'success',
        data: {
          user: {
            id: user.id,
            email: user.email,
            name: user.name,
          }
        }
      });
    } catch (err) {
      next(err);
    }
  }

  async changePassword(req, res, next) {
    try {
      const { currentPassword, newPassword } = req.body;
      const userId = req.user.id;

      if (!currentPassword || !newPassword) {
        throw new AppError('Current password and new password are required', 400);
      }

      if (newPassword.length < 6) {
        throw new AppError('New password must be at least 6 characters', 400);
      }

      // Get user with password
      const user = await this.userService.findById(userId);
      
      // Verify current password
      const isValidPassword = await user.isValidPassword(currentPassword);
      if (!isValidPassword) {
        throw new AppError('Current password is incorrect', 401);
      }

      // Update password (will be hashed by model hook)
      await this.userService.update(userId, { password: newPassword });

      res.json({
        status: 'success',
        message: 'Password updated successfully'
      });
    } catch (err) {
      next(err);
    }
  }
}

module.exports = new UserController();

