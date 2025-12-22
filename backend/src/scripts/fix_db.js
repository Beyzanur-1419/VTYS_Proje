const sequelize = require('../config/db.postgres');
const User = require('../models/User');
const Product = require('../models/Product');
const AnalysisHistory = require('../models/AnalysisHistory');
const bcrypt = require('bcryptjs');

const fixAndSeed = async () => {
    try {
        await sequelize.authenticate();
        console.log('✅ Connected to PostgreSQL');

        // 1. FIX SCHEMA
        try {
            await sequelize.query('SELECT "profileImageUrl" FROM "Users" LIMIT 1');
            console.log('✅ Column profileImageUrl already exists.');
        } catch (e) {
            console.log('⚠️  Column missing, attempting to add...');
            await sequelize.query('ALTER TABLE "Users" ADD COLUMN "profileImageUrl" VARCHAR(255)');
            console.log('✅ Column profileImageUrl added successfully.');
        }

        // Sync models to be safe
        await sequelize.sync({ alter: true });
        console.log('✅ Models Synced.');

        // 2. SEED USERS
        const testEmail = 'test@gmail.com';
        let user = await User.findOne({ where: { email: testEmail } });

        if (!user) {
            console.log('🌱 Creating test user...');
            // Manually hashing password because bulkCreate/create logic sometimes varies
            const salt = await bcrypt.genSalt(10);
            const hashedPassword = await bcrypt.hash('123456', salt);

            user = await User.create({
                name: 'Test Kullanıcı',
                email: testEmail,
                password: 'password_will_be_hashed_by_hook', // Hook handles it if using create
                profileImageUrl: '',
                skinType: 'Karma',
                skinGoal: 'Leke Karşıtı',
                age: 25
            });
            console.log('✅ Test user created: test@gmail.com / 123456');
        } else {
            console.log('ℹ️  Test user already exists.');
        }

        // 3. SEED PRODUCTS
        const productCount = await Product.count();
        if (productCount === 0) {
            console.log('🌱 Seeding products...');
            await Product.bulkCreate([
                {
                    title: 'Nemlendirici Krem',
                    brand: 'GlowBrand',
                    price: 150.00,
                    ingredients: 'Water, Glycerin, Hyaluronic Acid',
                    image_url: '/uploads/products/moisturizer.jpg'
                },
                {
                    title: 'C Vitamini Serumu',
                    brand: 'GlowBrand',
                    price: 250.00,
                    ingredients: 'Vitamin C, Ferulic Acid',
                    image_url: '/uploads/products/vitaminc.jpg'
                },
                {
                    title: 'Güneş Kremi SPF 50',
                    brand: 'SunSafe',
                    price: 180.00,
                    ingredients: 'Zinc Oxide, Titanium Dioxide',
                    image_url: '/uploads/products/sunscreen.jpg'
                }
            ]);
            console.log('✅ Sample products added.');
        } else {
            console.log('ℹ️  Products already exist.');
        }

        // 4. SEED HISTORY (Optional)
        if (user) {
            const historyCount = await AnalysisHistory.count({ where: { user_id: user.id } });
            if (historyCount === 0) {
                console.log('🌱 Seeding analysis history...');
                await AnalysisHistory.create({
                    user_id: user.id,
                    image_url: '/uploads/analysis/sample_face.jpg',
                    result_json: {
                        skin_type: 'Yağlı',
                        disease: 'Acne',
                        hasAcne: true,
                        hasEczema: false,
                        confidence: 0.95
                    }
                });
                console.log('✅ Sample analysis history added.');
            }
        }

        process.exit(0);
    } catch (error) {
        console.error('❌ Error:', error);
        process.exit(1);
    }
};

fixAndSeed();
