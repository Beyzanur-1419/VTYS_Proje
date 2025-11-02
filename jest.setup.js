require("dotenv").config({ path: ".env.test" });

// Set up MongoDB Memory Server for testing
const { MongoMemoryServer } = require("mongodb-memory-server");
const mongoose = require("mongoose");

let mongod;

beforeAll(async () => {
  // Create an in-memory MongoDB instance
  mongod = await MongoMemoryServer.create();
  const mongoUri = mongod.getUri();
  await mongoose.connect(mongoUri);
});

afterAll(async () => {
  // Clean up and close connections
  await mongoose.disconnect();
  await mongod.stop();
});

afterEach(async () => {
  // Clean up database between tests
  const collections = mongoose.connection.collections;
  for (const key in collections) {
    await collections[key].deleteMany();
  }
});
