const jwt = require("jsonwebtoken");
const Config = require("../config/config");
const UserService = require("../services/UserService");
const { AppError } = require("../utils/errors");
const crypto = require("crypto");

class AuthController {
  constructor() {
    this.userService = new UserService();
  }

  generateAccessToken(user) {
    if (!Config.JWT_SECRET) {
      throw new Error("JWT_SECRET is not configured");
    }
    return jwt.sign({
      id: user.id,
      email: user.email,
      type: "access"
    }, Config.JWT_SECRET, {
      expiresIn: Config.JWT_EXPIRES_IN || "1h",
      issuer: "glowmance-api",
      audience: "glowmance-client"
    });
  }

  async register(req, res, next) {
    try {
      const { email, password, name } = req.body;
      if (!email || !password) {
        throw new AppError("Email and password are required", 400);
      }
      const user = await this.userService.create({ email, password, name });
      const token = this.generateAccessToken(user);
      res.status(201).json({ token });
    } catch (err) {
      next(err);
    }
  }

  async login(req, res, next) {
    try {
      const { email, password } = req.body;
      if (!email || !password) {
        throw new AppError("Email and password are required", 400);
      }
      const user = await this.userService.findByEmail(email);
      if (!user || !(await user.comparePassword(password))) {
        throw new AppError("Invalid email or password", 401);
      }
      const token = this.generateAccessToken(user);
      res.json({ token });
    } catch (err) {
      next(err);
    }
  }
}

module.exports = new AuthController();
