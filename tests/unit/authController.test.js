const authController = require('../../src/controllers/authController');
const authService = require('../../src/services/authService');

jest.mock('../../src/services/authService');

describe('AuthController', () => {
  let req, res, next;

  beforeEach(() => {
    req = {
      body: {}
    };
    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis()
    };
    next = jest.fn();
    jest.clearAllMocks();
  });

  describe('register', () => {
    it('should register user successfully', async () => {
      const userData = {
        name: 'Test User',
        email: 'test@example.com',
        password: '123456'
      };
      const mockResult = {
        user: { id: 'user123', name: 'Test User', email: 'test@example.com' },
        token: 'jwt-token-123'
      };

      req.body = userData;
      authService.register = jest.fn().mockResolvedValue(mockResult);

      await authController.register(req, res, next);

      expect(authService.register).toHaveBeenCalledWith(userData);
      expect(res.status).toHaveBeenCalledWith(201);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        token: mockResult.token,
        user: mockResult.user
      });
    });

    it('should handle registration errors', async () => {
      const error = new Error('Email already exists');
      req.body = { email: 'test@example.com', password: '123456' };
      authService.register = jest.fn().mockRejectedValue(error);

      await authController.register(req, res, next);

      expect(next).toHaveBeenCalledWith(error);
    });
  });

  describe('login', () => {
    it('should login user successfully', async () => {
      const credentials = {
        email: 'test@example.com',
        password: '123456'
      };
      const mockResult = {
        user: { id: 'user123', email: 'test@example.com' },
        token: 'jwt-token-123'
      };

      req.body = credentials;
      authService.login = jest.fn().mockResolvedValue(mockResult);

      await authController.login(req, res, next);

      expect(authService.login).toHaveBeenCalledWith(credentials.email, credentials.password);
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        token: mockResult.token,
        user: mockResult.user
      });
    });

    it('should handle invalid credentials', async () => {
      const error = new Error('Invalid credentials');
      req.body = { email: 'test@example.com', password: 'wrong' };
      authService.login = jest.fn().mockRejectedValue(error);

      await authController.login(req, res, next);

      expect(next).toHaveBeenCalledWith(error);
    });
  });
});
