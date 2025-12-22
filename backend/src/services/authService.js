const User = require('../models/User');
const { generateToken } = require('../utils/jwt');
const Log = require('../models/Log');

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

    // LOG ACTION
    try {
      await Log.create({
        user_id: user.id,
        action: 'REGISTER',
        timestamp: new Date()
      });
    } catch (e) {
      console.error('Failed to log register action:', e.message);
    }

    return { user, token };
  }

  async login(email, password) {
    console.log(`[AuthService] Login attempt for: ${email}`);
    const normalizedEmail = email.toLowerCase();

    console.log('[AuthService] Finding user...');
    const user = await User.findOne({ where: { email: normalizedEmail } });

    if (!user) {
      console.log('[AuthService] User not found');
      throw new Error('Invalid email or password');
    }

    console.log('[AuthService] Verifying password...');
    if (!(await user.matchPassword(password))) {
      console.log('[AuthService] Password mismatch');
      throw new Error('Invalid email or password');
    }

    console.log('[AuthService] Generating token...');
    const token = generateToken(user.id);

    // LOG ACTION
    try {
      console.log('[AuthService] Logging action...');
      // Use a timeout or fire-and-forget to prevent blocking
      Log.create({
        user_id: user.id,
        action: 'LOGIN',
        timestamp: new Date()
      }).catch(err => console.error('[AuthService] Log creation failed (bg):', err.message));

      console.log('[AuthService] Login successful');
    } catch (e) {
      console.error('[AuthService] Failed to log login action:', e.message);
    }

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
