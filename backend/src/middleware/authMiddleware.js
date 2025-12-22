const { verifyToken } = require('../utils/jwt');
const User = require('../models/User');

const protect = async (req, res, next) => {
  let token;

  if (
    req.headers.authorization &&
    req.headers.authorization.startsWith('Bearer')
  ) {
    try {
      token = req.headers.authorization.split(' ')[1];
      console.log(`[AuthMiddleware] Verifying token...`);
      const decoded = verifyToken(token);
      console.log(`[AuthMiddleware] Token decoded. ID: ${decoded.id}`);

      req.user = await User.findByPk(decoded.id);

      if (!req.user) {
        console.log(`[AuthMiddleware] User not found for ID: ${decoded.id}`);
        return res.status(401).json({ success: false, message: 'Not authorized, user not found' });
      }
      console.log(`[AuthMiddleware] User found: ${req.user.id}`);
      next();
    } catch (error) {
      console.error(error);
      res.status(401).json({ success: false, message: 'Not authorized, token failed' });
    }
  }

  if (!token) {
    res.status(401).json({ success: false, message: 'Not authorized, no token' });
  }
};

module.exports = { protect };
