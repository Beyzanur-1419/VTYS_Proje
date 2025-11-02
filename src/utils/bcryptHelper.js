const bcrypt = require('bcrypt');

/**
 * Bcrypt Helper utilities for password hashing and verification
 */
class BcryptHelper {
  /**
   * Default salt rounds for password hashing
   */
  static SALT_ROUNDS = 10;

  /**
   * Minimum salt rounds for security
   */
  static MIN_SALT_ROUNDS = 10;

  /**
   * Maximum salt rounds for performance balance
   */
  static MAX_SALT_ROUNDS = 15;

  /**
   * Hash a password
   * @param {string} password - Plain text password
   * @param {number} saltRounds - Number of salt rounds (default: 10)
   * @returns {Promise<string>} Hashed password
   */
  static async hashPassword(password, saltRounds = this.SALT_ROUNDS) {
    if (!password) {
      throw new Error('Password is required');
    }

    // Validate salt rounds
    const rounds = Math.max(this.MIN_SALT_ROUNDS, Math.min(saltRounds, this.MAX_SALT_ROUNDS));

    try {
      const salt = await bcrypt.genSalt(rounds);
      const hash = await bcrypt.hash(password, salt);
      return hash;
    } catch (error) {
      throw new Error(`Password hashing failed: ${error.message}`);
    }
  }

  /**
   * Compare a password with a hash
   * @param {string} password - Plain text password
   * @param {string} hash - Hashed password
   * @returns {Promise<boolean>} True if passwords match
   */
  static async comparePassword(password, hash) {
    if (!password || !hash) {
      return false;
    }

    try {
      const isMatch = await bcrypt.compare(password, hash);
      return isMatch;
    } catch (error) {
      // Log error but don't expose details
      console.error('Password comparison error:', error.message);
      return false;
    }
  }

  /**
   * Verify password strength
   * @param {string} password - Password to verify
   * @returns {Object} Validation result with isValid and errors
   */
  static verifyPasswordStrength(password) {
    const errors = [];

    if (!password) {
      return { isValid: false, errors: ['Password is required'] };
    }

    if (password.length < 6) {
      errors.push('Password must be at least 6 characters long');
    }

    if (!/\d/.test(password)) {
      errors.push('Password must contain at least one number');
    }

    if (!/[a-z]/.test(password)) {
      errors.push('Password must contain at least one lowercase letter');
    }

    if (!/[A-Z]/.test(password)) {
      errors.push('Password must contain at least one uppercase letter');
    }

    if (/^\d+$/.test(password)) {
      errors.push('Password cannot be all numbers');
    }

    return {
      isValid: errors.length === 0,
      errors
    };
  }

  /**
   * Get hash info (for debugging/verification)
   * @param {string} hash - Bcrypt hash
   * @returns {Object} Hash information
   */
  static getHashInfo(hash) {
    if (!hash || typeof hash !== 'string') {
      return null;
    }

    // Bcrypt hash format: $2b$10$salt+hash
    const parts = hash.split('$');
    if (parts.length !== 4 || parts[0] !== '' || parts[1] !== '2b') {
      return null;
    }

    return {
      algorithm: parts[1], // '2b'
      saltRounds: parseInt(parts[2]),
      salt: parts[3].substring(0, 22),
      hash: parts[3].substring(22)
    };
  }

  /**
   * Check if hash is valid bcrypt format
   * @param {string} hash - Hash to check
   * @returns {boolean} True if valid bcrypt hash
   */
  static isValidHash(hash) {
    if (!hash || typeof hash !== 'string') {
      return false;
    }

    // Bcrypt hash should start with $2a$, $2b$, or $2y$
    const bcryptRegex = /^\$2[ayb]\$\d{2}\$[./A-Za-z0-9]{53}$/;
    return bcryptRegex.test(hash);
  }
}

module.exports = BcryptHelper;

