const puppeteer = require('puppeteer');
const cheerio = require('cheerio');
const axios = require('axios');
const Product = require('../models/product.model');

class ProductScraperService {
  constructor() {
    this.sources = [
      {
        name: 'watsons',
        baseUrl: 'https://www.watsons.com.tr',
        selectors: {
          productName: '.product-name',
          price: '.product-price',
          brand: '.product-brand',
          description: '.product-description',
          ingredients: '.ingredients-list'
        }
      },
      {
        name: 'gratis',
        baseUrl: 'https://www.gratis.com',
        selectors: {
          productName: '.product-name',
          price: '.product-price',
          brand: '.brand-name',
          description: '.product-desc',
          ingredients: '.ingredients'
        }
      }
    ];
  }

  async scrapeProducts(category, condition) {
    const browser = await puppeteer.launch({ headless: true });
    const results = [];

    try {
      for (const source of this.sources) {
        const products = await this._scrapeSource(browser, source, category, condition);
        results.push(...products);
      }

      // Verileri veritabanına kaydet
      await this._saveProducts(results);

      return results;
    } finally {
      await browser.close();
    }
  }

  async _scrapeSource(browser, source, category, condition) {
    const page = await browser.newPage();
    const products = [];

    try {
      // Kullanıcı ajanını ayarla
      await page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36');

      // Arama URL'sini oluştur
      const searchUrl = this._buildSearchUrl(source, category, condition);
      await page.goto(searchUrl, { waitUntil: 'networkidle0' });

      // Ürün detaylarını çek
      const productLinks = await page.$$eval('a.product-link', links => 
        links.map(link => link.href)
      );

      for (const link of productLinks.slice(0, 5)) { // Her kaynaktan en fazla 5 ürün
        const product = await this._scrapeProductDetails(page, link, source.selectors);
        if (product) {
          products.push({
            ...product,
            source: source.name,
            sourceUrl: link,
            category,
            suitableForConditions: [condition]
          });
        }
      }
    } catch (error) {
      console.error(`Error scraping ${source.name}:`, error);
    }

    return products;
  }

  async _scrapeProductDetails(page, url, selectors) {
    try {
      await page.goto(url, { waitUntil: 'networkidle0' });

      return await page.evaluate(selectors => ({
        name: document.querySelector(selectors.productName)?.textContent?.trim(),
        brand: document.querySelector(selectors.brand)?.textContent?.trim(),
        price: this._extractPrice(document.querySelector(selectors.price)?.textContent),
        description: document.querySelector(selectors.description)?.textContent?.trim(),
        ingredients: Array.from(document.querySelectorAll(selectors.ingredients))
          .map(el => el.textContent.trim())
          .filter(Boolean)
      }), selectors);
    } catch (error) {
      console.error('Error scraping product details:', error);
      return null;
    }
  }

  _buildSearchUrl(source, category, condition) {
    // Her kaynak için özel URL oluşturma mantığı
    const searchParams = new URLSearchParams({
      q: `${category} ${condition}`,
      sort: 'popularity'
    });

    return `${source.baseUrl}/search?${searchParams.toString()}`;
  }

  async _saveProducts(products) {
    // Ürünleri veritabanına kaydet
    const validProducts = products.filter(p => p && p.name && p.brand);
    
    for (const product of validProducts) {
      try {
        await Product.findOrCreate({
          where: {
            name: product.name,
            brand: product.brand
          },
          defaults: {
            ...product,
            metadata: {
              lastUpdated: new Date(),
              sourceUrl: product.sourceUrl
            }
          }
        });
      } catch (error) {
        console.error('Error saving product:', error);
      }
    }
  }

  _extractPrice(priceStr) {
    if (!priceStr) return null;
    // Fiyat string'inden sayısal değeri çıkar
    const match = priceStr.match(/[\d,.]+/);
    return match ? parseFloat(match[0].replace(',', '.')) : null;
  }
}

module.exports = new ProductScraperService();