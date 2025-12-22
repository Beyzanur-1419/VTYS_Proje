const sequelize = require('../config/db.postgres');
// Require models to register them
const User = require('../models/User');
const Product = require('../models/Product');
const AnalysisHistory = require('../models/AnalysisHistory');
const Log = require('../models/Log');
const Medication = require('../models/Medication');
const SkinHistory = require('../models/SkinHistory');
const Recommendation = require('../models/Recommendation');
const Image = require('../models/Image');

const syncSchemaErd = async () => {
    try {
        await sequelize.authenticate();
        console.log('✅ Connected to DB');

        // Force constraints? No, alter: true should handle valid migrations usually.
        // If field names changed (like skinType field:'skin_type'), alter: true might add new column and keep old?
        // Let's rely on alter: true for now. If messy, we drop/clean. 
        // Given this is dev/test, clean might be cleaner but user might want to keep data?
        // "tabloyu bozdum" implies we can fix it.

        await sequelize.sync({ alter: true });
        console.log('✅ ERD Schema Sync Complete.');

        process.exit(0);

    } catch (error) {
        console.error('❌ Fatal Error:', error);
        process.exit(1);
    }
};

syncSchemaErd();
