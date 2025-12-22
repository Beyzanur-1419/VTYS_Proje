const sequelize = require('../config/db.postgres');
const { DataTypes } = require('sequelize');

async function fixSchema() {
    try {
        await sequelize.authenticate();
        console.log('✅ Connected to database.');

        const queryInterface = sequelize.getQueryInterface();
        const tableName = 'AnalysisHistories';

        // Check if table exists
        const tables = await queryInterface.showAllTables();
        if (!tables.includes(tableName)) {
            console.error(`❌ Table ${tableName} does not exist!`);
            return;
        }

        const tableInfo = await queryInterface.describeTable(tableName);
        console.log('Existing columns:', Object.keys(tableInfo));

        // 1. skin_issues
        if (!tableInfo.skin_issues) {
            console.log('⚠️ Adding missing column: skin_issues');
            await queryInterface.addColumn(tableName, 'skin_issues', {
                type: DataTypes.JSONB,
                allowNull: true
            });
            console.log('✅ Added skin_issues');
        } else {
            console.log('ℹ️ skin_issues already exists');
        }

        // 2. ai_score
        if (!tableInfo.ai_score) {
            console.log('⚠️ Adding missing column: ai_score');
            await queryInterface.addColumn(tableName, 'ai_score', {
                type: DataTypes.FLOAT,
                allowNull: true
            });
            console.log('✅ Added ai_score');
        } else {
            console.log('ℹ️ ai_score already exists');
        }

        // 3. recommended_products
        if (!tableInfo.recommended_products) {
            console.log('⚠️ Adding missing column: recommended_products');
            await queryInterface.addColumn(tableName, 'recommended_products', {
                type: DataTypes.JSONB,
                allowNull: true
            });
            console.log('✅ Added recommended_products');
        } else {
            console.log('ℹ️ recommended_products already exists');
        }

        // 4. Force sync check for other discrepancies (optional, safer to do manual adds above)
        // await sequelize.sync({ alter: true }); 

        console.log('🎉 Schema fix completed successfully!');

    } catch (error) {
        console.error('❌ Schema fix failed:', error);
    } finally {
        await sequelize.close();
    }
}

fixSchema();
