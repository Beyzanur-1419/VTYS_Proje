const supertest = require('supertest');
const app = require('../app');
const { User } = require('../models');
const AIImage = require('../models/aiImage.model');
const mongoose = require('mongoose');
const Config = require('../config/config');

const request = supertest(app);

describe('API Smoke Tests', () => {
  let accessToken;
  let refreshToken;

  const testUser = {
    email: 'test@example.com',
    password: 'password123',
    name: 'Test User'
  };

  beforeAll(async () => {
    // Connect to MongoDB
    await mongoose.connect(Config.MONGODB_URI);
    
    // Clear test data
    await User.destroy({ where: {} });
    await AIImage.deleteMany({});
  });

  afterAll(async () => {
    await mongoose.connection.close();
  });

  describe('Auth Flow', () => {
    test('Register new user', async () => {
      const res = await request
        .post('/api/auth/register')
        .send(testUser);

      expect(res.status).toBe(201);
      expect(res.body.data.user).toBeDefined();
      expect(res.body.data.accessToken).toBeDefined();
      expect(res.body.data.refreshToken).toBeDefined();
    });

    test('Login user', async () => {
      const res = await request
        .post('/api/auth/login')
        .send({
          email: testUser.email,
          password: testUser.password
        });

      expect(res.status).toBe(200);
      expect(res.body.data.user).toBeDefined();
      expect(res.body.data.accessToken).toBeDefined();
      expect(res.body.data.refreshToken).toBeDefined();

      accessToken = res.body.data.accessToken;
      refreshToken = res.body.data.refreshToken;
    });

    test('Refresh token', async () => {
      const res = await request
        .post('/api/auth/refresh-token')
        .send({ refreshToken });

      expect(res.status).toBe(200);
      expect(res.body.data.accessToken).toBeDefined();
      expect(res.body.data.refreshToken).toBeDefined();

      accessToken = res.body.data.accessToken;
    });

    test('Logout user', async () => {
      const res = await request
        .post('/api/auth/logout')
        .set('Authorization', `Bearer ${accessToken}`);

      expect(res.status).toBe(200);
    });
  });

  describe('Upload & Analysis Flow', () => {
    beforeAll(async () => {
      // Login to get fresh tokens
      const res = await request
        .post('/api/auth/login')
        .send({
          email: testUser.email,
          password: testUser.password
        });

      accessToken = res.body.data.accessToken;
    });

    test('Upload image', async () => {
      const res = await request
        .post('/api/upload')
        .set('Authorization', `Bearer ${accessToken}`)
        .attach('image', '__tests__/fixtures/test-image.jpg');

      expect(res.status).toBe(201);
      expect(res.body.data.image).toBeDefined();
      expect(res.body.data.analysis).toBeDefined();
    });

    test('Get analysis history', async () => {
      const res = await request
        .get('/api/analysis-history')
        .set('Authorization', `Bearer ${accessToken}`);

      expect(res.status).toBe(200);
      expect(Array.isArray(res.body.data.analyses)).toBe(true);
      expect(res.body.data.analyses.length).toBeGreaterThan(0);
    });
  });
});