const axios = require('axios');

async function testApi() {
    const searchTerms = 'acne cleanser treatment';
    const limit = 5;
    const url = `https://world.openbeautyfacts.org/cgi/search.pl?search_terms=${encodeURIComponent(searchTerms)}&search_simple=1&action=process&json=1&page_size=${limit}`;

    console.log('Requesting:', url);
    try {
        const response = await axios.get(url, {
            timeout: 10000,
            headers: { 'User-Agent': 'GlowmanceApp-Test/1.0' }
        });

        if (response.data && response.data.products) {
            console.log(`Success! Found ${response.data.products.length} products.`);
            response.data.products.forEach(p => {
                console.log(`- ${p.product_name} (${p.brands})`);
                console.log(`  Img: ${p.image_url || 'None'}`);
                console.log(`  Ing: ${p.ingredients_text ? 'Yes' : 'No'}`);
            });
        } else {
            console.log('Failed: No products in response data.');
        }

    } catch (error) {
        console.error('API Error:', error.message);
        if (error.response) console.error('Status:', error.response.status);
    }
}

testApi();
