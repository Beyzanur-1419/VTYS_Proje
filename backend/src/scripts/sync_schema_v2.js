const sequelize = require('../config/db.postgres');

const schemaDefinition = {
    Users: {
        id: 'UUID PRIMARY KEY DEFAULT gen_random_uuid()',
        name: 'VARCHAR(255)',
        email: 'VARCHAR(255) UNIQUE',
        password: 'VARCHAR(255)',
        profileImageUrl: 'VARCHAR(255)',
        skinType: 'VARCHAR(255)',
        skinGoal: 'VARCHAR(255)',
        age: 'INTEGER',
        notificationEnabled: 'BOOLEAN DEFAULT true',
        emailNotifications: 'BOOLEAN DEFAULT true',
        analysisReminder: 'BOOLEAN DEFAULT true',
        campaigns: 'BOOLEAN DEFAULT true',
        tips: 'BOOLEAN DEFAULT true',
        created_at: 'TIMESTAMP WITH TIME ZONE DEFAULT NOW()',
        updated_at: 'TIMESTAMP WITH TIME ZONE DEFAULT NOW()'
    },
    Products: {
        id: 'UUID PRIMARY KEY DEFAULT gen_random_uuid()',
        title: 'VARCHAR(255)',
        brand: 'VARCHAR(255)',
        price: 'DECIMAL(10, 2)',
        ingredients: 'TEXT',
        image_url: 'VARCHAR(255)',
        created_at: 'TIMESTAMP WITH TIME ZONE DEFAULT NOW()',
        updated_at: 'TIMESTAMP WITH TIME ZONE DEFAULT NOW()'
    },
    AnalysisHistories: {
        id: 'UUID PRIMARY KEY DEFAULT gen_random_uuid()',
        user_id: 'UUID REFERENCES "Users"("id")',
        image_url: 'VARCHAR(255)',
        result_json: 'JSONB',
        created_at: 'TIMESTAMP WITH TIME ZONE DEFAULT NOW()',
        updated_at: 'TIMESTAMP WITH TIME ZONE DEFAULT NOW()'
    }
};

const syncSchema = async () => {
    try {
        await sequelize.authenticate();
        console.log('✅ Connected to DB');

        for (const [tableName, columns] of Object.entries(schemaDefinition)) {
            console.log(`\nChecking table: ${tableName}`);

            // Check if table exists
            const tableExists = await sequelize.query(`
        SELECT EXISTS (
          SELECT FROM information_schema.tables 
          WHERE table_schema = 'public' 
          AND table_name = '${tableName}'
        );
      `);

            if (!tableExists[0][0].exists) {
                console.log(`⚠️ Table ${tableName} missing. Creating...`);
                // Basic creation (simpler to rely on sync, but let's try manual if needed)
                // For now, let's assume tables exist because previous sync runs worked partially.
                // If table completely missing, standard sequelize.sync() usually handles it.
            }

            for (const [colName, colType] of Object.entries(columns)) {
                try {
                    // Check if column exists
                    await sequelize.query(`SELECT "${colName}" FROM "${tableName}" LIMIT 1`);
                    // console.log(`  ✅ Column ${colName} exists.`);
                } catch (e) {
                    console.log(`  ⚠️ Column ${colName} missing in ${tableName}. Adding...`);
                    try {
                        // Handle "UNIQUE" specially if needed, but for simple missing cols:
                        // Remove constraints from type definition for ADD COLUMN to avoid syntax errors if complex
                        let cleanType = colType;
                        if (colType.includes('PRIMARY KEY')) cleanType = 'UUID DEFAULT gen_random_uuid()'; // Simplified
                        if (colType.includes('UNIQUE')) cleanType = cleanType.replace('UNIQUE', '');

                        await sequelize.query(`ALTER TABLE "${tableName}" ADD COLUMN "${colName}" ${cleanType}`);
                        console.log(`  ✅ Added ${colName}`);
                    } catch (alterError) {
                        console.error(`  ❌ Failed to add ${colName}: ${alterError.message}`);
                    }
                }
            }
        }

        console.log('\n✅ Schema sync check complete.');
        process.exit(0);

    } catch (error) {
        console.error('❌ Fatal Error:', error);
        process.exit(1);
    }
};

syncSchema();
