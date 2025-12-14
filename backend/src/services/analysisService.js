const AnalysisHistory = require('../models/AnalysisHistory');
const ImageLog = require('../models/ImageLog');

class AnalysisService {
  async createAnalysis(userId, data) {
    // 1. Save to PostgreSQL
    const analysis = await AnalysisHistory.create({
      user_id: userId,
      image_url: data.image_url,
      result_json: data.result_json,
    });

    // 2. Log to MongoDB (optional but good for analytics)
    await this.logAnalysisToMongo({
      userId,
      analysisId: analysis.id,
      ...data
    });

    return analysis;
  }

  async getUserHistory(userId) {
    return await AnalysisHistory.findAll({
      where: { user_id: userId },
      order: [['created_at', 'DESC']],
    });
  }

  async getAnalysisById(id, userId) {
    const analysis = await AnalysisHistory.findOne({
      where: { id, user_id: userId }
    });
    if (!analysis) {
      throw new Error('Analysis not found');
    }
    return analysis;
  }

  async deleteAnalysis(id, userId) {
    const result = await AnalysisHistory.destroy({
      where: { id, user_id: userId }
    });
    if (result === 0) {
      throw new Error('Analysis not found');
    }
    return { message: 'Analysis deleted successfully' };
  }

  async getStats(userId) {
    const total = await AnalysisHistory.count({ where: { user_id: userId } });
    
    // Example stats - can be expanded based on JSON content
    // This requires parsing JSON which might be heavy for SQL
    // Better to use MongoDB for aggregation if needed
    
    return {
      totalAnalyses: total,
      lastAnalysis: await AnalysisHistory.findOne({
        where: { user_id: userId },
        order: [['created_at', 'DESC']]
      })
    };
  }

  async logAnalysisToMongo(data) {
    try {
      await ImageLog.create({
        userId: data.userId,
        filename: data.image_url.split('/').pop(),
        raw_output: data.result_json,
        detected_type: data.result_json.skin_type || 'unknown',
      });
    } catch (error) {
      console.error('Failed to log to MongoDB:', error.message);
    }
  }
}

module.exports = new AnalysisService();
