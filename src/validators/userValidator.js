const Joi = require('joi');

const updateProfileSchema = Joi.object({
  name: Joi.string().min(2).max(50),
  email: Joi.string().email(),
});

const changePasswordSchema = Joi.object({
  currentPassword: Joi.string().required(),
  newPassword: Joi.string().min(6).required(),
});

module.exports = {
  updateProfileSchema,
  changePasswordSchema,
};
