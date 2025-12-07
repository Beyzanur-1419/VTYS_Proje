const axios = require('axios');
const cheerio = require('cheerio');

(async () => {
  try {
    const url = 'https://incidecoder.com/products/cerave-moisturizing-cream';
    
    const response = await axios.get(url, {
      headers: {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
      }
    });

    const $ = cheerio.load(response.data);
    
    console.log('=== Testing Selectors ===\n');
    
    // Test different selectors
    console.log('h1:', $('h1').first().text().trim());
    console.log('h1.product-name:', $('h1.product-name').text().trim());
    console.log('.product-name:', $('.product-name').text().trim());
    
    console.log('\nBrand selectors:');
    console.log('a.brand-name:', $('a.brand-name').text().trim());
    console.log('.brand-name:', $('.brand-name').text().trim());
    
    console.log('\nIngredient boxes:');
    console.log('.ingred-flex-box count:', $('.ingred-flex-box').length);
    console.log('.ingredient-item count:', $('.ingredient-item').length);
    console.log('[class*="ingredient"] count:', $('[class*="ingredient"]').length);
    
    // Print first 500 chars of HTML to see structure
    console.log('\n=== HTML Preview ===');
    console.log(response.data.substring(0, 1000));
    
  } catch (err) {
    console.error('Error:', err.message);
  }
})();
