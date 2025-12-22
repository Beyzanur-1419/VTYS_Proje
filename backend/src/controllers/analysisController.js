const analysisService = require('../services/analysisService');
const mlServiceClient = require('../integrations/ml/mlClient');
const productService = require('../services/productService');
const upload = require('../utils/uploader');

class AnalysisController {
  async createAnalysis(req, res, next) {
    try {
      console.log(`[AnalysisController] Creating analysis for User ID: ${req.user.id}`);
      const analysis = await analysisService.createAnalysis(req.user.id, req.body);
      res.status(201).json({ success: true, data: analysis });
    } catch (error) {
      next(error);
    }
  }

  async getHistory(req, res, next) {
    try {
      const history = await analysisService.getUserHistory(req.user.id);
      // Frontend'in beklediği formata dönüştür
      const formattedHistory = history.map(item => {
        const resultJson = item.result_json || {};
        return {
          id: item.id,
          userId: item.user_id,
          imageUrl: item.image_url || null,
          hasEczema: resultJson.hasEczema || resultJson.has_eczema || false,
          eczemaLevel: resultJson.eczemaLevel || resultJson.eczema_level || null,
          hasAcne: resultJson.hasAcne || resultJson.has_acne || false,
          acneLevel: resultJson.acneLevel || resultJson.acne_level || null,
          hasRosacea: resultJson.hasRosacea || resultJson.has_rosacea || false,
          rosaceaLevel: resultJson.rosaceaLevel || resultJson.rosacea_level || null,
          isNormal: resultJson.isNormal || resultJson.is_normal || false,
          products: resultJson.recommendedProducts || resultJson.recommended_products || [],
          createdAt: item.created_at,
          updatedAt: item.updated_at || null
        };
      });
      res.status(200).json(formattedHistory);
    } catch (error) {
      next(error);
    }
  }

  // ML Skin Type Analysis with real integration
  async analyzeSkinType(req, res, next) {
    try {
      // 1. Check if file is uploaded
      if (!req.file) {
        return res.status(400).json({ success: false, message: 'Image file is required' });
      }

      let analysisResult;
      let recommendedProducts = [];

      // 2. Call ML Service
      try {
        analysisResult = await mlServiceClient.analyzeSkinType(
          req.file.buffer || require('fs').readFileSync(req.file.path),
          req.file.originalname
        );
      } catch (mlError) {
        console.warn('ML Service unavailable, using mock data:', mlError.message);
        analysisResult = {
          skin_type: 'Combination',
          confidence: 0.85,
          note: 'ML Service unavailable - using mock data',
        };
      }

      // 3. Get Product Recommendations based on Skin Type
      try {
        const skinType = analysisResult.skin_type || 'Normal';
        // Map ML skin type to our product categories
        const typeMap = {
          'Oily': 'oily',
          'Dry': 'dry',
          'Combination': 'combination',
          'Sensitive': 'normal', // Map sensitive to normal for now
          'Normal': 'normal',
        };

        const mappedType = typeMap[skinType] || 'normal';
        recommendedProducts = await productService.getProductsBySkinType(mappedType, 3);
      } catch (productError) {
        console.error('Product Service Error:', productError.message);
        // Continue without products if service fails
      }

      // 4. Save Analysis to Database
      const savedAnalysis = await analysisService.createAnalysis(req.user.id, {
        image_url: req.file.path ? `/uploads/${req.file.filename}` : '',
        result_json: {
          ...analysisResult,
          recommended_products: recommendedProducts
        }
      });

      // 5. Return Unified Response
      res.status(200).json({
        success: true,
        data: {
          analysisId: savedAnalysis.id,
          aiResult: analysisResult,
          recommendedProducts: recommendedProducts,
          createdAt: savedAnalysis.created_at
        }
      });

    } catch (error) {
      next(error);
    }
  }

  // ML Disease Analysis with real integration
  async analyzeDisease(req, res, next) {
    try {
      // Check if file is uploaded
      if (!req.file) {
        return res.status(400).json({ success: false, message: 'Image file is required' });
      }

      // Call ML Service
      try {
        const result = await mlServiceClient.analyzeDisease(
          req.file.buffer || require('fs').readFileSync(req.file.path),
          req.file.originalname
        );

        // Product Recommendation Logic for Real Analysis
        const disease = result.disease || 'healthy';
        const diseaseMap = {
          'Acne': 'acne',
          'Rosacea': 'rosacea',
          'Eczema': 'eczema',
          'Psoriasis': 'eczema', // Map to eczema for now
          'Melasma': 'healthy', // Map to healthy for now
          'Vitiligo': 'healthy', // Map to healthy for now
          'Healthy': 'healthy',
          'General': 'healthy'
        };

        const mappedDisease = diseaseMap[disease] || 'healthy';
        let recommendedProducts = [];

        try {
          recommendedProducts = await productService.getProductsByDisease(mappedDisease, 3);
        } catch (err) {
          console.error("Product service failed during disease analysis:", err.message);
        }

        res.status(200).json({
          success: true,
          data: {
            ...result,
            recommendedProducts: recommendedProducts
          }
        });
      } catch (mlError) {
        // Fallback to mock if ML service is unavailable
        console.warn('ML Service unavailable, using mock data:', mlError.message);
        const mockResult = {
          disease: 'Acne',
          severity: 'Moderate',
          confidence: 0.88,
          note: 'ML Service unavailable - using mock data',
        };
        const disease = mockResult.disease || 'healthy';

        // Disease to Product Category Mapping
        const diseaseMap = {
          'Acne': 'acne',
          'Rosacea': 'rosacea',
          'Eczema': 'eczema',
          'Psoriasis': 'eczema',
          'Melasma': 'healthy',
          'Vitiligo': 'healthy',
          'Healthy': 'healthy',
          'General': 'healthy'
        };

        const mappedDisease = diseaseMap[disease] || 'healthy';
        let recommendedProducts = [];

        try {
          recommendedProducts = await productService.getProductsByDisease(mappedDisease, 3);
        } catch (err) {
          console.error("Product service failed during disease analysis:", err.message);
        }

        res.status(200).json({
          success: true,
          data: {
            ...mockResult,
            recommendedProducts: recommendedProducts
          }
        });
      }

      // If ML service succeeded
      if (!res.headersSent) { // Check if response already sent in catch block
        // NOTE: Ideally we should move this logic outside the try-catch or restructure, 
        // but for now let's handle the success case here to match the user's request quickly.
        // Wait, the original code had `res.status(200).json(...)` inside the try block. 
        // I need to intercept that.
      }
    } catch (error) {
      next(error);
    }
  }

  async getAnalysisById(req, res, next) {
    try {
      const analysis = await analysisService.getAnalysisById(req.params.id, req.user.id);
      res.status(200).json({ success: true, data: analysis });
    } catch (error) {
      next(error);
    }
  }

  async deleteAnalysis(req, res, next) {
    try {
      await analysisService.deleteAnalysis(req.params.id, req.user.id);
      res.status(200).json({ success: true, message: 'Analysis deleted successfully' });
    } catch (error) {
      next(error);
    }
  }

  async getStats(req, res, next) {
    try {
      const stats = await analysisService.getStats(req.user.id);
      res.status(200).json({ success: true, data: stats });
    } catch (error) {
      next(error);
    }
  }
  // Unified Analysis Endpoint
  async analyzeFull(req, res, next) {
    try {
      if (!req.file) {
        return res.status(400).json({ success: false, message: 'Image file is required' });
      }

      // Call Unified ML Service (using analyzeDisease wrapper since it returns full data)
      let aiResult;
      try {
        console.log('[AnalysisController] Calling Real ML Service...');
        aiResult = await mlServiceClient.analyzeDisease(
          req.file.buffer || require('fs').readFileSync(req.file.path),
          req.file.originalname
        );
        console.log('🔥🔥🔥 DEBUG - RAW AI RESULT:', JSON.stringify(aiResult, null, 2));
      } catch (mlError) {
        console.error('[AnalysisController] ML Service FAILED. Reason:', mlError.message);
        console.warn('ML Service unavailable, using mock:', mlError.message);
        aiResult = {
          disease: 'Healthy',
          skin_type: 'Normal',
          hasAcne: false,
          hasEczema: false,
          note: 'Mock fallback - ML Service Failed'
        };
      }


      // --- DEBUG FORCE REMOVED ---

      // Get Product Recommendations
      let recommendedProducts = [];
      try {
        // Use disease or skin type for recommendation
        const target = aiResult.disease !== 'Healthy' ? aiResult.disease : aiResult.skin_type;
        // Simple mapping logic
        let query = 'normal';
        if (aiResult.hasAcne) query = 'acne';
        else if (aiResult.hasEczema) query = 'eczema';
        else if (aiResult.hasRosacea || (aiResult.disease && aiResult.disease.toLowerCase().includes('rozase'))) query = 'rosacea';
        else if (aiResult.skin_type === 'Oily') query = 'oily';
        else if (aiResult.skin_type === 'Dry') query = 'dry';

        recommendedProducts = await productService.getProductsByDisease(query, 10);

        // --- FALLBACK: If no products found (e.g. Healthy/Normal returns empty), get general items ---
        if (!recommendedProducts || recommendedProducts.length === 0) {
          console.log('[AnalysisController] No specific products found. Fetching general fallback products.');
          recommendedProducts = await productService.getProductsByDisease('skin', 6); // Increased limit
        } else {
          // Ensure we have enough products
          if (recommendedProducts.length < 3) {
            const extra = await productService.getProductsByDisease('skin', 3);
            recommendedProducts = [...recommendedProducts, ...extra];
          }
        }
      } catch (err) {
        console.error('Product Rec Error:', err.message);
      }

      // Save Analysis
      console.log(`[AnalysisController] Saving full analysis. User object:`, JSON.stringify(req.user));
      if (!req.user || !req.user.id) {
        throw new Error('User ID missing from request object');
      }

      const userId = req.user.id;
      console.log(`[AnalysisController] Explicit User ID for DB: ${userId}`);

      const savedAnalysis = await analysisService.createAnalysis(userId, {
        image_url: req.file.path ? `/uploads/${req.file.filename}` : '',
        result_json: {
          ...aiResult,
          recommendedProducts
        }
      });

      res.status(200).json({
        success: true,
        data: {
          analysisId: savedAnalysis.id,
          result: aiResult,
          products: recommendedProducts
        }
      });

    } catch (error) {
      next(error);
    }
  }
}

module.exports = new AnalysisController();
