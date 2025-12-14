module.exports = {
  apps: [
    {
      name: 'glowmance-api',
      script: './src/server.js',
      instances: 'max', // Tüm CPU core'larını kullan (veya sayı: 2, 4, vb.)
      exec_mode: 'cluster', // Cluster mode
      env: {
        NODE_ENV: 'development',
        PORT: 3000
      },
      env_production: {
        NODE_ENV: 'production',
        PORT: 3000
      },
      // Auto restart
      autorestart: true,
      watch: false, // Development'ta true yapabilirsin
      max_memory_restart: '1G',
      
      // Logging
      error_file: './logs/pm2-error.log',
      out_file: './logs/pm2-out.log',
      log_date_format: 'YYYY-MM-DD HH:mm:ss Z',
      
      // Graceful shutdown
      kill_timeout: 5000,
      wait_ready: true,
      listen_timeout: 10000,
      
      // Restart delay
      min_uptime: '10s',
      max_restarts: 10
    }
  ]
};
