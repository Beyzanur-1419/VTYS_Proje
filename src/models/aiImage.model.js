const mongoose = require("mongoose");

const aiImageSchema = new mongoose.Schema(
  {
    userId: {
      type: String, // UUID from SQL
      required: true,
      index: true,
    },
    originalName: {
      type: String,
      required: true,
    },
    mimeType: {
      type: String,
      required: true,
    },
    size: {
      type: Number,
      required: true,
    },
    url: {
      type: String,
      required: true,
    },
    cloudProvider: {
      type: String,
      enum: ['cloudinary', 's3', 'local'],
      default: 'local',
      required: true,
    },
    publicId: {
      type: String,
      required: false, // Only for cloudinary
    },
    cloudUrl: {
      type: String,
      required: false, // Cloud storage URL (Cloudinary/S3)
    },
    width: {
      type: Number,
      required: false,
    },
    height: {
      type: Number,
      required: false,
    },
    format: {
      type: String,
      required: false,
    },
    timestamp: {
      type: Date,
      required: true,
      index: true,
    },
  },
  {
    timestamps: true,
  }
);

module.exports = mongoose.model("AIImage", aiImageSchema);
