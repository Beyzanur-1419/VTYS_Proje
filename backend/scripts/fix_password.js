const User = require('../src/models/User');
const sequelize = require('../src/config/db.postgres');

async function fixPassword() {
    try {
        await sequelize.authenticate();
        console.log('Database connection OK.');

        // List existing users to debug
        const users = await User.findAll({ attributes: ['email'] });
        console.log('Current users in DB:', users.map(u => u.email));

        const email = 'deneme@gmail.com';
        const newPassword = 'Deneme123';

        let user = await User.findOne({ where: { email } });

        if (!user) {
            console.log('User not found. Creating new user...');
            // Create user (hooks will hash password)
            user = await User.create({
                name: 'Deneme Test',
                email: email,
                password: newPassword,
                profileImageUrl: null,
                skinType: 'Normal',
                skinGoal: 'Hydration',
                age: 25
            });
            console.log('User created successfully.');
        } else {
            console.log(`Found user: ${user.email}. Updating password...`);
            user.password = newPassword;
            await user.save(); // Hooks will hash password
            console.log('Password updated successfully.');
        }

    } catch (error) {
        console.error('Error:', error);
    } finally {
        await sequelize.close();
    }
}

fixPassword();
