const mongoose = require("mongoose");

const aiImageSchema = new mongoose.Schema(
  {
    userId: {
      type: String,
      required: true,
      index: true,
    },
    imageUrl: {
      type: String,
      required: true,
    },
    cloudinaryId: {
      type: String,
      required: true,
    },
    uploadDate: {
      type: Date,
      default: Date.now,
    },
    metadata: {
      originalName: String,
      mimeType: String,
      size: Number,
    },
  },
  { timestamps: true }
);

const AIImage = mongoose.model("AIImage", aiImageSchema);

module.exports = AIImage;
