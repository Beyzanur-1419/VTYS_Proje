const express = require("express");
const cors = require("cors");
const cookieParser = require("cookie-parser");
const morgan = require("morgan");
const Config = require("./config/config");
const { errorHandler } = require("./middleware/errorHandler");

// Import routes
const authRoutes = require("./routes/authRoutes");
const uploadRoutes = require("./routes/uploadRoutes");
const analysisHistoryRoutes = require("./routes/analysisHistoryRoutes");
const userRoutes = require("./routes/userRoutes");
const productsRoutes = require("./routes/products.routes");

// Create Express app
const app = express();

// Middleware
app.use(morgan("dev")); // Logging
app.use(
  cors({
    origin: Config.CORS_ORIGIN,
    credentials: true, // For cookies
  })
);
app.use(cookieParser());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Serve static files from uploads directory
app.use("/uploads", express.static("uploads"));

// Health check endpoint
app.get("/health", (req, res) => {
  res.json({ status: "ok", timestamp: new Date().toISOString() });
});

// API Routes
app.use("/api/auth", authRoutes);
app.use("/api/upload", uploadRoutes);
app.use("/api/analysis-history", analysisHistoryRoutes);
app.use("/api/user", userRoutes);
app.use("/api/products", productsRoutes);

// Error handling
app.use(errorHandler);

// Handle 404
app.use((req, res, next) => {
  res.status(404).json({
    status: "error",
    message: "Not Found",
  });
});

module.exports = app;
