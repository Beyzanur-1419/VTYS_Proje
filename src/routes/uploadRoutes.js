const express = require('express');
const router = express.Router();
const uploadController = require('../controllers/uploadController');
const { authenticateToken } = require('../middleware/auth');

// All routes require authentication
router.use(authenticateToken);

// Upload image route with multer middleware
router.post(
  '/',
  uploadController.getUploadMiddleware(),
  uploadController.uploadImage.bind(uploadController)
);

module.exports = router;