const sequelize = require('../config/db.postgres');
const User = require('../models/User');
const AnalysisHistory = require('../models/AnalysisHistory');
// Import other models if needed to ensure full sync
// const Product = require('../models/Product'); 

async function sync() {
    try {
        await sequelize.authenticate();
        console.log('Syncing database...');
        // Sync User first, then AnalysisHistory
        await User.sync({ alter: true });
        await AnalysisHistory.sync({ alter: true });
        console.log('Database synced successfully.');
    } catch (error) {
        console.error('Sync error:', error);
    } finally {
        await sequelize.close();
    }
}

sync();
