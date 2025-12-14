const fs = require('fs');
const path = require('path');
const axios = require('axios');
const config = require('../config');

class ProductService {
  constructor() {
    this.productsData = null;
    this.loadProducts();
  }

  /**
   * Load products from JSON file
   */
  loadProducts() {
    try {
      const filePath = path.join(__dirname, '../data/products.json');
      const rawData = fs.readFileSync(filePath, 'utf-8');
      this.productsData = JSON.parse(rawData);
      console.log('✅ Product database loaded successfully');
    } catch (error) {
      console.error('❌ Failed to load product database:', error.message);
      this.productsData = { skinTypes: {}, diseases: {} };
    }
  }

  /**
   * Get all products
   * @param {number} limit - Maximum number of products to return
   */
  async getAllProducts(limit = 50) {
    const allProducts = [];
    
    // Collect all products from all categories
    if (this.productsData.skinTypes) {
      Object.values(this.productsData.skinTypes).forEach(products => {
        allProducts.push(...products);
      });
    }
    
    if (this.productsData.diseases) {
      Object.values(this.productsData.diseases).forEach(products => {
        allProducts.push(...products);
      });
    }
    
    // Remove duplicates based on id
    const uniqueProducts = Array.from(
      new Map(allProducts.map(p => [p.id, p])).values()
    );
    
    return uniqueProducts.slice(0, limit);
  }

  /**
   * Get products by skin type
   * @param {string} skinType - dry, oily, combination, normal
   * @param {number} limit - Maximum number of products to return
   */
  async getProductsBySkinType(skinType, limit = 3) {
    const normalizedType = skinType.toLowerCase();
    
    // Check if using external API
    if (config.PRODUCT_API_KEY && config.PRODUCT_API_URL && config.USE_MOCK_PRODUCTS !== 'true') {
      return await this.fetchFromExternalAPI({ skinType: normalizedType }, limit);
    }

    // Use mock data
    const products = this.productsData.skinTypes[normalizedType] || [];
    return products.slice(0, limit);
  }

  /**
   * Get products by disease
   * @param {string} disease - acne, eczema, rosacea, healthy
   * @param {number} limit - Maximum number of products to return
   */
  async getProductsByDisease(disease, limit = 3) {
    const normalizedDisease = disease.toLowerCase();
    
    // Check if using external API
    if (config.PRODUCT_API_KEY && config.PRODUCT_API_URL && config.USE_MOCK_PRODUCTS !== 'true') {
      return await this.fetchFromExternalAPI({ disease: normalizedDisease }, limit);
    }

    // Use mock data
    const products = this.productsData.diseases[normalizedDisease] || [];
    return products.slice(0, limit);
  }

  /**
   * Get products by both skin type and disease (combination)
   * @param {string} skinType - dry, oily, combination, normal
   * @param {string} disease - acne, eczema, rosacea, healthy
   * @param {number} limit - Maximum number of products to return
   */
  async getProductsByTypeAndDisease(skinType, disease, limit = 3) {
    const normalizedType = skinType.toLowerCase();
    const normalizedDisease = disease.toLowerCase();
    
    // Check if using external API
    if (config.PRODUCT_API_KEY && config.PRODUCT_API_URL && config.USE_MOCK_PRODUCTS !== 'true') {
      return await this.fetchFromExternalAPI({ 
        skinType: normalizedType, 
        disease: normalizedDisease 
      }, limit);
    }

    // Use mock data - combine products from both categories
    const typeProducts = this.productsData.skinTypes[normalizedType] || [];
    const diseaseProducts = this.productsData.diseases[normalizedDisease] || [];
    
    // Prioritize disease-specific products, then add skin type products
    const combined = [...diseaseProducts, ...typeProducts];
    
    // Remove duplicates based on product ID
    const unique = combined.filter((product, index, self) =>
      index === self.findIndex((p) => p.id === product.id)
    );
    
    return unique.slice(0, limit);
  }

  /**
   * Search products by keyword (for future use)
   * @param {string} keyword - Search term
   * @param {number} limit - Maximum number of products to return
   */
  async searchProducts(keyword, limit = 10) {
    // Check if using external API
    if (config.PRODUCT_API_KEY && config.PRODUCT_API_URL && config.USE_MOCK_PRODUCTS !== 'true') {
      return await this.fetchFromExternalAPI({ keyword }, limit);
    }

    // Search in mock data
    const allProducts = [
      ...Object.values(this.productsData.skinTypes).flat(),
      ...Object.values(this.productsData.diseases).flat()
    ];

    const lowerKeyword = keyword.toLowerCase();
    const results = allProducts.filter(product =>
      product.name.toLowerCase().includes(lowerKeyword) ||
      product.brand.toLowerCase().includes(lowerKeyword) ||
      product.description.toLowerCase().includes(lowerKeyword)
    );

    // Remove duplicates
    const unique = results.filter((product, index, self) =>
      index === self.findIndex((p) => p.id === product.id)
    );

    return unique.slice(0, limit);
  }

  /**
   * Fetch products from external API (if configured)
   * @param {object} params - Query parameters
   * @param {number} limit - Maximum number of products
   */
  async fetchFromExternalAPI(params, limit) {
    try {
      const response = await axios.get(config.PRODUCT_API_URL, {
        headers: {
          'Authorization': `Bearer ${config.PRODUCT_API_KEY}`,
          'Content-Type': 'application/json'
        },
        params: {
          ...params,
          limit
        },
        timeout: 5000
      });

      return response.data.products || [];
    } catch (error) {
      console.error('❌ External API request failed:', error.message);
      console.log('⚠️  Falling back to mock data...');
      
      // Fallback to mock data
      if (params.skinType && params.disease) {
        return await this.getProductsByTypeAndDisease(params.skinType, params.disease, limit);
      } else if (params.skinType) {
        return await this.getProductsBySkinType(params.skinType, limit);
      } else if (params.disease) {
        return await this.getProductsByDisease(params.disease, limit);
      } else if (params.keyword) {
        return await this.searchProducts(params.keyword, limit);
      }
      
      return [];
    }
  }

  /**
   * Get all available skin types
   */
  getAvailableSkinTypes() {
    return Object.keys(this.productsData.skinTypes);
  }

  /**
   * Get all available diseases
   */
  getAvailableDiseases() {
    return Object.keys(this.productsData.diseases);
  }
}

module.exports = new ProductService();
