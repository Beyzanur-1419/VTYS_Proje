const AuthController = require("../../../controllers/authController");
const UserService = require("../../../services/UserService");
const { AppError } = require("../../../utils/errors");

jest.mock("../../../services/UserService");

describe("AuthController", () => {
  let authController;
  let mockReq;
  let mockRes;
  let mockNext;

  beforeEach(() => {
    authController = new AuthController();
    mockReq = {
      body: {},
    };
    mockRes = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn(),
    };
    mockNext = jest.fn();
  });

  describe("register", () => {
    it("should register a new user successfully", async () => {
      const userData = {
        email: "test@example.com",
        password: "password123",
        name: "Test User",
      };
      mockReq.body = userData;

      const mockUser = { id: 1, ...userData };
      UserService.prototype.create.mockResolvedValue(mockUser);

      await authController.register(mockReq, mockRes, mockNext);

      expect(UserService.prototype.create).toHaveBeenCalledWith(userData);
      expect(mockRes.status).toHaveBeenCalledWith(201);
      expect(mockRes.json).toHaveBeenCalledWith(
        expect.objectContaining({
          token: expect.any(String),
        })
      );
    });

    it("should handle missing email and password", async () => {
      await authController.register(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(expect.any(AppError));
    });
  });

  describe("login", () => {
    it("should login user successfully", async () => {
      const userData = {
        email: "test@example.com",
        password: "password123",
      };
      mockReq.body = userData;

      const mockUser = {
        id: 1,
        ...userData,
        comparePassword: jest.fn().mockResolvedValue(true),
      };
      UserService.prototype.findByEmail.mockResolvedValue(mockUser);

      await authController.login(mockReq, mockRes, mockNext);

      expect(UserService.prototype.findByEmail).toHaveBeenCalledWith(
        userData.email
      );
      expect(mockUser.comparePassword).toHaveBeenCalledWith(userData.password);
      expect(mockRes.json).toHaveBeenCalledWith(
        expect.objectContaining({
          token: expect.any(String),
        })
      );
    });

    it("should handle invalid credentials", async () => {
      const userData = {
        email: "test@example.com",
        password: "wrongpassword",
      };
      mockReq.body = userData;

      const mockUser = {
        id: 1,
        email: userData.email,
        comparePassword: jest.fn().mockResolvedValue(false),
      };
      UserService.prototype.findByEmail.mockResolvedValue(mockUser);

      await authController.login(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(expect.any(AppError));
    });
  });
});
