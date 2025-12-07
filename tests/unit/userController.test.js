const userController = require('../../src/controllers/userController');
const userService = require('../../src/services/userService');

jest.mock('../../src/services/userService');

describe('UserController', () => {
  let req, res, next;

  beforeEach(() => {
    req = {
      user: { id: 'user123' },
      body: {}
    };
    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis()
    };
    next = jest.fn();
    jest.clearAllMocks();
  });

  describe('getProfile', () => {
    it('should return user profile', async () => {
      const mockUser = {
        id: 'user123',
        name: 'Test User',
        email: 'test@example.com'
      };
      userService.getUserById = jest.fn().mockResolvedValue(mockUser);

      await userController.getProfile(req, res, next);

      expect(userService.getUserById).toHaveBeenCalledWith('user123');
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        user: mockUser
      });
    });

    it('should handle errors', async () => {
      const error = new Error('User not found');
      userService.getUserById = jest.fn().mockRejectedValue(error);

      await userController.getProfile(req, res, next);

      expect(next).toHaveBeenCalledWith(error);
    });
  });

  describe('updateProfile', () => {
    it('should update user profile', async () => {
      const updateData = { name: 'Updated Name' };
      const mockUpdatedUser = {
        id: 'user123',
        name: 'Updated Name',
        email: 'test@example.com'
      };

      req.body = updateData;
      userService.updateUserProfile = jest.fn().mockResolvedValue(mockUpdatedUser);

      await userController.updateProfile(req, res, next);

      expect(userService.updateUserProfile).toHaveBeenCalledWith('user123', updateData);
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        user: mockUpdatedUser
      });
    });
  });
});
