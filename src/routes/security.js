const express = require("express");
const router = express.Router();
const securityController = require("../controllers/securityController");
const { authenticateToken } = require("../middleware/auth");
const { authLimiter } = require("../middleware/rateLimiter");

// Tüm route'lar için authentication gerekli
router.use(authenticateToken);

// 2FA yönetimi
router.post("/2fa/enable", authLimiter, securityController.enable2FA);
router.post("/2fa/verify", authLimiter, securityController.verify2FA);
router.post("/2fa/disable", authLimiter, securityController.disable2FA);

// Güvenlik durumu ve istatistikler
router.get("/stats", securityController.getSecurityStats);

// Oturum yönetimi
router.get("/sessions", securityController.getActiveSessions);
router.delete("/sessions/:sessionId", securityController.terminateSession);

module.exports = router;
