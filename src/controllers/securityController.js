const SecurityService = require("../services/securityService");
const TwoFactorAuth = require("../models/twoFactorAuth.model");
const { AppError } = require("../utils/errors");
const speakeasy = require("speakeasy");
const qrcode = require("qrcode");

const securityController = {
  // 2FA aktivasyonu
  async enable2FA(req, res, next) {
    try {
      const secret = speakeasy.generateSecret({
        name: `GLOWMANCE:${req.user.email}`,
      });

      await TwoFactorAuth.create({
        userId: req.user.id,
        secret: secret.base32,
        backupCodes: new TwoFactorAuth().generateBackupCodes(),
      });

      const qrCodeUrl = await qrcode.toDataURL(secret.otpauth_url);

      res.json({
        status: "success",
        data: {
          qrCode: qrCodeUrl,
          secret: secret.base32,
        },
      });
    } catch (err) {
      next(err);
    }
  },

  // 2FA doğrulama ve aktivasyon
  async verify2FA(req, res, next) {
    try {
      const { token } = req.body;
      const twoFactorAuth = await TwoFactorAuth.findOne({
        where: { userId: req.user.id },
      });

      if (!twoFactorAuth) {
        throw new AppError("2FA not set up", 400);
      }

      const verified = speakeasy.totp.verify({
        secret: twoFactorAuth.secret,
        encoding: "base32",
        token,
      });

      if (!verified) {
        throw new AppError("Invalid 2FA token", 401);
      }

      await twoFactorAuth.update({ isEnabled: true });

      res.json({
        status: "success",
        message: "2FA enabled successfully",
        data: {
          backupCodes: twoFactorAuth.backupCodes,
        },
      });
    } catch (err) {
      next(err);
    }
  },

  // 2FA devre dışı bırakma
  async disable2FA(req, res, next) {
    try {
      const { password } = req.body;

      // Şifre doğrulama
      const isValidPassword = await req.user.isValidPassword(password);
      if (!isValidPassword) {
        throw new AppError("Invalid password", 401);
      }

      await TwoFactorAuth.destroy({
        where: { userId: req.user.id },
      });

      res.json({
        status: "success",
        message: "2FA disabled successfully",
      });
    } catch (err) {
      next(err);
    }
  },

  // Güvenlik istatistikleri
  async getSecurityStats(req, res, next) {
    try {
      const stats = await SecurityService.getSecurityStats(req.user.id);

      res.json({
        status: "success",
        data: stats,
      });
    } catch (err) {
      next(err);
    }
  },

  // Aktif oturumları listele
  async getActiveSessions(req, res, next) {
    try {
      const sessions = await TokenBlacklist.findAll({
        where: {
          userId: req.user.id,
          type: "active",
          expiresAt: {
            [Op.gt]: new Date(),
          },
        },
      });

      res.json({
        status: "success",
        data: {
          sessions: sessions.map((s) => ({
            id: s.id,
            createdAt: s.createdAt,
            lastActivity: s.updatedAt,
            deviceInfo: s.metadata?.deviceInfo,
            location: s.metadata?.location,
          })),
        },
      });
    } catch (err) {
      next(err);
    }
  },

  // Oturum sonlandır
  async terminateSession(req, res, next) {
    try {
      const { sessionId } = req.params;

      const session = await TokenBlacklist.findOne({
        where: {
          id: sessionId,
          userId: req.user.id,
        },
      });

      if (!session) {
        throw new AppError("Session not found", 404);
      }

      await session.update({
        type: "terminated",
        metadata: {
          ...session.metadata,
          terminatedAt: new Date(),
          terminatedBy: req.user.id,
        },
      });

      res.json({
        status: "success",
        message: "Session terminated successfully",
      });
    } catch (err) {
      next(err);
    }
  },
};

module.exports = securityController;
