const mongoose = require('mongoose');
const Config = require('../../config/config');
const AIImage = require('../../models/aiImage.model');

const sampleImages = [
  {
    userId: '550e8400-e29b-41d4-a716-446655440000', // Test kullanıcısı UUID'si
    originalName: 'sample1.jpg',
    mimeType: 'image/jpeg',
    size: 1024 * 100, // 100KB
    url: '/uploads/sample1.jpg',
    analysisResults: {
      confidence: 0.95,
      conditions: ['Akne', 'Kuruluk'],
      recommendations: ['Nemlendirici', 'Akne Kremi']
    },
    timestamp: new Date()
  },
  {
    userId: '550e8400-e29b-41d4-a716-446655440000',
    originalName: 'sample2.jpg',
    mimeType: 'image/jpeg',
    size: 1024 * 150, // 150KB
    url: '/uploads/sample2.jpg',
    analysisResults: {
      confidence: 0.88,
      conditions: ['Kızarıklık', 'Hassasiyet'],
      recommendations: ['Yatıştırıcı Krem', 'Güneş Kremi']
    },
    timestamp: new Date()
  }
];

async function seedMongoDB() {
  try {
    // MongoDB'ye bağlan
    await mongoose.connect(Config.MONGODB_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true
    });
    console.log('✅ MongoDB bağlantısı başarılı');

    // Mevcut verileri temizle
    await AIImage.deleteMany({});
    console.log('✅ Eski veriler temizlendi');

    // Örnek verileri ekle
    await AIImage.insertMany(sampleImages);
    console.log('✅ Örnek veriler eklendi');

    // Eklenen verileri kontrol et
    const count = await AIImage.countDocuments();
    console.log(`✨ Toplam ${count} örnek görsel eklendi`);

    // Bağlantıyı kapat
    await mongoose.connection.close();
    console.log('✅ MongoDB bağlantısı kapatıldı');

  } catch (error) {
    console.error('❌ MongoDB seed hatası:', error);
    process.exit(1);
  }
}

// Script doğrudan çalıştırıldıysa
if (require.main === module) {
  seedMongoDB()
    .then(() => process.exit(0))
    .catch(error => {
      console.error(error);
      process.exit(1);
    });
}

module.exports = seedMongoDB;