const jwt = require('jsonwebtoken');
const Config = require('../config/config');

/**
 * JWT Helper utilities for token generation and verification
 */
class JWTHelper {
  /**
   * Verify if JWT secret is configured
   */
  static verifyJWTConfig() {
    if (!Config.JWT_SECRET) {
      throw new Error('JWT_SECRET is not configured in environment variables');
    }
    if (!Config.REFRESH_TOKEN_SECRET) {
      throw new Error('REFRESH_TOKEN_SECRET is not configured in environment variables');
    }
    return true;
  }

  /**
   * Generate access token
   * @param {Object} payload - Token payload (id, email)
   * @returns {string} JWT access token
   */
  static generateAccessToken(payload) {
    this.verifyJWTConfig();
    
    return jwt.sign(
      { 
        id: payload.id, 
        email: payload.email,
        type: 'access'
      },
      Config.JWT_SECRET,
      {
        expiresIn: Config.JWT_EXPIRES_IN || '1h',
        issuer: 'glowmance-api',
        audience: 'glowmance-client'
      }
    );
  }

  /**
   * Generate refresh token
   * @param {Object} payload - Token payload (id)
   * @returns {string} JWT refresh token
   */
  static generateRefreshToken(payload) {
    this.verifyJWTConfig();
    
    return jwt.sign(
      { 
        id: payload.id,
        type: 'refresh'
      },
      Config.REFRESH_TOKEN_SECRET,
      {
        expiresIn: Config.REFRESH_TOKEN_EXPIRES_IN || '7d',
        issuer: 'glowmance-api',
        audience: 'glowmance-client'
      }
    );
  }

  /**
   * Verify access token
   * @param {string} token - JWT access token
   * @returns {Object} Decoded token payload
   */
  static verifyAccessToken(token) {
    this.verifyJWTConfig();
    
    try {
      const decoded = jwt.verify(token, Config.JWT_SECRET, {
        issuer: 'glowmance-api',
        audience: 'glowmance-client'
      });
      
      if (decoded.type !== 'access') {
        throw new Error('Invalid token type');
      }
      
      return decoded;
    } catch (error) {
      if (error.name === 'TokenExpiredError') {
        throw new Error('Token expired');
      } else if (error.name === 'JsonWebTokenError') {
        throw new Error('Invalid token');
      }
      throw error;
    }
  }

  /**
   * Verify refresh token
   * @param {string} token - JWT refresh token
   * @returns {Object} Decoded token payload
   */
  static verifyRefreshToken(token) {
    this.verifyJWTConfig();
    
    try {
      const decoded = jwt.verify(token, Config.REFRESH_TOKEN_SECRET, {
        issuer: 'glowmance-api',
        audience: 'glowmance-client'
      });
      
      if (decoded.type !== 'refresh') {
        throw new Error('Invalid token type');
      }
      
      return decoded;
    } catch (error) {
      if (error.name === 'TokenExpiredError') {
        throw new Error('Token expired');
      } else if (error.name === 'JsonWebTokenError') {
        throw new Error('Invalid token');
      }
      throw error;
    }
  }

  /**
   * Decode token without verification (for debugging)
   * @param {string} token - JWT token
   * @returns {Object} Decoded token payload
   */
  static decodeToken(token) {
    return jwt.decode(token);
  }

  /**
   * Check if token is expired
   * @param {string} token - JWT token
   * @returns {boolean} True if expired
   */
  static isTokenExpired(token) {
    try {
      const decoded = jwt.decode(token);
      if (!decoded || !decoded.exp) {
        return true;
      }
      
      const currentTime = Math.floor(Date.now() / 1000);
      return decoded.exp < currentTime;
    } catch (error) {
      return true;
    }
  }
}

module.exports = JWTHelper;

