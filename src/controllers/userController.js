const userService = require('../services/userService');

class UserController {
  async getProfile(req, res, next) {
    try {
      const user = await userService.getUserById(req.user.id);
      res.status(200).json({ success: true, user });
    } catch (error) {
      next(error);
    }
  }

  async updateProfile(req, res, next) {
    try {
      const user = await userService.updateUserProfile(req.user.id, req.body);
      res.status(200).json({ success: true, user });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new UserController();
