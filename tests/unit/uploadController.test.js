const uploadController = require('../../src/controllers/uploadController');
const imageService = require('../../src/services/imageService');

jest.mock('../../src/services/imageService');

describe('UploadController', () => {
  let req, res, next;

  beforeEach(() => {
    req = {
      user: { id: 'user123' },
      file: null,
      params: {}
    };
    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis()
    };
    next = jest.fn();
    jest.clearAllMocks();
  });

  describe('uploadImage', () => {
    it('should upload image successfully', async () => {
      const mockFile = {
        filename: 'test.jpg',
        originalname: 'test.jpg',
        mimetype: 'image/jpeg',
        size: 1024,
        path: '/uploads/test.jpg'
      };
      const mockImageLog = {
        _id: 'img123',
        filename: 'test.jpg',
        url: '/uploads/test.jpg',
        size: 1024,
        created_at: new Date()
      };

      req.file = mockFile;
      imageService.createImageLog = jest.fn().mockResolvedValue(mockImageLog);

      await uploadController.uploadImage(req, res, next);

      expect(imageService.createImageLog).toHaveBeenCalledWith({
        userId: 'user123',
        filename: 'test.jpg',
        originalName: 'test.jpg',
        mimeType: 'image/jpeg',
        size: 1024,
        path: '/uploads/test.jpg',
        url: '/uploads/test.jpg'
      });
      expect(res.status).toHaveBeenCalledWith(201);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        data: {
          id: 'img123',
          filename: 'test.jpg',
          url: '/uploads/test.jpg',
          size: 1024,
          uploadedAt: mockImageLog.created_at
        }
      });
    });

    it('should return 400 if no file uploaded', async () => {
      req.file = null;

      await uploadController.uploadImage(req, res, next);

      expect(res.status).toHaveBeenCalledWith(400);
      expect(res.json).toHaveBeenCalledWith({
        success: false,
        message: 'No image file uploaded'
      });
    });

    it('should handle errors', async () => {
      const error = new Error('Upload failed');
      req.file = { filename: 'test.jpg' };
      imageService.createImageLog = jest.fn().mockRejectedValue(error);

      await uploadController.uploadImage(req, res, next);

      expect(next).toHaveBeenCalledWith(error);
    });
  });

  describe('getUserImages', () => {
    it('should return user images', async () => {
      const mockImages = [
        { _id: 'img1', filename: 'test1.jpg', url: '/uploads/test1.jpg' },
        { _id: 'img2', filename: 'test2.jpg', url: '/uploads/test2.jpg' }
      ];
      imageService.getUserImages = jest.fn().mockResolvedValue(mockImages);

      await uploadController.getUserImages(req, res, next);

      expect(imageService.getUserImages).toHaveBeenCalledWith('user123');
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        count: 2,
        data: mockImages
      });
    });
  });

  describe('deleteImage', () => {
    it('should delete image successfully', async () => {
      req.params.id = 'img123';
      imageService.deleteImage = jest.fn().mockResolvedValue({
        message: 'Image deleted successfully'
      });

      await uploadController.deleteImage(req, res, next);

      expect(imageService.deleteImage).toHaveBeenCalledWith('img123', 'user123');
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        message: 'Image deleted successfully'
      });
    });

    it('should return 404 if image not found', async () => {
      req.params.id = 'img999';
      const error = new Error('Image not found');
      imageService.deleteImage = jest.fn().mockRejectedValue(error);

      await uploadController.deleteImage(req, res, next);

      expect(res.status).toHaveBeenCalledWith(404);
      expect(res.json).toHaveBeenCalledWith({
        success: false,
        message: 'Image not found'
      });
    });
  });

  describe('getImageStats', () => {
    it('should return image statistics', async () => {
      const mockStats = {
        totalImages: 10,
        recentImages: []
      };
      imageService.getUserImageStats = jest.fn().mockResolvedValue(mockStats);

      await uploadController.getImageStats(req, res, next);

      expect(imageService.getUserImageStats).toHaveBeenCalledWith('user123');
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({
        success: true,
        data: mockStats
      });
    });
  });
});
