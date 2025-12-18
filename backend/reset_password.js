const sequelize = require('./src/config/db.postgres');
const User = require('./src/models/User');

async function resetPassword() {
    try {
        await sequelize.authenticate();
        console.log('Database connected.');

        const email = 'beyz@gmail.com'; // User reported in logs
        const newPassword = '123456';

        const user = await User.findOne({ where: { email } });
        if (!user) {
            console.log(`User with email ${email} not found.`);
            process.exit(1);
        }

        user.password = newPassword; // Hook will hash this
        await user.save();

        console.log(`Password for ${email} has been reset to ${newPassword}`);
    } catch (error) {
        console.error('Error resetting password:', error);
    } finally {
        await sequelize.close();
    }
}

resetPassword();
