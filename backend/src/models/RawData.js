const mongoose = require('mongoose');

const rawDataSchema = new mongoose.Schema({
  image_base64: {
    type: String,
    required: true,
  },
  metadata: {
    type: Object,
  },
  created_at: {
    type: Date,
    default: Date.now,
  },
});

module.exports = mongoose.model('RawData', rawDataSchema);
