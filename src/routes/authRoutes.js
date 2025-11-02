const express = require("express");
const router = express.Router();
const authController = require("../controllers/authController");
const { authenticateToken } = require("../middleware/auth");
const { validationResult } = require("express-validator");
const { AppError } = require("../utils/errors");
const {
  registerValidator,
  loginValidator,
  passwordResetValidator,
  newPasswordValidator,
} = require("../validators/auth.validator");

// Validation middleware
const validate = (validators) => {
  return async (req, res, next) => {
    // Run all validators
    await Promise.all(validators.map((validator) => validator.run(req)));

    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      const errorMessages = errors.array().map((err) => err.msg);
      return next(new AppError(errorMessages.join(", "), 400));
    }

    next();
  };
};

// Public routes
router.post("/register", validate(registerValidator), authController.register);
router.post("/login", validate(loginValidator), authController.login);
router.post("/refresh-token", authController.refresh);
router.post("/forgot-password", validate(passwordResetValidator), authController.forgotPassword);
router.post("/reset-password", validate(newPasswordValidator), authController.resetPassword);

// Protected routes
router.post("/logout", authenticateToken, authController.logout);

module.exports = router;
