const sequelize = require('../config/db.postgres');
const User = require('../models/User');
const Product = require('../models/Product');
const AnalysisHistory = require('../models/AnalysisHistory');
const SkinHistory = require('../models/SkinHistory');
const Medication = require('../models/Medication');
const Log = require('../models/Log');
const bcrypt = require('bcryptjs');

// Helper to fetch data
const fetch = (...args) => import('node-fetch').then(({ default: fetch }) => fetch(...args));

const seedFullData = async () => {
    try {
        await sequelize.authenticate();
        console.log('✅ Connected to DB');

        // Schema is already synced via sync_schema_v2.js. 
        // Skipping sync here to avoid locks/errors.
        // await sequelize.sync({ alter: true });

        // --- 1. FETCH & SEED PRODUCTS FROM DUMMYJSON ---
        console.log('🌍 Fetching products from DummyJSON...');
        let products = [];
        try {
            // Build a robust product list merging skincare and maybe fragrances/others? Just skincare for now.
            const response = await fetch('https://dummyjson.com/products/category/skincare');
            const data = await response.json();
            const apiProducts = data.products || [];

            // Map to our DB model
            const mappedProducts = apiProducts.map(p => ({
                title: p.title,
                brand: p.brand || 'Generic',
                price: p.price,
                ingredients: p.description, // DummyJSON doesn't have ingredients, use description
                image_url: p.thumbnail
            }));

            // Add some custom specific ones for variety if needed
            const customProducts = [
                { title: 'Anti-Acne Serum', brand: 'DermaClear', price: 29.99, ingredients: 'Salicylic Acid, Niacinamide', image_url: 'https://cdn.dummyjson.com/product-images/1/thumbnail.jpg' },
                { title: 'Hydrating Moisturizer', brand: 'HydroBoost', price: 19.99, ingredients: 'Hyaluronic Acid, Ceramides', image_url: 'https://cdn.dummyjson.com/product-images/2/thumbnail.jpg' },
                { title: 'Retinol Night Cream', brand: 'AgeRewind', price: 45.00, ingredients: 'Retinol, Peptides', image_url: 'https://cdn.dummyjson.com/product-images/3/thumbnail.jpg' }
            ];

            products = [...mappedProducts, ...customProducts];

            // Bulk Create (upsert logic roughly)
            // For simplicity, we delete existing products to avoid duplicates or messy state for this seed
            await Product.destroy({ where: {}, truncate: true });
            await Product.bulkCreate(products);
            console.log(`✅ Seeded ${products.length} products.`);
        } catch (e) {
            console.error('⚠️ Failed to fetch/seed products:', e.message);
            // Fallback hardcoded if fetch fails
        }

        // Refresh products from DB to get IDs
        const allProducts = await Product.findAll();


        // --- 2. SEED USERS & ANALYSIS ---
        console.log('👤 Seeding Users and Histories...');

        // Define Scenarios
        const scenarios = [
            {
                email: 'akne@test.com',
                name: 'Akne Test Kullanıcısı',
                skinType: 'Yağlı',
                skinGoal: 'Akne Tedavisi',
                analysis: {
                    hasAcne: true,
                    hasEczema: false,
                    disease: 'Acne',
                    skin_type: 'Yağlı',
                    confidence: 0.92
                }
            },
            {
                email: 'kuru@test.com',
                name: 'Kuru Cilt Kullanıcısı',
                skinType: 'Kuru',
                skinGoal: 'Nemlendirme',
                analysis: {
                    hasAcne: false,
                    hasEczema: true,
                    disease: 'Eczema',
                    skin_type: 'Kuru',
                    confidence: 0.88
                }
            },
            {
                email: 'normal@test.com',
                name: 'Normal Cilt Kullanıcısı',
                skinType: 'Normal',
                skinGoal: 'Koruma',
                analysis: {
                    hasAcne: false,
                    hasEczema: false,
                    disease: 'Healthy',
                    skin_type: 'Normal',
                    confidence: 0.99
                }
            }
        ];

        for (const scenario of scenarios) {
            try {
                console.log(`Processing scenario: ${scenario.email}`);

                // Create/Find User
                // Delete if exists to force fresh start for this test script? 
                // Or just findOne. Let's start fresh for specific test emails.
                await User.destroy({ where: { email: scenario.email } });

                // We do NOT manually hash here because User model beforeCreate hook handles it
                // const salt = await bcrypt.genSalt(10);
                // const hashedPassword = await bcrypt.hash('123456', salt);

                const user = await User.create({
                    name: scenario.name,
                    email: scenario.email,
                    password: '123456', // Plain text, model will hash it
                    profileImageUrl: `https://ui-avatars.com/api/?name=${scenario.name.replace(' ', '+')}`,
                    skinType: scenario.skinType,
                    skinGoal: scenario.skinGoal,
                    age: 20 + Math.floor(Math.random() * 10)
                });
                console.log(`  User created: ${user.id}`);

                // Create Analysis History

                // Use local images if user wants to add them to 'uploads' folder
                // Mapping simple names to scenarios
                let localImage = '';
                if (scenario.disease === 'Acne') localImage = '/uploads/acne_sample.jpg';
                else if (scenario.disease === 'Eczema') localImage = '/uploads/eczema_sample.jpg';
                else localImage = '/uploads/healthy_sample.jpg';

                // We can use a dummy URL as fallback in the frontend if this 404s, 
                // but for DB we store the local path expectation.
                // Actually, let's keep the dummy URL as default but log instructions.
                const imageUrl = localImage;

                // Pick products based on condition
                let relevantProducts = [];

                if (scenario.disease === 'Acne') {
                    relevantProducts = allProducts.filter(p =>
                        p.title.toLowerCase().includes('acne') ||
                        p.title.toLowerCase().includes('serum') ||
                        p.title.toLowerCase().includes('oil') // specific valid keywords for dummyjson
                    ).slice(0, 3);
                } else if (scenario.disease === 'Eczema') {
                    relevantProducts = allProducts.filter(p =>
                        p.title.toLowerCase().includes('cream') ||
                        p.title.toLowerCase().includes('moisturizer') ||
                        p.title.toLowerCase().includes('dry')
                    ).slice(0, 3);
                } else {
                    // Normal / Healthy
                    relevantProducts = allProducts.filter(p =>
                        !p.title.toLowerCase().includes('acne')
                    ).sort(() => 0.5 - Math.random()).slice(0, 3); // Helper or just slice
                }

                // Fallback if filter found nothing ( DummyJSON might vary)
                if (relevantProducts.length === 0) {
                    relevantProducts = allProducts.slice(0, 3);
                }

                // Limit to 3
                if (relevantProducts.length > 3) relevantProducts = relevantProducts.slice(0, 3);

                // Construct result_json. 
                // NOTE: Frontend usually expects 'products' inside 'data' or similar. 
                // Check AnalysisResponse model... accessing result.data.result. 
                // AnalysisData has 'result' and 'products'.
                // AnalysisResult has 'skin_type', 'disease', etc.
                // We are storing 'result_json' in DB which usually maps to AnalysisData or AnalysisResult?
                // Looking at AnalysisHistory.js, it's just a JSONB. 
                // The backend endpoint typically constructs the full response.
                // Let's assume result_json needs to contain the core analysis fields.

                await AnalysisHistory.create({
                    user_id: user.id,
                    image_url: imageUrl, // User can place 'acne_sample.jpg' etc in uploads folder
                    result_json: {
                        ...scenario.analysis,
                        // We add recommended products references here if the app stores them in history
                        // (Or the app fetches them dynamically. For this dump, we'll confirm integration)
                        recommended_products: relevantProducts.map(p => ({
                            id: p.id,
                            title: p.title,
                            image_url: p.image_url,
                            price: p.price
                        }))
                    },
                    // Add required fields for ERD if strictly validated
                    skin_issues: scenario.analysis, // map parts of analysis
                    ai_score: scenario.analysis.confidence,
                    recommended_products: relevantProducts.map(p => p.title) // JSON or array
                });
                console.log(`  Analysis history created.`);

                // 3. Seed Extra ERD Data

                // Skin History
                await SkinHistory.create({
                    user_id: user.id,
                    condition: scenario.analysis.hasAcne ? 'Akne' : (scenario.analysis.hasEczema ? 'Eczema' : 'Normal'),
                    diagnosis_date: new Date(),
                    treatment: scenario.analysis.hasAcne ? 'Salisilik Asit' : 'Nemlendirici',
                    notes: 'Otomatik analiz sonucu oluşturuldu.'
                });
                console.log(`  SkinHistory created.`);

                // Medications (Randomly assign one)
                if (Math.random() > 0.5) {
                    await Medication.create({
                        user_id: user.id,
                        product_name: 'Dermatolog Kremi X',
                        start_date: new Date(Date.now() - 86400000 * 10), // 10 days ago
                        notes: 'Günde 2 kere'
                    });
                    console.log(`  Medication created.`);
                }

                // Action Log
                await Log.create({
                    user_id: user.id,
                    action: 'REGISTER',
                    timestamp: new Date()
                });
                await Log.create({
                    user_id: user.id,
                    action: 'LOGIN',
                    timestamp: new Date()
                });
                console.log(`  Logs created.`);

                console.log(`✅ Completed scenario for ${scenario.email}`);

            } catch (innerError) {
                console.error(`❌ Error processing scenario ${scenario.email}:`, innerError.message);
                console.error('Details:', JSON.stringify(innerError.errors || innerError, null, 2));
            }
        }

        console.log('\n✅ Full Seed Complete!');
        process.exit(0);

    } catch (error) {
        console.error('❌ Error seeding data:', error);
        process.exit(1);
    }
};

seedFullData();
