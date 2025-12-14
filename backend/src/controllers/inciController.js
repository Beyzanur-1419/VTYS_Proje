const axios = require("axios");
const cheerio = require("cheerio");

const getIngredients = async (req, res) => {
    try {
        const { product } = req.body;

        if (!product) {
            return res.status(400).json({ error: "Product name gerekli" });
        }

        // INCIdecoder product URL
        const formattedProduct = product.toLowerCase().replace(/\s+/g, "-");
        const url = `https://incidecoder.com/products/${formattedProduct}`;

        const response = await axios.get(url, {
            headers: {
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
            },
            timeout: 5000
        });
        
        const $ = cheerio.load(response.data);

        let ingredients = [];

        $(".iStuff").each((i, el) => {
            const name = $(el).find(".ingred-link").text().trim();
            const rating = $(el).find(".circle").attr("class") || "";
            const description = $(el).find(".ingred-details").text().trim();

            ingredients.push({
                name,
                rating,
                description
            });
        });

        if (ingredients.length === 0) {
            return res.status(404).json({ 
                message: "İçerik bulunamadı. Ürün adı yanlış olabilir.",
                tried_url: url
            });
        }

        return res.json({
            success: true,
            product,
            total: ingredients.length,
            ingredients
        });

    } catch (error) {
        return res.status(500).json({
            success: false,
            error: "INCIdecoder scrape hata verdi",
            details: error.message
        });
    }
};

module.exports = { getIngredients };
