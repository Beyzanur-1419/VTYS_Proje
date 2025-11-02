const { User, AnalysisHistory } = require('../models');
const AIImage = require('../models/aiImage.model');
const bcrypt = require('bcryptjs');

const users = [
  {
    id: '550e8400-e29b-41d4-a716-446655440000',
    email: 'test@example.com',
    password: 'password123', // Will be hashed
    name: 'Test User'
  }
];

const aiImages = [
  {
    userId: '550e8400-e29b-41d4-a716-446655440000',
    originalName: 'test-image-1.jpg',
    mimeType: 'image/jpeg',
    size: 1024,
    url: '/uploads/test-image-1.jpg',
    timestamp: new Date()
  },
  {
    userId: '550e8400-e29b-41d4-a716-446655440000',
    originalName: 'test-image-2.jpg',
    mimeType: 'image/jpeg',
    size: 2048,
    url: '/uploads/test-image-2.jpg',
    timestamp: new Date()
  }
];

const analysisHistories = [
  {
    id: '660e8400-e29b-41d4-a716-446655440000',
    userId: '550e8400-e29b-41d4-a716-446655440000',
    imageId: null, // Will be set after AIImage creation
    results: {
      confidence: 0.95,
      conditions: ['Acne', 'Dryness'],
      recommendations: ['Product A', 'Product B']
    },
    timestamp: new Date()
  },
  {
    id: '770e8400-e29b-41d4-a716-446655440000',
    userId: '550e8400-e29b-41d4-a716-446655440000',
    imageId: null, // Will be set after AIImage creation
    results: {
      confidence: 0.88,
      conditions: ['Redness', 'Sensitivity'],
      recommendations: ['Product C', 'Product D']
    },
    timestamp: new Date()
  }
];

async function seedDatabase() {
  try {
    // Hash passwords
    const hashedUsers = await Promise.all(users.map(async user => ({
      ...user,
      password: await bcrypt.hash(user.password, 12)
    })));

    // Create users
    await User.bulkCreate(hashedUsers);
    console.log('✅ Users seeded');

    // Create AI images
    const createdImages = await AIImage.insertMany(aiImages);
    console.log('✅ AI images seeded');

    // Update analysis histories with image IDs
    const updatedAnalysisHistories = analysisHistories.map((history, index) => ({
      ...history,
      imageId: createdImages[index]._id.toString()
    }));

    // Create analysis histories
    await AnalysisHistory.bulkCreate(updatedAnalysisHistories);
    console.log('✅ Analysis histories seeded');

    console.log('✨ Database seeding completed successfully');
  } catch (error) {
    console.error('❌ Error seeding database:', error);
    throw error;
  }
}

// Export for CLI usage
module.exports = seedDatabase;

// Run if called directly
if (require.main === module) {
  seedDatabase()
    .then(() => process.exit(0))
    .catch(error => {
      console.error(error);
      process.exit(1);
    });
}