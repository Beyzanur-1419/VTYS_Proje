
const sequelize = require('../src/config/db.postgres');
const User = require('../src/models/User');
const AnalysisHistory = require('../src/models/AnalysisHistory');

async function seedData() {
    try {
        console.log('Connecting to database...');
        await sequelize.authenticate();
        console.log('Connection has been established successfully.');

        // Find the specific user
        const user = await User.findOne({ where: { email: 'test@gmail.com' } });

        if (!user) {
            console.error('User "test@gmail.com" not found in the database. Please ensure the user is registered.');
            process.exit(1);
        }

        console.log(`Found user: ${user.email} (ID: ${user.id})`);

        // DELETE existing history to prevent duplicates
        console.log('Deleting existing analysis history for user...');
        await AnalysisHistory.destroy({ where: { user_id: user.id } });
        console.log('Old history deleted.');

        // Sample Data
        // Order: Oldest -> Newest 
        // 1. Acne (Start) - 10 days ago
        // 2. Eczema/Healing (Middle) - 5 days ago
        // 3. Healthy (Goal/Today) - Today

        const samples = [
            {
                user_id: user.id,
                image_url: '/uploads/sample_acne.jpg', // STARTING POINT (Oldest)
                result_json: {
                    skin_type: 'Oily',
                    hasAcne: true,
                    acneLevel: 'High',
                    hasEczema: false,
                    hasRosacea: false,
                    isNormal: false,
                    confidence: 0.85,
                    recommendedProducts: [
                        {
                            id: 'prod3',
                            name: 'Salicylic Acid Cleanser',
                            brand: 'The Inkey List',
                            imageUrl: '/uploads/prod_acne_wash.jpg',
                            description: 'Exfoliating cleanser to reduce acne and oil.',
                            ingredients: ['Salicylic Acid', 'Zinc']
                        },
                        {
                            id: 'prod4',
                            name: 'Niacinamide 10% + Zinc 1%',
                            brand: 'The Ordinary',
                            imageUrl: '/uploads/prod_serum.jpg',
                            description: 'Serum to regulate sebum and minimize pores.',
                            ingredients: ['Niacinamide', 'Zinc PCA']
                        }
                    ]
                },
                created_at: new Date(new Date().setDate(new Date().getDate() - 10)) // 10 days ago
            },
            {
                user_id: user.id,
                image_url: '/uploads/sample_eczema.jpg', // MIDDLE POINT
                result_json: {
                    skin_type: 'Dry',
                    hasAcne: false,
                    hasEczema: true,
                    eczemaLevel: 'Mild',
                    hasRosacea: false,
                    isNormal: false,
                    confidence: 0.92,
                    recommendedProducts: [
                        {
                            id: 'prod5',
                            name: 'Lipikar Balm AP+',
                            brand: 'La Roche-Posay',
                            imageUrl: '/uploads/prod_lotion.jpg',
                            description: 'Soothing balm for dry and eczema-prone skin.',
                            ingredients: ['Shea Butter', 'Niacinamide']
                        }
                    ]
                },
                created_at: new Date(new Date().setDate(new Date().getDate() - 5)) // 5 days ago
            },
            {
                user_id: user.id,
                image_url: '/uploads/sample_healthy.jpg', // GOAL / TODAY
                result_json: {
                    skin_type: 'Normal',
                    hasAcne: false,
                    hasEczema: false,
                    hasRosacea: false,
                    isNormal: true,
                    confidence: 0.98,
                    recommendedProducts: [
                        {
                            id: 'prod1',
                            name: 'Hydrating Gentle Cleanser',
                            brand: 'CeraVe',
                            imageUrl: '/uploads/prod_cleanser.jpg',
                            description: 'Daily gentle cleanser for normal to dry skin.',
                            ingredients: ['Ceramides', 'Hyaluronic Acid']
                        },
                        {
                            id: 'prod2',
                            name: 'Daily Moisturizing Lotion',
                            brand: 'Cetaphil',
                            imageUrl: '/uploads/prod_lotion.jpg',
                            description: 'Lightweight moisturizer for all skin types.',
                            ingredients: ['Glycerin', 'Vitamin E']
                        }
                    ]
                },
                created_at: new Date() // Today
            }
        ];

        console.log('Inserting fresh sample analysis records...');

        for (const sample of samples) {
            await AnalysisHistory.create(sample);
        }

        console.log('✅ Successfully refilled analysis history!');
        process.exit(0);

    } catch (error) {
        console.error('Unable to connect to the database:', error);
        process.exit(1);
    }
}

seedData();
