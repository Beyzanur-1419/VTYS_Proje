const multer = require('multer');
const path = require('path');
const { BadRequestError } = require('../utils/errors');

// Desteklenen dosya tipleri
const ALLOWED_MIME_TYPES = ['image/jpeg', 'image/png', 'image/webp'];
const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

// Multer konfigürasyonu
const storage = multer.memoryStorage(); // Dosyaları geçici olarak bellekte tut

const fileFilter = (req, file, cb) => {
  // MIME type kontrolü
  if (!ALLOWED_MIME_TYPES.includes(file.mimetype)) {
    return cb(new BadRequestError('Desteklenmeyen dosya formatı. Sadece JPEG, PNG ve WebP desteklenir.'));
  }
  cb(null, true);
};

const upload = multer({
  storage,
  fileFilter,
  limits: {
    fileSize: MAX_FILE_SIZE
  }
});

module.exports = upload;