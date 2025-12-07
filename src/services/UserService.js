const User = require('../models/User');

class UserService {
  async getUserById(id) {
    const user = await User.findByPk(id, {
      attributes: { exclude: ['password'] },
    });
    if (!user) {
      throw new Error('User not found');
    }
    return user;
  }

  async updateUserProfile(id, data) {
    const user = await User.findByPk(id);
    if (!user) {
      throw new Error('User not found');
    }

    await user.update(data);
    return user;
  }
}

module.exports = new UserService();
