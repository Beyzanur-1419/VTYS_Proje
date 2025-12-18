const express = require('express');
const productController = require('../controllers/productController');
const validate = require('../middleware/validateMiddleware');
const { createProductSchema } = require('../validators/productValidator');
const { protect } = require('../middleware/authMiddleware');
const { cache } = require('../middleware/cacheMiddleware');

const router = express.Router();

/**
 * @swagger
 * /products:
 *   get:
 *     summary: Get all products
 *     tags: [Products]
 *     responses:
 *       200:
 *         description: List of products
 */
router.get('/', cache(600), productController.getAllProducts); // Cache for 10 minutes

/**
 * @swagger
 * /products/recommendations:
 *   get:
 *     summary: Get product recommendations
 *     tags: [Products]
 *     parameters:
 *       - in: query
 *         name: conditions
 *         schema:
 *           type: string
 *         description: Comma separated skin conditions (e.g. acne,oily)
 *     responses:
 *       200:
 *         description: List of recommended products
 */
router.get('/recommendations', cache(300), productController.getRecommendations); // Cache for 5 minutes

/**
 * @swagger
 * /products/recommendations/advanced:
 *   post:
 *     summary: Get advanced product recommendations based on profile
 *     tags: [Products]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               skinType:
 *                 type: string
 *               problems:
 *                 type: array
 *                 items:
 *                   type: string
 *     responses:
 *       200:
 *         description: List of recommended products
 */
router.post('/recommendations/advanced', productController.getAdvancedRecommendations);

/**
 * @swagger
 * /products/trending:
 *   get:
 *     summary: Get trending products
 *     tags: [Products]
 *     parameters:
 *       - in: query
 *         name: limit
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: List of trending products
 */
router.get('/trending', cache(600), productController.getTrendingProducts); // Cache for 10 minutes

/**
 * @swagger
 * /products/search:
 *   get:
 *     summary: Search products
 *     tags: [Products]
 *     parameters:
 *       - in: query
 *         name: q
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Search results
 */
router.get('/search', cache(300), productController.searchProducts); // Cache for 5 minutes

/**
 * @swagger
 * /products/{id}:
 *   get:
 *     summary: Get product by ID
 *     tags: [Products]
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Product details
 *       404:
 *         description: Product not found
 */
router.get('/:id', cache(600), productController.getProductById); // Cache for 10 minutes

/**
 * @swagger
 * /products:
 *   post:
 *     summary: Create a new product
 *     tags: [Products]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - title
 *               - brand
 *               - price
 *             properties:
 *               title:
 *                 type: string
 *               brand:
 *                 type: string
 *               price:
 *                 type: number
 *               ingredients:
 *                 type: string
 *               image_url:
 *                 type: string
 *     responses:
 *       201:
 *         description: Product created
 */
router.post('/', protect, validate(createProductSchema), productController.createProduct);

module.exports = router;
