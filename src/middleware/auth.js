const UserService = require("../services/UserService");
const Config = require("../config/config");
const { AppError } = require("../utils/errors");
const TokenManager = require("../utils/tokenManager");

const authenticateToken = async (req, res, next) => {
  try {
    // Verify JWT configuration
    if (!Config.JWT_SECRET) {
      throw new AppError("JWT configuration error", 500);
    }

    // Token'ı header'dan al
    const authHeader = req.headers.authorization;
    if (!authHeader?.startsWith("Bearer ")) {
      throw new AppError(
        "Authentication required. Please provide a valid token",
        401
      );
    }

    const token = authHeader.split(" ")[1];

    if (!token) {
      throw new AppError("Token not provided", 401);
    }

    // Token'ı doğrula
    const decoded = await TokenManager.verifyToken(token, Config.JWT_SECRET);

    // Kullanıcıyı bul
    const userService = new UserService();
    const user = await userService.findById(decoded.id);

    if (!user) {
      throw new AppError("User not found or account deleted", 401);
    }

    // Token versiyonu kontrolü (opsiyonel güvenlik katmanı)
    if (decoded.tokenVersion && decoded.tokenVersion !== user.tokenVersion) {
      throw new AppError("Token has been revoked", 401);
    }

    // User'ı request'e ekle
    req.user = user;
    req.token = token;
    next();
  } catch (error) {
    if (error.isOperational) {
      next(error);
    } else {
      next(new AppError("Authentication failed", 401));
    }
  }
};

module.exports = { authenticateToken };
