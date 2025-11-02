class MockAnalysisService {
  constructor() {
    this.skinConditions = [
      'Akne',
      'Kuruluk',
      'Kızarıklık',
      'Hassasiyet',
      'Pigmentasyon'
    ];

    this.recommendations = [
      'Nemlendirici',
      'Güneş Koruyucu',
      'Akne Kremi',
      'Yatıştırıcı Serum',
      'Leke Karşıtı Ürün'
    ];
  }

  async analyzeSkin() {
    // Mock analiz sonuçları üret
    const numberOfConditions = Math.floor(Math.random() * 3) + 1;
    const numberOfRecommendations = Math.floor(Math.random() * 3) + 1;

    const selectedConditions = this.getRandomElements(this.skinConditions, numberOfConditions);
    const selectedRecommendations = this.getRandomElements(this.recommendations, numberOfRecommendations);

    return {
      confidence: Math.random() * 0.3 + 0.7, // 0.7 - 1.0 arası
      conditions: selectedConditions,
      recommendations: selectedRecommendations,
      timestamp: new Date()
    };
  }

  getRandomElements(array, count) {
    const shuffled = [...array].sort(() => 0.5 - Math.random());
    return shuffled.slice(0, count);
  }
}

module.exports = new MockAnalysisService();