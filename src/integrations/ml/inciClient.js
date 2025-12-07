const axios = require('axios');
const cheerio = require('cheerio');

// Mock ingredient database for common products
const mockProducts = {
  'cerave-moisturizing-cream': {
    productName: 'Moisturizing Cream',
    brand: 'CeraVe',
    ingredients: [
      { name: 'Aqua / Water', function: 'solvent', safety: 'good', comedogenic: '0' },
      { name: 'Glycerin', function: 'skin-identical ingredient, moisturizer/humectant', safety: 'good', comedogenic: '0' },
      { name: 'Cetearyl Alcohol', function: 'emollient, viscosity controlling, emulsifying, surfactant/cleansing', safety: 'good', comedogenic: '2' },
      { name: 'Caprylic/Capric Triglyceride', function: 'emollient', safety: 'good', comedogenic: '1' },
      { name: 'Cetyl Alcohol', function: 'emollient, viscosity controlling, emulsifying, surfactant/cleansing', safety: 'good', comedogenic: '2' },
      { name: 'Ceramide NP', function: 'skin-identical ingredient', safety: 'superstar', comedogenic: '0' },
      { name: 'Ceramide AP', function: 'skin-identical ingredient', safety: 'superstar', comedogenic: '0' },
      { name: 'Ceramide EOP', function: 'skin-identical ingredient', safety: 'superstar', comedogenic: '0' },
      { name: 'Hyaluronic Acid', function: 'skin-identical ingredient, moisturizer/humectant', safety: 'superstar', comedogenic: '0' },
      { name: 'Niacinamide', function: 'skin brightening, anti-acne, cell-communicating ingredient, moisturizer/humectant', safety: 'superstar', comedogenic: '0' }
    ]
  },
  'la-roche-posay-toleriane-double-repair-face-moisturizer': {
    productName: 'Toleriane Double Repair Face Moisturizer',
    brand: 'La Roche-Posay',
    ingredients: [
      { name: 'Aqua / Water', function: 'solvent', safety: 'good', comedogenic: '0' },
      { name: 'Glycerin', function: 'skin-identical ingredient, moisturizer/humectant', safety: 'good', comedogenic: '0' },
      { name: 'Niacinamide', function: 'skin brightening, anti-acne, cell-communicating ingredient', safety: 'superstar', comedogenic: '0' },
      { name: 'Dimethicone', function: 'emollient', safety: 'good', comedogenic: '1' },
      { name: 'Ceramide-3', function: 'skin-identical ingredient', safety: 'superstar', comedogenic: '0' }
    ]
  },
  'neutrogena-hydro-boost-water-gel': {
    productName: 'Hydro Boost Water Gel',
    brand: 'Neutrogena',
    ingredients: [
      { name: 'Water', function: 'solvent', safety: 'good', comedogenic: '0' },
      { name: 'Dimethicone', function: 'emollient', safety: 'good', comedogenic: '1' },
      { name: 'Glycerin', function: 'moisturizer/humectant', safety: 'good', comedogenic: '0' },
      { name: 'Hyaluronic Acid', function: 'skin-identical ingredient, moisturizer/humectant', safety: 'superstar', comedogenic: '0' },
      { name: 'Olive Extract', function: 'antioxidant', safety: 'good', comedogenic: '2' }
    ]
  }
};

class InciDecoderClient {
  async getProductInfo(productSlug) {
    try {
      // Check if we have mock data for this product
      if (mockProducts[productSlug]) {
        console.log(`✅ Returning mock data for: ${productSlug}`);
        return {
          success: true,
          source: 'mock',
          ...mockProducts[productSlug]
        };
      }

      // Try to scrape from INCIdecoder (will likely fail due to JS rendering)
      const url = `https://incidecoder.com/products/${productSlug}`;

      const response = await axios.get(url, {
        headers: {
          'User-Agent':
            'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123 Safari/537.36',
        },
        timeout: 5000
      });

      const $ = cheerio.load(response.data);

      // Try multiple selector strategies
      const productName = $('h1').first().text().trim() || 
                         $('[class*="product"]').first().text().trim();
      
      const brand = $('a[class*="brand"]').first().text().trim() ||
                   $('.brand').first().text().trim();

      const ingredients = [];
      
      // Try to find ingredient elements
      $('[class*="ingred"]').each((i, el) => {
        const name = $(el).find('a, [class*="name"]').first().text().trim();
        if (name) {
          ingredients.push({
            name,
            function: $(el).find('[class*="function"]').text().trim() || 'N/A',
            safety: $(el).find('[class*="safety"]').text().trim() || 'N/A',
            comedogenic: $(el).find('[class*="comedogenic"]').text().trim() || 'N/A',
          });
        }
      });

      // If scraping worked, return real data
      if (productName && ingredients.length > 0) {
        return {
          success: true,
          source: 'scraped',
          productName,
          brand,
          ingredients,
        };
      }

      // Fallback: Return empty but valid response
      return {
        success: true,
        source: 'unavailable',
        productName: '',
        brand: '',
        ingredients: [],
        message: `Product "${productSlug}" not found in mock database. Add it to mockProducts or use Puppeteer for real scraping.`
      };

    } catch (err) {
      console.error('INCIdecoder error:', err.message);
      return { 
        success: false, 
        error: 'Failed to fetch ingredient data',
        message: err.message 
      };
    }
  }

  /**
   * Get list of available mock products
   */
  getAvailableProducts() {
    return Object.keys(mockProducts).map(slug => ({
      slug,
      name: mockProducts[slug].productName,
      brand: mockProducts[slug].brand
    }));
  }
}

module.exports = new InciDecoderClient();
