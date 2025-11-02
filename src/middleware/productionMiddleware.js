const express = require("express");
const helmet = require("helmet");
const compression = require("compression");
const rateLimit = require("express-rate-limit");
const slowDown = require("express-slow-down");

const setupProductionMiddleware = (app) => {
  // Security headers
  app.use(helmet());

  // Compression
  app.use(compression());

  // Rate limiting
  const limiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 100, // limit each IP to 100 requests per windowMs
  });
  app.use("/api/", limiter);

  // Speed limiting
  const speedLimiter = slowDown({
    windowMs: 15 * 60 * 1000, // 15 minutes
    delayAfter: 100, // allow 100 requests per 15 minutes, then...
    delayMs: 500, // begin adding 500ms of delay per request above 100
  });
  app.use("/api/", speedLimiter);

  // Trust proxy - needed if you're behind a reverse proxy (Heroku, Bluemix, AWS ELB, Nginx, etc)
  app.enable("trust proxy");

  // Security best practices
  app.use(helmet.noSniff());
  app.use(helmet.ieNoOpen());
  app.use(helmet.xssFilter());
  app.use(helmet.frameguard({ action: "deny" }));

  // CORS configuration for production
  app.use((req, res, next) => {
    res.header(
      "Access-Control-Allow-Origin",
      process.env.ALLOWED_ORIGINS || "*"
    );
    res.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
    res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
    next();
  });

  // Cache control
  app.use((req, res, next) => {
    // Cache static files for 1 day
    if (req.url.match(/^\/(css|js|img|font)/)) {
      res.setHeader("Cache-Control", "public, max-age=86400");
    }
    next();
  });
};

module.exports = setupProductionMiddleware;
