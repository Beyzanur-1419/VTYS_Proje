const express = require("express");
const router = express.Router();
const userController = require("../controllers/userController");
const { authenticateToken } = require("../middleware/auth");
const {
  updateProfileValidator,
  changePasswordValidator,
} = require("../validators/user.validator");

router.use(authenticateToken);

// Profil görüntüleme
router.get("/profile", userController.getProfile);

// Profil güncelleme
router.put("/profile", updateProfileValidator, userController.updateProfile);

// Şifre değiştirme
router.put("/password", changePasswordValidator, userController.changePassword);

module.exports = router;
