const User = require('../models/User');
const { generateToken } = require('../utils/jwt');

class AuthService {
  async register(userData) {
    const userExists = await User.findOne({ where: { email: userData.email } });
    if (userExists) {
      const error = new Error('User already exists');
      error.statusCode = 409; // Conflict
      throw error;
    }

    const user = await User.create(userData);
    const token = generateToken(user.id);

    return { user, token };
  }

  async login(email, password) {
    const user = await User.findOne({ where: { email } });
    if (!user || !(await user.matchPassword(password))) {
      throw new Error('Invalid email or password');
    }

    const token = generateToken(user.id);
    return { user, token };
  }
}

module.exports = new AuthService();
