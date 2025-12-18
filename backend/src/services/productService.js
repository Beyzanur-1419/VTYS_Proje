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

  /**
   * Get product recommendations based on conditions
   * @param {Array<string>} conditions - Array of conditions (e.g., ['acne', 'oily', 'eczema'])
   * @param {number} limit - Maximum number of products to return
   */
  async getRecommendations(conditions = [], limit = 10) {
    if (!conditions || conditions.length === 0) {
      // If no conditions, return trending/popular products
      return await this.getTrendingProducts(limit);
    }

    const allRecommendedProducts = [];
    const seenProductIds = new Set();

    // Process each condition
    for (const condition of conditions) {
      const normalizedCondition = condition.toLowerCase().trim();

      // Check if it's a skin type
      const skinTypes = ['dry', 'oily', 'combination', 'normal', 'sensitive'];
      if (skinTypes.includes(normalizedCondition)) {
        const products = await this.getProductsBySkinType(normalizedCondition, limit);
        products.forEach(product => {
          if (!seenProductIds.has(product.id)) {
            allRecommendedProducts.push(product);
            seenProductIds.add(product.id);
          }
        });
      }

      // Check if it's a disease/condition
      const diseases = ['acne', 'eczema', 'rosacea', 'healthy'];
      if (diseases.includes(normalizedCondition)) {
        const products = await this.getProductsByDisease(normalizedCondition, limit);
        products.forEach(product => {
          if (!seenProductIds.has(product.id)) {
            allRecommendedProducts.push(product);
            seenProductIds.add(product.id);
          }
        });
      }
    }

    // If no products found, return trending products as fallback
    if (allRecommendedProducts.length === 0) {
      return await this.getTrendingProducts(limit);
    }

    return allRecommendedProducts.slice(0, limit);
  }

  /**
   * Get advanced product recommendations based on full profile
   * @param {object} profile - { skinType, problems, sensitivity, acne, careLevel }
   * @param {number} limit - Maximum number of products to return
   */
  async getAdvancedRecommendations(profile, limit = 3) {
    const { skinType, problems = [], sensitivity, acne, careLevel } = profile;

    // Normalize inputs
    const userSkinType = (skinType || '').toLowerCase();
    const userProblems = (problems || []).map(p => p.toLowerCase());
    const isSensitive = sensitivity && sensitivity.toLowerCase().includes('yüksek');
    const isAcneProne = acne && (acne.toLowerCase().includes('var') || acne.toLowerCase().includes('yüksek') || acne.toLowerCase().includes('orta'));

    // Get all products to score them
    const allProducts = await this.getAllProducts(1000);

    // Scoring logic
    const scoredProducts = allProducts.map(product => {
      let score = 0;
      const pDesc = (product.description || '').toLowerCase();
      const pName = (product.name || '').toLowerCase();
      const pId = product.id.toLowerCase();

      // 1. Skin Type Match (High Priority)
      // Products in 'skinTypes' map generally belong to that type
      // But we are iterating a flat list, so we check ID or description
      if (pId.includes(userSkinType) || pDesc.includes(userSkinType)) {
        score += 50;
      } else if (pId.includes('normal') || pId.includes('all')) {
        score += 10; // Neutral products are okay
      } else if (userSkinType === 'combination' && (pId.includes('oily') || pId.includes('dry'))) {
        score += 20; // Combo skin can use oily or dry products sometimes
      } else {
        score -= 50; // Wrong skin type penalize
      }

      // 2. Problems Match (Very High Priority)
      userProblems.forEach(problem => {
        if (pDesc.includes(problem) || pName.includes(problem) || pId.includes(problem)) {
          score += 40;
        }
      });

      // 3. Sensitivity Check
      if (isSensitive) {
        if (pDesc.includes('hassas') || pDesc.includes('yatıştırıcı') || pDesc.includes('sensitive') || pDesc.includes('doğal')) {
          score += 30;
        }
        if (pDesc.includes('parfüm') || pDesc.includes('alkol')) {
          score -= 40;
        }
      }

      // 4. Acne Prone Check
      if (isAcneProne) {
        if (pDesc.includes('akne') || pDesc.includes('gözenek') || pDesc.includes('yağsız') || pDesc.includes('non-comedogenic')) {
          score += 30;
        }
        if (pName.includes('yağ') && !pName.includes('temizleme yağı') && !pName.includes('çay ağacı')) {
          // Avoid heavy oils for acne prone, except cleansing oils or tea tree
          score -= 30;
        }
      }

      return { product, score };
    });

    // Filter out negative scores and sort by highest score
    const recommended = scoredProducts
      .filter(item => item.score > 0)
      .sort((a, b) => b.score - a.score)
      .map(item => item.product);

    // Remove duplicates
    const uniqueRecommended = Array.from(new Set(recommended.map(p => p.id)))
      .map(id => recommended.find(p => p.id === id));

    // Fallback if no specific recommendations found
    if (uniqueRecommended.length === 0) {
      return this.getTrendingProducts(limit);
    }

    return uniqueRecommended.slice(0, limit);
  }

  /**
   * Get trending products
   * @param {number} limit - Maximum number of products to return
   */
  async getTrendingProducts(limit = 10) {
    // Get products from all categories and return most popular ones
    const allProducts = await this.getAllProducts(limit * 2);

    // Simple trending logic: return first N products
    // In production, this could be based on views, ratings, sales, etc.
    return allProducts.slice(0, limit);
  }

  /**
   * Create a new product
   * @param {object} productData - Product data
   */
  async createProduct(productData) {
    // In a real application, this would save to database
    // For now, return the product data with generated ID
    return {
      id: Date.now().toString(),
      ...productData,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
  }
}

module.exports = new ProductService();
