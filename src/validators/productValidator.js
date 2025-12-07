const Joi = require('joi');

const createProductSchema = Joi.object({
  title: Joi.string().min(3).max(100).required(),
  brand: Joi.string().min(2).max(50).required(),
  price: Joi.number().precision(2).min(0).required(),
  ingredients: Joi.string().allow('', null),
  image_url: Joi.string().uri().allow('', null),
  description: Joi.string().max(1000).allow('', null),
  category: Joi.string().max(50).allow('', null),
  stock: Joi.number().integer().min(0).default(0),
});

const updateProductSchema = Joi.object({
  title: Joi.string().min(3).max(100),
  brand: Joi.string().min(2).max(50),
  price: Joi.number().precision(2).min(0),
  ingredients: Joi.string().allow('', null),
  image_url: Joi.string().uri().allow('', null),
  description: Joi.string().max(1000).allow('', null),
  category: Joi.string().max(50).allow('', null),
  stock: Joi.number().integer().min(0),
});

module.exports = {
  createProductSchema,
};
