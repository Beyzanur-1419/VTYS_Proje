const express = require('express');
const userController = require('../controllers/userController');
const validate = require('../middleware/validateMiddleware');
const { updateProfileSchema, updateSkinProfileSchema, updateSettingsSchema } = require('../validators/userValidator');
const { protect } = require('../middleware/authMiddleware');

const router = express.Router();

router.use(protect);

router.get('/profile', userController.getProfile);
router.put('/profile', validate(updateProfileSchema), userController.updateProfile);

// Cilt profili
router.put('/skin-profile', validate(updateSkinProfileSchema), userController.updateSkinProfile);

// Ayarlar
router.get('/settings', userController.getSettings);
router.put('/settings', validate(updateSettingsSchema), userController.updateSettings);

// Bildirimler
router.get('/notifications', userController.getNotifications);

module.exports = router;
