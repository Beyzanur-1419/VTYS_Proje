const crypto = require('crypto');
const { promisify } = require('util');

class TokenSecurityService {
  constructor() {
    this.hashAlgorithm = 'sha256';
    this.tokenBlacklist = new Map(); // Production'da Redis kullanılmalı
  }

  async hashToken(token) {
    const hash = crypto.createHash(this.hashAlgorithm);
    return hash.update(token).digest('hex');
  }

  async addToBlacklist(token, expiryTime) {
    const hashedToken = await this.hashToken(token);
    this.tokenBlacklist.set(hashedToken, {
      expiresAt: new Date(Date.now() + expiryTime * 1000)
    });

    // Otomatik temizleme için timer kur
    setTimeout(() => {
      this.tokenBlacklist.delete(hashedToken);
    }, expiryTime * 1000);
  }

  async isBlacklisted(token) {
    const hashedToken = await this.hashToken(token);
    const blacklistEntry = this.tokenBlacklist.get(hashedToken);

    if (!blacklistEntry) return false;

    if (blacklistEntry.expiresAt < new Date()) {
      this.tokenBlacklist.delete(hashedToken);
      return false;
    }

    return true;
  }

  async cleanupBlacklist() {
    const now = new Date();
    for (const [token, entry] of this.tokenBlacklist.entries()) {
      if (entry.expiresAt < now) {
        this.tokenBlacklist.delete(token);
      }
    }
  }

  // Production'da kullanılacak Redis implementasyonu
  async initializeRedisBlacklist() {
    // Redis bağlantısı
    // this.redis = new Redis(Config.REDIS_URL);
  }
}

module.exports = new TokenSecurityService();