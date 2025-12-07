const express = require('express');
const userController = require('../controllers/userController');
const validate = require('../middleware/validateMiddleware');
const { updateProfileSchema } = require('../validators/userValidator');
const { protect } = require('../middleware/authMiddleware');

const router = express.Router();

router.use(protect);

router.get('/profile', userController.getProfile);
router.put('/profile', validate(updateProfileSchema), userController.updateProfile);

module.exports = router;
