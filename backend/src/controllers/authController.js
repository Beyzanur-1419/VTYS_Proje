const authService = require('../services/authService');

class AuthController {
  async register(req, res, next) {
    try {
      const { user, token } = await authService.register(req.body);
      res.status(201).json({
        accessToken: token,
        refreshToken: null,
        user: {
          id: user.id,
          name: user.name,
          email: user.email,
        },
      });
    } catch (error) {
      next(error);
    }
  }

  async login(req, res, next) {
    try {
      const { email, password } = req.body;
      const { user, token } = await authService.login(email, password);
      res.status(200).json({
        accessToken: token,
        refreshToken: null,
        user: {
          id: user.id,
          name: user.name,
          email: user.email,
        },
      });
    } catch (error) {
      next(error);
    }
  }

  async getMe(req, res, next) {
    try {
      res.status(200).json({
        success: true,
        user: {
          id: req.user.id,
          name: req.user.name,
          email: req.user.email,
        },
      });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new AuthController();
