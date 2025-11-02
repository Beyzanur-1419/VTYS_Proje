const mongoose = require("mongoose");

const AIImageSchema = new mongoose.Schema(
  {
    userId: {
      type: String,
      required: true,
      index: true,
    },
    originalName: {
      type: String,
      required: true,
    },
    fileName: {
      type: String,
      required: true,
      unique: true,
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
    cloudinaryId: {
      type: String,
      sparse: true,
    },
    localPath: {
      type: String,
      sparse: true,
    },
    metadata: {
      width: Number,
      height: Number,
      format: String,
      hasAlpha: Boolean,
      colorSpace: String,
    },
    modelAnalysis: {
      type: mongoose.Schema.Types.Mixed,
      default: null,
    },
    status: {
      type: String,
      enum: ["pending", "analyzed", "failed"],
      default: "pending",
    },
  },
  {
    timestamps: true,
  }
);

// İndeksler
AIImageSchema.index({ userId: 1, createdAt: -1 });
AIImageSchema.index({ status: 1 });

module.exports = mongoose.model("AIImage", AIImageSchema);
