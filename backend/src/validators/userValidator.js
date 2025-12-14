const Joi = require('joi');

const updateProfileSchema = Joi.object({
  name: Joi.string().min(2).max(50),
  email: Joi.string().email(),
});

const updateSkinProfileSchema = Joi.object({
  skinType: Joi.string().allow(null, ''),
  skinGoal: Joi.string().allow(null, ''),
  age: Joi.number().integer().min(1).max(120).allow(null),
});

const updateSettingsSchema = Joi.object({
  notificationEnabled: Joi.boolean(),
  emailNotifications: Joi.boolean(),
});

const changePasswordSchema = Joi.object({
  currentPassword: Joi.string().required(),
  newPassword: Joi.string().min(6).required(),
});

module.exports = {
  updateProfileSchema,
  updateSkinProfileSchema,
  updateSettingsSchema,
  changePasswordSchema,
};
