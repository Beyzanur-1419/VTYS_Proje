const { AppError } = require('../utils/errors');

const errorHandler = (err, req, res, next) => {
  err.statusCode = err.statusCode || 500;
  err.status = err.status || 'error';

  // Operational, trusted error: send message to client
  if (err.isOperational) {
    return res.status(err.statusCode).json({
      status: err.status,
      message: err.message
    });
  }

  // Programming or other unknown error: don't leak error details
  if (process.env.NODE_ENV !== 'production') {
    console.error('ERROR 💥', err);
    return res.status(500).json({
      status: 'error',
      message: err.message,
      stack: err.stack
    });
  }

  console.error('ERROR 💥', err);
  return res.status(500).json({
    status: 'error',
    message: 'Something went very wrong!'
  });
};

module.exports = {
  errorHandler
};