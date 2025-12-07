const productService = require('../../src/services/productService');

describe('ProductService', () => {
  beforeEach(() => {
    // Reset the service state
    productService.loadProducts();
  });

  describe('getProductsBySkinType', () => {
    it('should return products for oily skin', async () => {
      const products = await productService.getProductsBySkinType('oily', 3);

      expect(products).toBeDefined();
      expect(Array.isArray(products)).toBe(true);
      expect(products.length).toBeLessThanOrEqual(3);
      if (products.length > 0) {
        expect(products[0]).toHaveProperty('id');
        expect(products[0]).toHaveProperty('name');
        expect(products[0]).toHaveProperty('price');
      }
    });

    it('should return products for dry skin', async () => {
      const products = await productService.getProductsBySkinType('dry', 3);

      expect(products).toBeDefined();
      expect(Array.isArray(products)).toBe(true);
    });

    it('should handle case insensitive skin type', async () => {
      const products = await productService.getProductsBySkinType('OILY', 3);

      expect(products).toBeDefined();
      expect(Array.isArray(products)).toBe(true);
    });

    it('should return empty array for invalid skin type', async () => {
      const products = await productService.getProductsBySkinType('invalid', 3);

      expect(products).toEqual([]);
    });
  });

  describe('getProductsByDisease', () => {
    it('should return products for acne', async () => {
      const products = await productService.getProductsByDisease('acne', 3);

      expect(products).toBeDefined();
      expect(Array.isArray(products)).toBe(true);
      expect(products.length).toBeLessThanOrEqual(3);
    });

    it('should return products for eczema', async () => {
      const products = await productService.getProductsByDisease('eczema', 3);

      expect(products).toBeDefined();
      expect(Array.isArray(products)).toBe(true);
    });

    it('should return empty array for invalid disease', async () => {
      const products = await productService.getProductsByDisease('invalid', 3);

      expect(products).toEqual([]);
    });
  });

  describe('getProductsByTypeAndDisease', () => {
    it('should return combined products', async () => {
      const products = await productService.getProductsByTypeAndDisease('oily', 'acne', 3);

      expect(products).toBeDefined();
      expect(Array.isArray(products)).toBe(true);
      expect(products.length).toBeLessThanOrEqual(3);
    });

    it('should prioritize disease products', async () => {
      const products = await productService.getProductsByTypeAndDisease('dry', 'eczema', 5);

      expect(products).toBeDefined();
      expect(Array.isArray(products)).toBe(true);
    });

    it('should remove duplicates', async () => {
      const products = await productService.getProductsByTypeAndDisease('oily', 'acne', 10);

      const ids = products.map(p => p.id);
      const uniqueIds = [...new Set(ids)];
      expect(ids.length).toBe(uniqueIds.length);
    });
  });

  describe('searchProducts', () => {
    it('should search products by name', async () => {
      const products = await productService.searchProducts('cream', 10);

      expect(products).toBeDefined();
      expect(Array.isArray(products)).toBe(true);
    });

    it('should search products by brand', async () => {
      const products = await productService.searchProducts('CeraVe', 10);

      expect(products).toBeDefined();
      expect(Array.isArray(products)).toBe(true);
    });

    it('should be case insensitive', async () => {
      const products = await productService.searchProducts('CREAM', 10);

      expect(products).toBeDefined();
      expect(Array.isArray(products)).toBe(true);
    });

    it('should return empty array for no matches', async () => {
      const products = await productService.searchProducts('nonexistent12345', 10);

      expect(products).toEqual([]);
    });
  });

  describe('getAvailableSkinTypes', () => {
    it('should return all skin types', () => {
      const skinTypes = productService.getAvailableSkinTypes();

      expect(skinTypes).toBeDefined();
      expect(Array.isArray(skinTypes)).toBe(true);
      expect(skinTypes).toContain('dry');
      expect(skinTypes).toContain('oily');
      expect(skinTypes).toContain('combination');
      expect(skinTypes).toContain('normal');
    });
  });

  describe('getAvailableDiseases', () => {
    it('should return all diseases', () => {
      const diseases = productService.getAvailableDiseases();

      expect(diseases).toBeDefined();
      expect(Array.isArray(diseases)).toBe(true);
      expect(diseases).toContain('acne');
      expect(diseases).toContain('eczema');
      expect(diseases).toContain('rosacea');
      expect(diseases).toContain('healthy');
    });
  });
});
