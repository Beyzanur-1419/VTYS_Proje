const productController = require('../../src/controllers/productController');
const productService = require('../../src/services/productService');

// Mock the productService
jest.mock('../../src/services/productService');

describe('ProductController', () => {
  let req, res, next;

  beforeEach(() => {
    req = {
      params: {},
      query: {},
      body: {},
      user: { id: 'user123' }
    };
    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis()
    };
    next = jest.fn();
    jest.clearAllMocks();
  });

  describe('getAllProducts', () => {
    it('should return all products successfully', async () => {
      const mockProducts = [
        { id: '1', name: 'Product 1', price: 100 },
        { id: '2', name: 'Product 2', price: 200 }
      ];
      productService.getAllProducts = jest.fn().mockResolvedValue(mockProducts);

      await productController.getAllProducts(req, res, next);

      expect(productService.getAllProducts).toHaveBeenCalled();
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        count: 2,
        data: mockProducts
      });
    });

    it('should handle errors', async () => {
      const error = new Error('Database error');
      productService.getAllProducts = jest.fn().mockRejectedValue(error);

      await productController.getAllProducts(req, res, next);

      expect(next).toHaveBeenCalledWith(error);
    });
  });

  describe('getProductById', () => {
    it('should return product by id', async () => {
      const mockProduct = { id: '1', name: 'Product 1', price: 100 };
      req.params.id = '1';
      productService.getProductById = jest.fn().mockResolvedValue(mockProduct);

      await productController.getProductById(req, res, next);

      expect(productService.getProductById).toHaveBeenCalledWith('1');
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        data: mockProduct
      });
    });

    it('should return 404 if product not found', async () => {
      req.params.id = '999';
      productService.getProductById = jest.fn().mockResolvedValue(null);

      await productController.getProductById(req, res, next);

      expect(res.status).toHaveBeenCalledWith(404);
      expect(res.json).toHaveBeenCalledWith({
        success: false,
        message: 'Product not found'
      });
    });
  });

  describe('createProduct', () => {
    it('should create product successfully', async () => {
      const newProduct = { title: 'New Product', brand: 'Brand', price: 150 };
      const createdProduct = { id: '3', ...newProduct };
      req.body = newProduct;
      productService.createProduct = jest.fn().mockResolvedValue(createdProduct);

      await productController.createProduct(req, res, next);

      expect(productService.createProduct).toHaveBeenCalledWith(newProduct);
      expect(res.status).toHaveBeenCalledWith(201);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        data: createdProduct
      });
    });
  });

  describe('searchProducts', () => {
    it('should search products successfully', async () => {
      const mockProducts = [{ id: '1', name: 'Moisturizer' }];
      req.query.q = 'moisturizer';
      productService.searchProducts = jest.fn().mockResolvedValue(mockProducts);

      await productController.searchProducts(req, res, next);

      expect(productService.searchProducts).toHaveBeenCalledWith('moisturizer');
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        count: 1,
        data: mockProducts
      });
    });

    it('should return 400 if query is missing', async () => {
      req.query.q = '';

      await productController.searchProducts(req, res, next);

      expect(res.status).toHaveBeenCalledWith(400);
      expect(res.json).toHaveBeenCalledWith({
        success: false,
        message: 'Search query is required'
      });
    });
  });

  describe('getRecommendations', () => {
    it('should return recommendations', async () => {
      const mockProducts = [{ id: '1', name: 'Acne Cream' }];
      req.query.conditions = 'acne,oily';
      productService.getRecommendations = jest.fn().mockResolvedValue(mockProducts);

      await productController.getRecommendations(req, res, next);

      expect(productService.getRecommendations).toHaveBeenCalledWith(['acne', 'oily']);
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        count: 1,
        data: mockProducts
      });
    });
  });

  describe('getTrendingProducts', () => {
    it('should return trending products with default limit', async () => {
      const mockProducts = [{ id: '1', name: 'Trending Product' }];
      productService.getTrendingProducts = jest.fn().mockResolvedValue(mockProducts);

      await productController.getTrendingProducts(req, res, next);

      expect(productService.getTrendingProducts).toHaveBeenCalledWith(10);
      expect(res.status).toHaveBeenCalledWith(200);
    });

    it('should return trending products with custom limit', async () => {
      const mockProducts = [{ id: '1', name: 'Trending Product' }];
      req.query.limit = '5';
      productService.getTrendingProducts = jest.fn().mockResolvedValue(mockProducts);

      await productController.getTrendingProducts(req, res, next);

      expect(productService.getTrendingProducts).toHaveBeenCalledWith(5);
    });
  });
});
