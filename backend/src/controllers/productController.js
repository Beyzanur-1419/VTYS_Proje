const productService = require('../services/productService');

class ProductController {
  async getAllProducts(req, res, next) {
    try {
      const products = await productService.getAllProducts();
      res.status(200).json({ success: true, count: products.length, data: products });
    } catch (error) {
      next(error);
    }
  }

  async createProduct(req, res, next) {
    try {
      const product = await productService.createProduct(req.body);
      res.status(201).json({ success: true, data: product });
    } catch (error) {
      next(error);
    }
  }

  async getProductById(req, res, next) {
    try {
      const product = await productService.getProductById(req.params.id);
      if (!product) {
        return res.status(404).json({ success: false, message: 'Product not found' });
      }
      res.status(200).json({ success: true, data: product });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Get product recommendations
   * GET /api/v1/products/recommendations?conditions=acne,oily
   */
  async getRecommendations(req, res, next) {
    try {
      const { conditions } = req.query;
      const conditionsArray = conditions ? conditions.split(',').map(c => c.trim()) : [];
      
      const products = await productService.getRecommendations(conditionsArray);
      
      res.status(200).json({
        success: true,
        count: products.length,
        data: products,
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Get trending products
   * GET /api/v1/products/trending
   */
  async getTrendingProducts(req, res, next) {
    try {
      const limit = parseInt(req.query.limit) || 10;
      const products = await productService.getTrendingProducts(limit);
      
      res.status(200).json({
        success: true,
        count: products.length,
        data: products,
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Search products
   * GET /api/v1/products/search?q=moisturizer
   */
  async searchProducts(req, res, next) {
    try {
      const { q } = req.query;
      
      if (!q) {
        return res.status(400).json({
          success: false,
          message: 'Search query is required',
        });
      }

      const products = await productService.searchProducts(q);
      
      res.status(200).json({
        success: true,
        count: products.length,
        data: products,
      });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new ProductController();
