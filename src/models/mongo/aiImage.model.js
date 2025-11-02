const mongoose = require('mongoose');

const aiImageSchema = new mongoose.Schema({
  userId: {
    type: String, // UUID from SQL
    required: true,
    index: true
  },
  originalName: {
    type: String,
    required: true
  },
  mimeType: {
    type: String,
    required: true
  },
  size: {
    type: Number,
    required: true
  },
  url: {
    type: String,
    required: true
  },
  timestamp: {
    type: Date,
    required: true,
    index: true
  }
}, {
  timestamps: true
});

module.exports = mongoose.model('AIImage', aiImageSchema);