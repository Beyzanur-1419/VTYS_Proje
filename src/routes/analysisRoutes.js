const express = require('express');
const analysisController = require('../controllers/analysisController');
const validate = require('../middleware/validateMiddleware');
const { analysisSchema } = require('../validators/analysisValidator');
const { protect } = require('../middleware/authMiddleware');
const upload = require('../utils/uploader');

const router = express.Router();

router.use(protect);

/**
 * @swagger
 * /analysis:
 *   post:
 *     summary: Create a new analysis record
 *     tags: [Analysis]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - image_url
 *               - result_json
 *             properties:
 *               image_url:
 *                 type: string
 *               result_json:
 *                 type: object
 *     responses:
 *       201:
 *         description: Analysis created
 */
router.post('/', validate(analysisSchema), analysisController.createAnalysis);

/**
 * @swagger
 * /analysis/history:
 *   get:
 *     summary: Get user analysis history
 *     tags: [Analysis]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Analysis history
 */
router.get('/history', analysisController.getHistory);

/**
 * @swagger
 * /analysis/history/{id}:
 *   get:
 *     summary: Get analysis details
 *     tags: [Analysis]
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
 *         description: Analysis details
 */
router.get('/history/:id', analysisController.getAnalysisById);

/**
 * @swagger
 * /analysis/history/{id}:
 *   delete:
 *     summary: Delete analysis
 *     tags: [Analysis]
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
 *         description: Analysis deleted
 */
router.delete('/history/:id', analysisController.deleteAnalysis);

/**
 * @swagger
 * /analysis/stats/summary:
 *   get:
 *     summary: Get analysis statistics
 *     tags: [Analysis]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Analysis statistics
 */
router.get('/stats/summary', analysisController.getStats);

/**
 * @swagger
 * /analysis/skin-type:
 *   post:
 *     summary: Analyze skin type from image
 *     tags: [Analysis]
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
 *       200:
 *         description: Analysis result
 */
router.post('/skin-type', upload.single('image'), analysisController.analyzeSkinType);

/**
 * @swagger
 * /analysis/disease:
 *   post:
 *     summary: Analyze skin disease from image
 *     tags: [Analysis]
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
 *       200:
 *         description: Analysis result
 */
router.post('/disease', upload.single('image'), analysisController.analyzeDisease);

module.exports = router;
