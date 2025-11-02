module.exports = {
  testEnvironment: "node",
  testMatch: ["**/__tests__/**/*.test.js"],
  collectCoverage: true,
  coverageDirectory: "coverage",
  coverageReporters: ["text", "lcov", "clover"],
  coveragePathIgnorePatterns: ["/node_modules/", "/__tests__/"],
  setupFilesAfterEnv: ["./jest.setup.js"],
  verbose: true,
};
