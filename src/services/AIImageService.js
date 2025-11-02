const AIImage = require('../models/aiImage.model');

class AIImageService {
  async create(data) {
    return AIImage.create(data);
  }

  async findById(id) {
    return AIImage.findById(id);
  }

  async findByUserId(userId) {
    return AIImage.find({ userId }).sort({ timestamp: -1 });
  }

  async delete(id) {
    return AIImage.findByIdAndDelete(id);
  }

  async update(id, data) {
    return AIImage.findByIdAndUpdate(id, data, { new: true });
  }
}

module.exports = AIImageService;

