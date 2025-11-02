const express = require("express");
const router = express.Router();
const { authLimiter } = require("../middleware/rateLimiter");
const { authenticateToken } = require("../middleware/auth");
const authController = require("../controllers/authController");

// Rate limiting uygula
router.post("/register", authLimiter, authController.register);
router.post("/login", authLimiter, authController.login);

// Token yenileme ve çıkış
router.post("/refresh", authLimiter, authController.refresh);
router.post("/logout", authenticateToken, authController.logout);

// Şifre sıfırlama
router.post("/forgot-password", authLimiter, authController.forgotPassword);
router.post("/reset-password", authLimiter, authController.resetPassword);

module.exports = router;
