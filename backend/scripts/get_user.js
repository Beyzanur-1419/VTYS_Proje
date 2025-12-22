
const sequelize = require('../src/config/db.postgres');
const User = require('../src/models/User');

async function getUser() {
    try {
        await sequelize.authenticate();
        const user = await User.findOne();
        if (user) {
            console.log(`Email: ${user.email}`);
            console.log(`Name: ${user.name}`);
        } else {
            console.log('No user found');
        }
        process.exit(0);
    } catch (error) {
        console.error(error);
        process.exit(1);
    }
}

getUser();
