const inciController = require('../../src/controllers/inciController');
const axios = require('axios');
const cheerio = require('cheerio');

jest.mock('axios');
jest.mock('cheerio');

describe('InciController', () => {
  let req, res;

  beforeEach(() => {
    req = {
      body: {}
    };
    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis()
    };
    jest.clearAllMocks();
  });

  describe('getIngredients', () => {
    it('should return 400 if product name is missing', async () => {
      req.body = {};

      await inciController.getIngredients(req, res);

      expect(res.status).toHaveBeenCalledWith(400);
      expect(res.json).toHaveBeenCalledWith({
        error: 'Product name gerekli'
      });
    });

    it('should handle scraping errors', async () => {
      req.body = { product: 'Test Product' };
      axios.get = jest.fn().mockRejectedValue(new Error('Network error'));

      await inciController.getIngredients(req, res);

      expect(res.status).toHaveBeenCalledWith(500);
      expect(res.json).toHaveBeenCalledWith({
        success: false,
        error: 'INCIdecoder scrape hata verdi',
        details: 'Network error'
      });
    });

    it('should return 404 if no ingredients found', async () => {
      req.body = { product: 'Test Product' };
      
      const mockHtml = '<html><body></body></html>';
      axios.get = jest.fn().mockResolvedValue({ data: mockHtml });
      
      const mockCheerio = {
        load: jest.fn().mockReturnValue(() => ({
          find: jest.fn().mockReturnThis(),
          text: jest.fn().mockReturnValue(''),
          attr: jest.fn().mockReturnValue(''),
          trim: jest.fn().mockReturnValue('')
        }))
      };
      cheerio.load = mockCheerio.load;

      await inciController.getIngredients(req, res);

      expect(res.status).toHaveBeenCalledWith(404);
    });
  });
});
