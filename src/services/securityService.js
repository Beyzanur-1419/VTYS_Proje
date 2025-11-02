const SecurityLog = require("../models/securityLog.model");
const TokenBlacklist = require("../models/tokenBlacklist.model");
const { AppError } = require("../utils/errors");
const Config = require("../config/config");

class SecurityService {
  // IP bazlı şüpheli aktivite takibi
  static async trackActivity(
    ip,
    eventType,
    eventData = {},
    userId = null,
    userAgent = null
  ) {
    try {
      // Son 1 saatteki başarısız girişimleri kontrol et
      const recentFailures = await SecurityLog.count({
        where: {
          ip,
          status: "failure",
          createdAt: {
            [Op.gte]: new Date(Date.now() - 60 * 60 * 1000),
          },
        },
      });

      // IP bazlı risk değerlendirmesi
      let severity = "info";
      if (recentFailures >= 5) severity = "warning";
      if (recentFailures >= 10) severity = "error";
      if (recentFailures >= 20) severity = "critical";

      // Log kaydı oluştur
      await SecurityLog.create({
        ip,
        userId,
        eventType,
        eventData,
        userAgent,
        severity,
        status: eventData.status || "success",
      });

      // Kritik seviyede ise IP'yi blokla
      if (severity === "critical") {
        await this.blockIP(ip);
        throw new AppError("IP blocked due to suspicious activity", 403);
      }

      return true;
    } catch (error) {
      console.error("Security tracking error:", error);
      throw error;
    }
  }

  // Aktif oturum sayısı kontrolü
  static async checkActiveSessions(userId) {
    try {
      const activeSessions = await TokenBlacklist.count({
        where: {
          userId,
          type: "active",
          expiresAt: {
            [Op.gt]: new Date(),
          },
        },
      });

      if (activeSessions >= Config.MAX_ACTIVE_SESSIONS) {
        throw new AppError("Maximum active sessions reached", 400);
      }

      return true;
    } catch (error) {
      console.error("Session check error:", error);
      throw error;
    }
  }

  // IP bloklama
  static async blockIP(ip, duration = 24 * 60 * 60 * 1000) {
    // 24 saat
    try {
      await SecurityLog.create({
        ip,
        eventType: "ip_blocked",
        severity: "critical",
        status: "blocked",
        eventData: {
          duration,
          unblockAt: new Date(Date.now() + duration),
        },
      });
    } catch (error) {
      console.error("IP blocking error:", error);
      throw error;
    }
  }

  // IP blok kontrolü
  static async isIPBlocked(ip) {
    try {
      const block = await SecurityLog.findOne({
        where: {
          ip,
          eventType: "ip_blocked",
          status: "blocked",
          createdAt: {
            [Op.gt]: new Date(Date.now() - 24 * 60 * 60 * 1000),
          },
        },
        order: [["createdAt", "DESC"]],
      });

      return !!block;
    } catch (error) {
      console.error("IP block check error:", error);
      return false;
    }
  }

  // Güvenlik istatistikleri
  static async getSecurityStats(userId) {
    try {
      const [
        totalFailedAttempts,
        recentFailedAttempts,
        blockedIPs,
        suspiciousActivities,
      ] = await Promise.all([
        SecurityLog.count({
          where: {
            userId,
            status: "failure",
          },
        }),
        SecurityLog.count({
          where: {
            userId,
            status: "failure",
            createdAt: {
              [Op.gt]: new Date(Date.now() - 24 * 60 * 60 * 1000),
            },
          },
        }),
        SecurityLog.count({
          where: {
            eventType: "ip_blocked",
            status: "blocked",
          },
        }),
        SecurityLog.findAll({
          where: {
            userId,
            severity: {
              [Op.in]: ["warning", "error", "critical"],
            },
          },
          limit: 10,
          order: [["createdAt", "DESC"]],
        }),
      ]);

      return {
        totalFailedAttempts,
        recentFailedAttempts,
        blockedIPs,
        suspiciousActivities,
      };
    } catch (error) {
      console.error("Security stats error:", error);
      throw error;
    }
  }
}

module.exports = SecurityService;
