const request = require("supertest");
const app = require("../../app");
const { connect, disconnect } = require("../../config/database");

describe("Authentication API Integration Tests", () => {
  beforeAll(async () => {
    await connect();
  });

  afterAll(async () => {
    await disconnect();
  });

  describe("POST /api/auth/register", () => {
    it("should register a new user successfully", async () => {
      const response = await request(app).post("/api/auth/register").send({
        email: "test@example.com",
        password: "password123",
        name: "Test User",
      });

      expect(response.status).toBe(201);
      expect(response.body).toHaveProperty("token");
    });

    it("should return error for missing required fields", async () => {
      const response = await request(app).post("/api/auth/register").send({});

      expect(response.status).toBe(400);
      expect(response.body).toHaveProperty("message");
    });
  });

  describe("POST /api/auth/login", () => {
    it("should login successfully with valid credentials", async () => {
      // First register a user
      await request(app).post("/api/auth/register").send({
        email: "login@example.com",
        password: "password123",
        name: "Login Test",
      });

      // Then try to login
      const response = await request(app).post("/api/auth/login").send({
        email: "login@example.com",
        password: "password123",
      });

      expect(response.status).toBe(200);
      expect(response.body).toHaveProperty("token");
    });

    it("should return error for invalid credentials", async () => {
      const response = await request(app).post("/api/auth/login").send({
        email: "nonexistent@example.com",
        password: "wrongpassword",
      });

      expect(response.status).toBe(401);
      expect(response.body).toHaveProperty("message");
    });
  });
});
