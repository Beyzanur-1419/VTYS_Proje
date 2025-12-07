const express = require('express');
const router = express.Router();
const inciDecoderClient = require('../integrations/ml/inciClient');
const { getIngredients } = require('../controllers/inciController');

/**
 * @swagger
 * /inci/{slug}:
 *   get:
 *     summary: Get product ingredient information from INCIdecoder (mock data)
 *     tags: [INCI]
 *     parameters:
 *       - in: path
 *         name: slug
 *         required: true
 *         schema:
 *           type: string
 *         description: Product slug (e.g., cerave-moisturizing-cream)
 *     responses:
 *       200:
 *         description: Product ingredient information
 *       500:
 *         description: Server error
 */
router.get('/inci/:slug', async (req, res) => {
  try {
    const { slug } = req.params;
    const result = await inciDecoderClient.getProductInfo(slug);

    res.json(result);
  } catch (error) {
    res.status(500).json({ success: false, message: 'Error fetching INCI data' });
  }
});

/**
 * @swagger
 * /inci:
 *   post:
 *     summary: Scrape product ingredients by product name
 *     tags: [INCI]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               product:
 *                 type: string
 *                 description: Product name to search
 *     responses:
 *       200:
 *         description: Ingredient information
 *       400:
 *         description: Bad request
 *       500:
 *         description: Server error
 */
router.post('/inci', getIngredients);

module.exports = router;
