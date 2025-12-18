const User = require('../models/User');
const { generateToken } = require('../utils/jwt');

class AuthService {
  async register(userData) {
    const email = userData.email.toLowerCase();
    const userExists = await User.findOne({ where: { email } });
    if (userExists) {
      const error = new Error('User already exists');
      error.statusCode = 409; // Conflict
      throw error;
    }

    const user = await User.create({ ...userData, email });
    const token = generateToken(user.id);

    return { user, token };
  }

  async login(email, password) {
    const normalizedEmail = email.toLowerCase();
    const user = await User.findOne({ where: { email: normalizedEmail } });
    if (!user || !(await user.matchPassword(password))) {
      throw new Error('Invalid email or password');
    }

    const token = generateToken(user.id);
    return { user, token };
  }

  async changePassword(userId, oldPassword, newPassword) {
    try {
      const user = await User.findByPk(userId);
      if (!user) {
        throw new Error('Kullanıcı bulunamadı');
      }

      const isMatch = await user.matchPassword(oldPassword);
      if (!isMatch) {
        throw new Error('Mevcut şifre hatalı');
      }

      user.password = newPassword;
      await user.save();

      return true;
    } catch (error) {
      throw error;
    }
  }

  async deleteAccount(userId) {
    try {
      const user = await User.findByPk(userId);
      if (!user) {
        throw new Error('Kullanıcı bulunamadı');
      }

      await user.destroy();
      return true;
    } catch (error) {
      throw error;
    }
  }
}

module.exports = new AuthService();
