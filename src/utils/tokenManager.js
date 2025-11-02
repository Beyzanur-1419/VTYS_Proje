const jwt = require("jsonwebtoken");
const Config = require("../config/config");
const TokenBlacklist = require("../models/tokenBlacklist.model");
const { AppError } = require("./errors");

class TokenManager {
  static async blacklistToken(token, reason = "manual_blacklist") {
    try {
      const decoded = jwt.decode(token);
      if (!decoded || !decoded.exp) {
        throw new Error("Invalid token format");
      }

      await TokenBlacklist.create({
        token: token,
        reason: reason,
        expiresAt: new Date(decoded.exp * 1000),
      });
    } catch (error) {
      console.error("Error blacklisting token:", error);
      throw error;
    }
  }

  static async isBlacklisted(token) {
    const blacklisted = await TokenBlacklist.findOne({
      where: { token },
    });
    return !!blacklisted;
  }

  static generateAccessToken(user) {
    return jwt.sign({ id: user.id, email: user.email }, Config.JWT_SECRET, {
      expiresIn: Config.JWT_EXPIRES_IN,
    });
  }

  static generateRefreshToken(user) {
    return jwt.sign(
      { id: user.id, tokenVersion: user.tokenVersion || 0 },
      Config.REFRESH_TOKEN_SECRET,
      { expiresIn: Config.REFRESH_TOKEN_EXPIRES_IN }
    );
  }

  static async rotateTokens(user, oldRefreshToken) {
    // Eski refresh token'ı blacklist'e ekle
    if (oldRefreshToken) {
      await this.blacklistToken(oldRefreshToken, "rotation");
    }

    // Token versiyonunu artır
    await user.increment("tokenVersion");
    await user.reload();

    // Yeni tokenları oluştur
    const accessToken = this.generateAccessToken(user);
    const refreshToken = this.generateRefreshToken(user);

    return { accessToken, refreshToken };
  }

  static async verifyToken(token, secret) {
    try {
      // Blacklist kontrolü
      if (await this.isBlacklisted(token)) {
        throw new AppError("Token has been revoked", 401);
      }

      // Token doğrulama
      const decoded = jwt.verify(token, secret);
      return decoded;
    } catch (error) {
      if (error.name === "JsonWebTokenError") {
        throw new AppError("Invalid token", 401);
      }
      if (error.name === "TokenExpiredError") {
        throw new AppError("Token has expired", 401);
      }
      throw error;
    }
  }
}

module.exports = TokenManager;
