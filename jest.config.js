module.exports = {
  testEnvironment: "node",
  verbose: true,
  // setupFilesAfterEnv: ["<rootDir>/jest.setup.js"], // Commented out as file might have encoding issues
  testMatch: [
    "**/tests/**/*.test.js",
  ],
  testPathIgnorePatterns: ["/node_modules/"],
  collectCoverage: true,
  coverageDirectory: "coverage",
  coverageReporters: ["text", "lcov"],
  coveragePathIgnorePatterns: ["/node_modules/", "/tests/", "/coverage/"],
  transform: {},
  testTimeout: 30000,
};
