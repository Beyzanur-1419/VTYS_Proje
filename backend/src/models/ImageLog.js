const mongoose = require('mongoose');

const imageLogSchema = new mongoose.Schema({
  userId: {
    type: String,
    required: true,
    index: true,
  },
  filename: {
    type: String,
    required: true,
  },
  originalName: {
    type: String,
  },
  mimeType: {
    type: String,
  },
  size: {
    type: Number,
  },
  path: {
    type: String,
  },
  url: {
    type: String,
  },
  detected_type: {
    type: String,
  },
  detected_disease: {
    type: String,
  },
  raw_output: {
    type: Object,
  },
  created_at: {
    type: Date,
    default: Date.now,
  },
});

// Add indexes for better query performance
imageLogSchema.index({ userId: 1, created_at: -1 }); // Composite index for user queries
imageLogSchema.index({ created_at: -1 }); // Date sorting
imageLogSchema.index({ detected_type: 1 }); // Filter by skin type
imageLogSchema.index({ detected_disease: 1 }); // Filter by disease

module.exports = mongoose.model('ImageLog', imageLogSchema);
