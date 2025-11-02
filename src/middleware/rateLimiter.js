const rateLimit = require("express-rate-limit");
const Config = require("../config/config");

// Genel API rate limiter
const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 dakika
  max: 100, // IP başına max istek
  message: {
    status: "error",
    message: "Too many requests from this IP, please try again later.",
  },
  standardHeaders: true,
  legacyHeaders: false,
});

// Auth endpoints için özel limiter
const authLimiter = rateLimit({
  windowMs: 60 * 60 * 1000, // 1 saat
  max: 10, // IP başına max login denemesi
  message: {
    status: "error",
    message:
      "Too many login attempts from this IP, please try again after an hour",
  },
  standardHeaders: true,
  legacyHeaders: false,
});

// Upload endpoints için özel limiter
const uploadLimiter = rateLimit({
  windowMs: 60 * 60 * 1000, // 1 saat
  max: 20, // IP başına max yükleme
  message: {
    status: "error",
    message: "Upload limit reached, please try again later",
  },
  standardHeaders: true,
  legacyHeaders: false,
});

module.exports = {
  apiLimiter,
  authLimiter,
  uploadLimiter,
};
