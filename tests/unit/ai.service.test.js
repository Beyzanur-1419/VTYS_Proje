const mlClient = require('../../src/integrations/ml/mlClient');
const axios = require('axios');
const FormData = require('form-data');

// Mock Axios
jest.mock('axios');

describe('ML Service Unit Tests', () => {
  const mockBuffer = Buffer.from('fake-image');
  const mockFilename = 'test.jpg';

  beforeEach(() => {
    // Setup Axios Mock instance
    mlClient.client = {
      get: jest.fn(),
    };
    // Mock axios.post for static calls if any, or instance calls
    axios.post = jest.fn();
    axios.create = jest.fn().mockReturnValue({
        get: jest.fn(),
        post: jest.fn()
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  test('analyzeSkinType should return analysis result', async () => {
    const mockResponse = { data: { skin_type: 'Oily', confidence: 0.9 } };
    axios.post.mockResolvedValue(mockResponse);

    const result = await mlClient.analyzeSkinType(mockBuffer, mockFilename);

    expect(axios.post).toHaveBeenCalledWith(
      expect.stringContaining('/analyze/skin-type'),
      expect.any(FormData),
      expect.any(Object)
    );
    expect(result).toEqual(mockResponse.data);
  });

  test('analyzeSkinType should throw error on failure', async () => {
    axios.post.mockRejectedValue(new Error('Network Error'));

    await expect(mlClient.analyzeSkinType(mockBuffer, mockFilename))
      .rejects
      .toThrow('ML Service unavailable');
  });
});
