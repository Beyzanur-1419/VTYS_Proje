const AnalysisHistoryService = require('./AnalysisHistoryService');
const ProductScraperService = require('./ProductScraperService');
const { AppError } = require('../utils/errors');

/**
 * Recommendation Service
 * Handles product recommendations based on user analysis history
 */
class RecommendationService {
  constructor() {
    this.analysisHistoryService = new AnalysisHistoryService();
    this.updateInterval = 1000 * 60 * 60 * 24; // 24 saat
    this.lastUpdate = null;
  }

  /**
   * Get user's analysis history
   * @param {string} userId - User UUID
   * @param {Object} options - Query options (limit, offset)
   * @returns {Promise<Array>} Array of analysis history records
   */
  async getUserHistory(userId, options = {}) {
    try {
      const limit = options.limit || 10; // Son 10 analizi al
      const offset = options.offset || 0;

      const analysis = await this.analysisHistoryService.getUserAnalysisHistory(
        userId,
        { limit, offset }
      );

      return analysis;
    } catch (error) {
      console.error('Error getting user history:', error);
      throw new AppError(
        `Failed to get user history: ${error.message}`,
        500
      );
    }
  }

  /**
   * Get personalized product recommendations based on user's analysis history
   * @param {string} userId - User UUID
   * @param {Object} options - Recommendation options (limit, includeTrending)
   * @returns {Promise<Object>} Recommended products with metadata
   */
  async getRecommendations(userId, options = {}) {
    try {
      const limit = options.limit || 10;
      const includeTrending = options.includeTrending !== false;

      // Kullanıcı geçmişini ve analizlerini al
      const analysisHistory = await this.getUserHistory(userId, {
        limit: 10,
        offset: 0,
      });

      // Eğer geçmiş yoksa trend ürünleri döndür
      if (!analysisHistory || analysisHistory.length === 0) {
        const trending = await this.getTrendingProducts();
        return {
          recommendations: trending,
          source: 'trending',
          message: 'No analysis history found. Showing trending products.',
          basedOnHistory: false,
        };
      }

      // Analiz sonuçlarından kategorileri ve etiketleri çıkar
      const { tags, conditions, recommendations } = this.extractInsights(analysisHistory);

      // Kullanıcının ilgi alanlarına göre öneriler
      const personalizedProducts = await this.findSimilarProducts(
        conditions,
        recommendations,
        { limit }
      );

      // Trend ürünlerle birleştir (eğer isteniyorsa)
      let finalRecommendations = personalizedProducts;
      if (includeTrending && personalizedProducts.length < limit) {
        const trending = await this.getTrendingProducts();
        const remaining = limit - personalizedProducts.length;
        finalRecommendations = [
          ...personalizedProducts,
          ...trending.slice(0, remaining),
        ];
      }

      return {
        recommendations: finalRecommendations.slice(0, limit),
        source: 'personalized',
        message: 'Personalized recommendations based on your analysis history',
        basedOnHistory: true,
        insights: {
          conditions: conditions.slice(0, 5),
          recommendations: recommendations.slice(0, 5),
          totalAnalyses: analysisHistory.length,
        },
      };
    } catch (error) {
      console.error('Error getting recommendations:', error);
      
      // Fallback to trending products on error
      try {
        const trending = await this.getTrendingProducts();
        return {
          recommendations: trending,
          source: 'trending',
          message: 'Error getting personalized recommendations. Showing trending products.',
          basedOnHistory: false,
          error: error.message,
        };
      } catch (fallbackError) {
        throw new AppError(
          `Failed to get recommendations: ${error.message}`,
          500
        );
      }
    }
  }

  /**
   * Update product database based on analysis results
   * @param {Array} tags - Array of conditions/recommendations to search for
   */
  async _updateProductDatabase(tags) {
    // Son güncelleme üzerinden 24 saat geçtiyse güncelle
    if (!this.lastUpdate || Date.now() - this.lastUpdate > this.updateInterval) {
      try {
        // Tags'dan conditions ve recommendations'ı ayır
        const conditions = tags.filter((tag) =>
          ["Akne", "Kuruluk", "Hassasiyet", "Kızarıklık", "Hiperpigmentasyon", "Yaşlanma"].includes(
            tag
          )
        );

        // Her condition için uygun kategorilerdeki ürünleri tara
        for (const condition of conditions) {
          const categories = this._getCategoriesForCondition(condition);
          for (const category of categories) {
            try {
              await ProductScraperService.scrapeProducts(category, condition);
            } catch (scrapeError) {
              console.error(
                `Error scraping products for ${category}/${condition}:`,
                scrapeError.message
              );
              // Continue with next category even if one fails
            }
          }
        }

        this.lastUpdate = Date.now();
      } catch (error) {
        console.error("Ürün veritabanı güncelleme hatası:", error);
        // Don't throw - product database update should not block recommendations
      }
    }
  }

  /**
   * Get trending/popular products
   * @param {Object} options - Options (limit)
   * @returns {Promise<Array>} Array of trending products
   */
  async getTrendingProducts(options = {}) {
    try {
      const limit = options.limit || 10;

      // Mock trending products - gerçek implementasyon için Product model gerekli
      const trendingProducts = [
        {
          id: "trend-1",
          name: "Nemlendirici Serum - Hyaluronik Asit",
          category: "Nemlendirici",
          price: 299,
          rating: 4.5,
          imageUrl: "https://example.com/products/trend-1.jpg",
          tags: ["Nemlendirici", "Hyaluronik Asit"],
          isTrending: true,
        },
        {
          id: "trend-2",
          name: "Güneş Koruyucu SPF 50+",
          category: "Güneş Koruyucu",
          price: 199,
          rating: 4.7,
          imageUrl: "https://example.com/products/trend-2.jpg",
          tags: ["Güneş Koruyucu", "SPF50"],
          isTrending: true,
        },
        {
          id: "trend-3",
          name: "Akne Karşıtı Temizleyici",
          category: "Yüz Temizleyici",
          price: 149,
          rating: 4.3,
          imageUrl: "https://example.com/products/trend-3.jpg",
          tags: ["Akne", "Temizleyici"],
          isTrending: true,
        },
        {
          id: "trend-4",
          name: "Vitamin C Aydınlatıcı Serum",
          category: "Serum",
          price: 349,
          rating: 4.6,
          imageUrl: "https://example.com/products/trend-4.jpg",
          tags: ["Vitamin C", "Aydınlatıcı"],
          isTrending: true,
        },
        {
          id: "trend-5",
          name: "Hassas Cilt Nemlendirici Krem",
          category: "Nemlendirici",
          price: 249,
          rating: 4.4,
          imageUrl: "https://example.com/products/trend-5.jpg",
          tags: ["Hassas Cilt", "Nemlendirici"],
          isTrending: true,
        },
      ];

      return trendingProducts.slice(0, limit);
    } catch (error) {
      console.error("Error getting trending products:", error);
      return [];
    }
  }

  /**
   * Find similar products based on conditions and recommendations
   * @param {Array} conditions - Skin conditions array
   * @param {Array} recommendations - Product recommendations array
   * @param {Object} options - Search options (limit, etc.)
   * @returns {Promise<Array>} Array of recommended products
   */
  async findSimilarProducts(conditions, recommendations, options = {}) {
    try {
      const limit = options.limit || 10;
      const allTags = [...conditions, ...recommendations].filter(Boolean);

      // Eğer tag yoksa boş array döndür
      if (allTags.length === 0) {
        return [];
      }

      // Ürün veritabanını güncelle (24 saatte bir)
      await this._updateProductDatabase(allTags);

      // Her koşul için uygun kategorileri bul
      const categories = new Set();
      conditions.forEach((condition) => {
        const conditionCategories = this._getCategoriesForCondition(condition);
        conditionCategories.forEach((cat) => categories.add(cat));
      });

      // Mock products - gerçek implementasyon için Product model gerekli
      // Burada Product model'den ürünler sorgulanacak
      const products = [];
      const categoryArray = Array.from(categories);

      // Her kategori için ürün önerileri oluştur (mock)
      categoryArray.forEach((category, index) => {
        if (products.length < limit) {
          products.push({
            id: `product-${index + 1}`,
            name: `${category} - Önerilen Ürün ${index + 1}`,
            category: category,
            price: Math.floor(Math.random() * 500) + 50,
            rating: (Math.random() * 2 + 3).toFixed(1), // 3-5 arası
            imageUrl: `https://example.com/products/${index + 1}.jpg`,
            tags: allTags.slice(0, 3),
            relevanceScore: Math.random() * 0.3 + 0.7, // 0.7-1.0 arası
          });
        }
      });

      // Recommendations'a göre de ürün ekle
      recommendations.forEach((recommendation, index) => {
        if (products.length < limit) {
          products.push({
            id: `rec-${index + 1}`,
            name: `${recommendation}`,
            category: recommendation,
            price: Math.floor(Math.random() * 400) + 50,
            rating: (Math.random() * 1.5 + 3.5).toFixed(1), // 3.5-5 arası
            imageUrl: `https://example.com/products/rec-${index + 1}.jpg`,
            tags: [recommendation],
            relevanceScore: Math.random() * 0.2 + 0.8, // 0.8-1.0 arası (daha yüksek)
            isRecommended: true,
          });
        }
      });

      // Relevance score'a göre sırala ve limit uygula
      return products
        .sort((a, b) => b.relevanceScore - a.relevanceScore)
        .slice(0, limit)
        .map(({ relevanceScore, ...product }) => product); // relevanceScore'u kaldır
    } catch (error) {
      console.error("Error finding similar products:", error);
      return []; // Hata durumunda boş array döndür
    }
  }

  /**
   * Extract insights from analysis history (conditions, recommendations, tags)
   * @param {Array} analysisHistory - Array of analysis history records
   * @returns {Object} Extracted insights with tags, conditions, and recommendations
   */
  extractInsights(analysisHistory) {
    const tagCount = {};
    const conditionCount = {};
    const recommendationCount = {};

    // Analiz sonuçlarından etiketler, koşullar ve öneriler çıkar
    analysisHistory?.forEach((item) => {
      if (item.results) {
        // Conditions (cilt durumları)
        if (Array.isArray(item.results.conditions)) {
          item.results.conditions.forEach((condition) => {
            conditionCount[condition] = (conditionCount[condition] || 0) + 2; // Daha fazla ağırlık
            tagCount[condition] = (tagCount[condition] || 0) + 2;
          });
        }

        // Recommendations (önerilen ürünler)
        if (Array.isArray(item.results.recommendations)) {
          item.results.recommendations.forEach((recommendation) => {
            recommendationCount[recommendation] = (recommendationCount[recommendation] || 0) + 1;
            tagCount[recommendation] = (tagCount[recommendation] || 0) + 1;
          });
        }
      }
    });

    // En sık kullanılan etiketleri sırala ve döndür
    const topTags = Object.entries(tagCount)
      .sort(([, a], [, b]) => b - a)
      .slice(0, 10)
      .map(([tag]) => tag);

    // En sık görülen koşulları sırala ve döndür
    const topConditions = Object.entries(conditionCount)
      .sort(([, a], [, b]) => b - a)
      .slice(0, 5)
      .map(([condition, count]) => ({ condition, count }));

    // En sık önerilen ürünleri sırala ve döndür
    const topRecommendations = Object.entries(recommendationCount)
      .sort(([, a], [, b]) => b - a)
      .slice(0, 5)
      .map(([recommendation, count]) => ({ recommendation, count }));

    return {
      tags: topTags,
      conditions: topConditions.map((item) => item.condition),
      recommendations: topRecommendations.map((item) => item.recommendation),
      conditionDetails: topConditions,
      recommendationDetails: topRecommendations,
    };
  }

  _getCategoriesForCondition(condition) {
    // Her cilt durumu için uygun ürün kategorileri
    const categoryMap = {
      'Akne': ['Akne Tedavisi', 'Yüz Temizleyici', 'Tonik'],
      'Kuruluk': ['Nemlendirici', 'Serum', 'Yüz Bakım'],
      'Hassasiyet': ['Hassas Cilt', 'Nemlendirici', 'Güneş Koruyucu'],
      'Kızarıklık': ['Hassas Cilt', 'Kızarıklık Karşıtı', 'Nemlendirici'],
      'Hiperpigmentasyon': ['Leke Karşıtı', 'Aydınlatıcı', 'Güneş Koruyucu'],
      'Yaşlanma': ['Anti-Aging', 'Serum', 'Gece Kremi'],
      // Diğer durumlar için kategoriler eklenebilir
    };

    return categoryMap[condition] || ['Yüz Bakım', 'Nemlendirici'];
  }
}

module.exports = RecommendationService;