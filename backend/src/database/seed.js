const sequelize = require('../config/db.postgres');
const mongoose = require('mongoose');
const Product = require('../models/Product');
const User = require('../models/User');
const AnalysisHistory = require('../models/AnalysisHistory');
const ImageLog = require('../models/ImageLog');
const bcrypt = require('bcryptjs');
const connectMongo = require('../config/db.mongo');

const seedData = async () => {
  try {
    // Connect to databases
    await sequelize.authenticate();
    console.log('✅ Connected to PostgreSQL');
    
    await connectMongo();
    console.log('✅ Connected to MongoDB');

    // Sync PostgreSQL models
    await sequelize.sync({ alter: true });
    console.log('✅ PostgreSQL models synced');

    // ==================== USERS ====================
    console.log('\n📝 Seeding Users...');
    const users = [
      { name: 'Test User', email: 'test@example.com', password: 'password123' },
      { name: 'Alice Johnson', email: 'alice@example.com', password: 'password123' },
      { name: 'Bob Smith', email: 'bob@example.com', password: 'password123' },
      { name: 'Carol White', email: 'carol@example.com', password: 'password123' },
      { name: 'David Brown', email: 'david@example.com', password: 'password123' }
    ];

    const createdUsers = [];
    for (const userData of users) {
      const existingUser = await User.findOne({ where: { email: userData.email } });
      if (!existingUser) {
        const hashedPassword = await bcrypt.hash(userData.password, 10);
        const user = await User.create({
          ...userData,
          password: hashedPassword
        });
        createdUsers.push(user);
        console.log(`  ✅ Created user: ${userData.email}`);
      } else {
        createdUsers.push(existingUser);
        console.log(`  ⏭️  User exists: ${userData.email}`);
      }
    }

    // ==================== PRODUCTS ====================
    console.log('\n📝 Seeding Products...');
    const productCount = await Product.count();
    if (productCount === 0) {
      const products = [
        // Moisturizers
        { title: 'Hydrating Face Cream', brand: 'CeraVe', price: 29.99, ingredients: 'Water, Glycerin, Hyaluronic Acid, Ceramides', category: 'Moisturizer' },
        { title: 'Ultra Facial Cream', brand: "Kiehl's", price: 45.00, ingredients: 'Squalane, Glacial Glycoprotein, Desert Plant Extract', category: 'Moisturizer' },
        { title: 'Moisture Surge', brand: 'Clinique', price: 52.00, ingredients: 'Aloe Water, Caffeine, Hyaluronic Acid', category: 'Moisturizer' },
        
        // Serums
        { title: 'Vitamin C Serum', brand: 'The Ordinary', price: 15.50, ingredients: 'Ascorbic Acid, Vitamin E, Ferulic Acid', category: 'Serum' },
        { title: 'Niacinamide 10% + Zinc 1%', brand: 'The Ordinary', price: 12.00, ingredients: 'Niacinamide, Zinc PCA', category: 'Serum' },
        { title: 'Advanced Night Repair', brand: 'Estée Lauder', price: 98.00, ingredients: 'Bifida Ferment Lysate, Hyaluronic Acid', category: 'Serum' },
        
        // Cleansers
        { title: 'Gentle Cleanser', brand: 'La Roche-Posay', price: 18.50, ingredients: 'Thermal Spring Water, Glycerin', category: 'Cleanser' },
        { title: 'Foaming Facial Cleanser', brand: 'CeraVe', price: 14.99, ingredients: 'Ceramides, Niacinamide, Hyaluronic Acid', category: 'Cleanser' },
        { title: 'Oil Cleanser', brand: 'DHC', price: 28.00, ingredients: 'Olive Oil, Vitamin E', category: 'Cleanser' },
        
        // Toners
        { title: 'Alcohol-Free Toner', brand: "Thayers", price: 10.95, ingredients: 'Witch Hazel, Aloe Vera, Rose Water', category: 'Toner' },
        { title: 'Glycolic Acid Toner', brand: 'The Ordinary', price: 8.70, ingredients: 'Glycolic Acid, Aloe Vera, Ginseng', category: 'Toner' },
        
        // Sunscreens
        { title: 'Anthelios Sunscreen SPF 50', brand: 'La Roche-Posay', price: 35.99, ingredients: 'Avobenzone, Titanium Dioxide', category: 'Sunscreen' },
        { title: 'UV Clear SPF 46', brand: 'EltaMD', price: 37.00, ingredients: 'Zinc Oxide, Niacinamide', category: 'Sunscreen' },
        
        // Acne Treatment
        { title: 'Acne Treatment Gel', brand: 'Neutrogena', price: 9.99, ingredients: 'Benzoyl Peroxide 2.5%', category: 'Treatment' },
        { title: 'Salicylic Acid Solution 2%', brand: 'The Ordinary', price: 5.80, ingredients: 'Salicylic Acid, Witch Hazel', category: 'Treatment' },
        
        // Eye Creams
        { title: 'Eye Cream', brand: 'CeraVe', price: 19.99, ingredients: 'Ceramides, Hyaluronic Acid, Niacinamide', category: 'Eye Care' },
        { title: 'Retinol Eye Cream', brand: 'RoC', price: 24.99, ingredients: 'Retinol, Hyaluronic Acid', category: 'Eye Care' },
        
        // Masks
        { title: 'Clay Mask', brand: 'Aztec Secret', price: 12.95, ingredients: 'Bentonite Clay', category: 'Mask' },
        { title: 'Sheet Mask - Hydrating', brand: 'Mediheal', price: 2.50, ingredients: 'Hyaluronic Acid, Collagen', category: 'Mask' },
        
        // Exfoliants
        { title: 'AHA 30% + BHA 2% Peeling Solution', brand: 'The Ordinary', price: 7.20, ingredients: 'Glycolic Acid, Lactic Acid, Salicylic Acid', category: 'Exfoliant' },
        { title: 'Gentle Scrub', brand: 'St. Ives', price: 6.49, ingredients: 'Apricot Extract, Walnut Shell Powder', category: 'Exfoliant' }
      ];

      await Product.bulkCreate(products);
      console.log(`  ✅ Created ${products.length} products`);
    } else {
      console.log(`  ⏭️  Products already exist (${productCount} products)`);
    }

    // ==================== ANALYSIS HISTORY ====================
    console.log('\n📝 Seeding Analysis History...');
    const analysisCount = await AnalysisHistory.count();
    if (analysisCount === 0 && createdUsers.length > 0) {
      const skinTypes = ['Oily', 'Dry', 'Combination', 'Normal'];
      const diseases = ['Acne', 'Eczema', 'Rosacea', 'Healthy'];
      
      const analyses = [];
      for (let i = 0; i < createdUsers.length; i++) {
        const user = createdUsers[i];
        // Create 3 analyses per user
        for (let j = 0; j < 3; j++) {
          analyses.push({
            user_id: user.id,
            image_url: `/uploads/sample_${i}_${j}.jpg`,
            result_json: {
              skin_type: skinTypes[Math.floor(Math.random() * skinTypes.length)],
              disease: diseases[Math.floor(Math.random() * diseases.length)],
              confidence: 0.75 + Math.random() * 0.2,
              severity: ['Mild', 'Moderate', 'Severe'][Math.floor(Math.random() * 3)]
            }
          });
        }
      }

      await AnalysisHistory.bulkCreate(analyses);
      console.log(`  ✅ Created ${analyses.length} analysis records`);
    } else {
      console.log(`  ⏭️  Analysis history already exists (${analysisCount} records)`);
    }

    // ==================== IMAGE LOGS (MongoDB) ====================
    console.log('\n📝 Seeding Image Logs...');
    const imageCount = await ImageLog.countDocuments();
    if (imageCount === 0 && createdUsers.length > 0) {
      const imageLogs = [];
      for (let i = 0; i < createdUsers.length; i++) {
        const user = createdUsers[i];
        // Create 3 image logs per user
        for (let j = 0; j < 3; j++) {
          imageLogs.push({
            userId: user.id,
            filename: `sample_${i}_${j}.jpg`,
            originalName: `user_photo_${j + 1}.jpg`,
            mimeType: 'image/jpeg',
            size: 150000 + Math.floor(Math.random() * 100000),
            path: `/uploads/sample_${i}_${j}.jpg`,
            url: `/uploads/sample_${i}_${j}.jpg`,
            detected_type: ['Oily', 'Dry', 'Combination', 'Normal'][Math.floor(Math.random() * 4)],
            detected_disease: ['Acne', 'Eczema', 'Rosacea', 'Healthy'][Math.floor(Math.random() * 4)],
            raw_output: {
              confidence: 0.75 + Math.random() * 0.2,
              timestamp: new Date()
            }
          });
        }
      }

      await ImageLog.insertMany(imageLogs);
      console.log(`  ✅ Created ${imageLogs.length} image logs`);
    } else {
      console.log(`  ⏭️  Image logs already exist (${imageCount} records)`);
    }

    console.log('\n✅ Seeding completed successfully!');
    console.log('\n📊 Summary:');
    console.log(`  Users: ${await User.count()}`);
    console.log(`  Products: ${await Product.count()}`);
    console.log(`  Analysis History: ${await AnalysisHistory.count()}`);
    console.log(`  Image Logs: ${await ImageLog.countDocuments()}`);
    
    process.exit(0);
  } catch (error) {
    console.error('❌ Seeding failed:', error);
    process.exit(1);
  }
};

seedData();
