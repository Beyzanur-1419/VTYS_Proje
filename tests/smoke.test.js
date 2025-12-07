const request = require("supertest");
const app = require("../src/app");
const mongoose = require("mongoose");
const config = require("../src/config");

let server;

beforeAll(async () => {
  // Connect to test database
  if (config.MONGO_URI) {
    try {
      await mongoose.connect(config.MONGO_URI, { serverSelectionTimeoutMS: 5000 });
    } catch (err) {
      console.warn("⚠️ Smoke Test: MongoDB connection failed, proceeding without DB.");
    }
  }
  server = app.listen(0); // Random port
});

afterAll(async () => {
  if (mongoose.connection.readyState !== 0) {
    await mongoose.connection.close();
  }
  await server.close();
});

describe("API Smoke Tests", () => {
  describe("Health Check", () => {
    it("should return 200 OK for health check endpoint", async () => {
      const res = await request(app).get("/health");
      expect(res.statusCode).toBe(200);
      expect(res.body.status).toBe("ok");
      expect(res.body.timestamp).toBeDefined();
    });
  });

  describe("API Endpoints", () => {
    it("should return 401 for protected routes without authentication", async () => {
      const endpoints = [
        "/api/v1/user/profile",
        "/api/v1/analysis/history",
      ];

      for (const endpoint of endpoints) {
        const res = await request(app).get(endpoint);
        expect(res.statusCode).toBe(401);
      }
    });

    it("should return 404 for non-existent routes", async () => {
      const res = await request(app).get("/api/non-existent-route");
      expect(res.statusCode).toBe(404);
      expect(res.body.success).toBe(false); // Updated expectation
      expect(res.body.message).toBe("Not Found");
    });
  });

  describe("Security Headers", () => {
    it("should have security headers set", async () => {
      const res = await request(app).get("/health");

      // Check for important security headers
      expect(res.headers["x-content-type-options"]).toBe("nosniff");
      expect(res.headers["x-frame-options"]).toBe("SAMEORIGIN");
      expect(res.headers["x-xss-protection"]).toBe("0"); // Helmet default changed to 0
      expect(res.headers["strict-transport-security"]).toBeDefined();
    });
  });

  describe("Rate Limiting", () => {
    it("should enforce rate limits", async () => {
      const requests = Array(11).fill("/api/v1/auth/login"); // Updated path and limit (10 per hour)

      for (const [index, endpoint] of requests.entries()) {
        const res = await request(app).post(endpoint);

        if (index === 10) {
          // The 11th request should be rate limited
          expect(res.statusCode).toBe(429);
          expect(res.body.message).toContain("Too many login attempts");
        }
      }
    });
  });

  describe("CORS", () => {
    it("should have CORS headers", async () => {
      const res = await request(app)
        .get("/health")
        .set("Origin", config.CORS_ORIGIN);

      expect(res.headers["access-control-allow-origin"]).toBeDefined();
      expect(res.headers["access-control-allow-credentials"]).toBe("true");
    });
  });
});
