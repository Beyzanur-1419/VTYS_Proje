const userService = require('../services/userService');

class UserController {
  async getProfile(req, res, next) {
    try {
      const user = await userService.getUserById(req.user.id);
      res.status(200).json({
        id: user.id,
        name: user.name,
        email: user.email,
        profileImageUrl: user.profileImageUrl || null,
        skinType: user.skinType || null,
        skinGoal: user.skinGoal || null,
        age: user.age || null,
        createdAt: user.created_at,
        updatedAt: user.updated_at
      });
    } catch (error) {
      next(error);
    }
  }

  async updateProfile(req, res, next) {
    try {
      const user = await userService.updateUserProfile(req.user.id, req.body);
      res.status(200).json({
        id: user.id,
        name: user.name,
        email: user.email,
        profileImageUrl: user.profileImageUrl || null,
        createdAt: user.created_at,
        updatedAt: user.updated_at
      });
    } catch (error) {
      next(error);
    }
  }

  async updateSkinProfile(req, res, next) {
    try {
      const { skinType, skinGoal, age } = req.body;
      const user = await userService.updateUserProfile(req.user.id, {
        skinType,
        skinGoal,
        age
      });
      res.status(200).json({
        id: user.id,
        name: user.name,
        email: user.email,
        skinType: user.skinType || null,
        skinGoal: user.skinGoal || null,
        age: user.age || null,
        profileImageUrl: user.profileImageUrl || null,
        createdAt: user.created_at,
        updatedAt: user.updated_at
      });
    } catch (error) {
      next(error);
    }
  }

  async getSettings(req, res, next) {
    try {
      const user = await userService.getUserById(req.user.id);
      res.status(200).json({
        notificationEnabled: user.notificationEnabled ?? true,
        emailNotifications: user.emailNotifications ?? true,
        language: 'tr', // Varsayılan dil
        theme: 'dark' // Varsayılan tema
      });
    } catch (error) {
      next(error);
    }
  }

  async updateSettings(req, res, next) {
    try {
      const { notificationEnabled, emailNotifications } = req.body;
      const user = await userService.updateUserProfile(req.user.id, {
        notificationEnabled,
        emailNotifications
      });
      res.status(200).json({
        notificationEnabled: user.notificationEnabled ?? true,
        emailNotifications: user.emailNotifications ?? true,
        language: 'tr',
        theme: 'dark'
      });
    } catch (error) {
      next(error);
    }
  }

  async getNotifications(req, res, next) {
    try {
      // Şimdilik boş liste döndür, sonra bildirim sistemi eklendiğinde doldurulacak
      res.status(200).json({
        notifications: [],
        unreadCount: 0
      });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new UserController();
