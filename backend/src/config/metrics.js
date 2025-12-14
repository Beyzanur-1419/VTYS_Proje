const client = require('prom-client');
const logger = require('../utils/logger');

// Create a Registry
const register = new client.Registry();

// Add default metrics (CPU, memory, etc.)
client.collectDefaultMetrics({ register });

// Custom Metrics

// HTTP Request Duration
const httpRequestDuration = new client.Histogram({
  name: 'http_request_duration_seconds',
  help: 'Duration of HTTP requests in seconds',
  labelNames: ['method', 'route', 'status_code'],
  buckets: [0.1, 0.5, 1, 2, 5, 10]
});

// HTTP Request Counter
const httpRequestTotal = new client.Counter({
  name: 'http_requests_total',
  help: 'Total number of HTTP requests',
  labelNames: ['method', 'route', 'status_code']
});

// Active Requests Gauge
const httpRequestsActive = new client.Gauge({
  name: 'http_requests_active',
  help: 'Number of active HTTP requests'
});

// Database Query Duration
const dbQueryDuration = new client.Histogram({
  name: 'db_query_duration_seconds',
  help: 'Duration of database queries in seconds',
  labelNames: ['operation', 'table'],
  buckets: [0.01, 0.05, 0.1, 0.5, 1, 2]
});

// Cache Hit/Miss Counter
const cacheHitTotal = new client.Counter({
  name: 'cache_hits_total',
  help: 'Total number of cache hits',
  labelNames: ['cache_type']
});

const cacheMissTotal = new client.Counter({
  name: 'cache_misses_total',
  help: 'Total number of cache misses',
  labelNames: ['cache_type']
});

// Error Counter
const errorTotal = new client.Counter({
  name: 'errors_total',
  help: 'Total number of errors',
  labelNames: ['type', 'route']
});

// ML Service Requests
const mlRequestTotal = new client.Counter({
  name: 'ml_requests_total',
  help: 'Total number of ML service requests',
  labelNames: ['type', 'status']
});

const mlRequestDuration = new client.Histogram({
  name: 'ml_request_duration_seconds',
  help: 'Duration of ML service requests',
  labelNames: ['type'],
  buckets: [0.5, 1, 2, 5, 10, 30]
});

// Register all metrics
register.registerMetric(httpRequestDuration);
register.registerMetric(httpRequestTotal);
register.registerMetric(httpRequestsActive);
register.registerMetric(dbQueryDuration);
register.registerMetric(cacheHitTotal);
register.registerMetric(cacheMissTotal);
register.registerMetric(errorTotal);
register.registerMetric(mlRequestTotal);
register.registerMetric(mlRequestDuration);

// Helper functions
const metrics = {
  register,
  
  // HTTP Metrics
  recordHttpRequest(method, route, statusCode, duration) {
    httpRequestTotal.inc({ method, route, status_code: statusCode });
    httpRequestDuration.observe({ method, route, status_code: statusCode }, duration);
  },

  incrementActiveRequests() {
    httpRequestsActive.inc();
  },

  decrementActiveRequests() {
    httpRequestsActive.dec();
  },

  // Database Metrics
  recordDbQuery(operation, table, duration) {
    dbQueryDuration.observe({ operation, table }, duration);
  },

  // Cache Metrics
  recordCacheHit(cacheType = 'redis') {
    cacheHitTotal.inc({ cache_type: cacheType });
  },

  recordCacheMiss(cacheType = 'redis') {
    cacheMissTotal.inc({ cache_type: cacheType });
  },

  // Error Metrics
  recordError(type, route) {
    errorTotal.inc({ type, route });
  },

  // ML Metrics
  recordMlRequest(type, status, duration) {
    mlRequestTotal.inc({ type, status });
    if (duration !== undefined) {
      mlRequestDuration.observe({ type }, duration);
    }
  },

  // Get all metrics
  async getMetrics() {
    return await register.metrics();
  }
};

module.exports = metrics;
