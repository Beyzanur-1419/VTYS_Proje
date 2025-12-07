const express = require('express');
const metrics = require('../config/metrics');
const logger = require('../utils/logger');

const router = express.Router();

/**
 * @swagger
 * /metrics:
 *   get:
 *     summary: Get Prometheus metrics
 *     tags: [Monitoring]
 *     responses:
 *       200:
 *         description: Prometheus metrics in text format
 */
router.get('/metrics', async (req, res) => {
  try {
    res.set('Content-Type', metrics.register.contentType);
    const metricsData = await metrics.getMetrics();
    res.end(metricsData);
  } catch (error) {
    logger.error('Metrics endpoint error:', error);
    res.status(500).json({ success: false, error: 'Failed to get metrics' });
  }
});

/**
 * @swagger
 * /health:
 *   get:
 *     summary: Health check endpoint with detailed status
 *     tags: [Monitoring]
 *     responses:
 *       200:
 *         description: Service health status
 */
router.get('/health', async (req, res) => {
  const healthCheck = {
    status: 'ok',
    timestamp: new Date().toISOString(),
    uptime: process.uptime(),
    environment: process.env.NODE_ENV || 'development',
    memory: {
      used: Math.round(process.memoryUsage().heapUsed / 1024 / 1024),
      total: Math.round(process.memoryUsage().heapTotal / 1024 / 1024),
      unit: 'MB'
    },
    cpu: process.cpuUsage()
  };

  // Check database connections
  try {
    const sequelize = require('../config/db.postgres');
    await sequelize.authenticate();
    healthCheck.database = { postgres: 'connected' };
  } catch (error) {
    healthCheck.database = { postgres: 'disconnected' };
    healthCheck.status = 'degraded';
  }

  try {
    const mongoose = require('mongoose');
    healthCheck.database = {
      ...healthCheck.database,
      mongodb: mongoose.connection.readyState === 1 ? 'connected' : 'disconnected'
    };
  } catch (error) {
    healthCheck.database = {
      ...healthCheck.database,
      mongodb: 'disconnected'
    };
  }

  // Check Redis
  try {
    const redisClient = require('../config/redis');
    healthCheck.cache = {
      redis: redisClient.isConnected ? 'connected' : 'disconnected'
    };
  } catch (error) {
    healthCheck.cache = { redis: 'disconnected' };
  }

  const statusCode = healthCheck.status === 'ok' ? 200 : 503;
  res.status(statusCode).json(healthCheck);
});

/**
 * @swagger
 * /health/ready:
 *   get:
 *     summary: Readiness probe for Kubernetes
 *     tags: [Monitoring]
 *     responses:
 *       200:
 *         description: Service is ready
 *       503:
 *         description: Service is not ready
 */
router.get('/health/ready', async (req, res) => {
  try {
    const sequelize = require('../config/db.postgres');
    await sequelize.authenticate();
    res.status(200).json({ ready: true });
  } catch (error) {
    res.status(503).json({ ready: false, error: error.message });
  }
});

/**
 * @swagger
 * /health/live:
 *   get:
 *     summary: Liveness probe for Kubernetes
 *     tags: [Monitoring]
 *     responses:
 *       200:
 *         description: Service is alive
 */
router.get('/health/live', (req, res) => {
  res.status(200).json({ alive: true });
});

module.exports = router;
