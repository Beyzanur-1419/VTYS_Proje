const Joi = require('joi');

const analysisSchema = Joi.object({
  image_url: Joi.string().uri().required(),
  result_json: Joi.object({
    skin_type: Joi.string().valid('Oily', 'Dry', 'Combination', 'Normal', 'Sensitive').required(),
    confidence: Joi.number().min(0).max(1).required(),
    disease: Joi.string().allow(null, ''),
    severity: Joi.string().allow(null, ''),
    details: Joi.object().unknown(true),
  }).required(),
});

module.exports = {
  analysisSchema,
};
