const express = require('express');
const uploadController = require('../controllers/uploadController');
const { protect } = require('../middleware/authMiddleware');
const upload = require('../utils/uploader');

const router = express.Router();

// All routes require authentication
router.use(protect);

/**
 * @swagger
 * /upload:
 *   post:
 *     summary: Upload an image
 *     tags: [Upload]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       content:
 *         multipart/form-data:
 *           schema:
 *             type: object
 *             properties:
 *               image:
 *                 type: string
 *                 format: binary
 *     responses:
 *       201:
 *         description: Image uploaded
 */
router.post('/', upload.single('image'), uploadController.uploadImage);

/**
 * @swagger
 * /upload/list:
 *   get:
 *     summary: Get user's uploaded images
 *     tags: [Upload]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: List of images
 */
router.get('/list', uploadController.getUserImages);

/**
 * @swagger
 * /upload/stats:
 *   get:
 *     summary: Get upload statistics
 *     tags: [Upload]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Upload statistics
 */
router.get('/stats', uploadController.getImageStats);

/**
 * @swagger
 * /upload/{id}:
 *   delete:
 *     summary: Delete an image
 *     tags: [Upload]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Image deleted
 */
router.delete('/:id', uploadController.deleteImage);

module.exports = router;
