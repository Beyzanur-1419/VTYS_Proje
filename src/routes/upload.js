const express = require("express");
const router = express.Router();
const { upload, handleMulterError } = require("../middleware/upload");
const { authenticateToken } = require("../middleware/auth");
const {
  uploadImage,
  getUploadedImages,
  deleteImage,
} = require("../controllers/uploadController");

// Auth middleware'i tüm route'lara uygula
router.use(authenticateToken);

// Upload endpoints
router.post("/upload", upload.single("image"), handleMulterError, uploadImage);

router.get("/images", getUploadedImages);
router.delete("/images/:id", deleteImage);

module.exports = router;
