const TokenSecurityService = require('./TokenSecurityService');

class TokenBlacklistService {
  constructor() {
    this.blacklist = new Map();
    this.cleanupInterval = 1000 * 60 * 60; // Her saat temizlik
    this.startCleanupTask();
  }

  async addToBlacklist(token, expiryTime) {
    const { hash, salt } = await TokenSecurityService.secureToken(token);
    this.blacklist.set(hash, {
      salt,
      expiresAt: new Date(Date.now() + expiryTime * 1000)
    });
  }

  async isBlacklisted(token) {
    for (const [hash, entry] of this.blacklist.entries()) {
      if (await TokenSecurityService.verifyToken(token, hash, entry.salt)) {
        if (entry.expiresAt > new Date()) {
          return true;
        }
        // Süresi dolmuş token'ı sil
        this.blacklist.delete(hash);
      }
    }
    return false;
  }

  startCleanupTask() {
    setInterval(() => {
      const now = new Date();
      for (const [hash, entry] of this.blacklist.entries()) {
        if (entry.expiresAt <= now) {
          this.blacklist.delete(hash);
        }
      }
    }, this.cleanupInterval);
  }

  // Test ve debug için
  getBlacklistSize() {
    return this.blacklist.size;
  }
}

module.exports = new TokenBlacklistService();