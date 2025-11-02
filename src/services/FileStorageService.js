const multer = require('multer');
const path = require('path');
const fs = require('fs').promises;
const { AppError } = require('../middleware/errorHandler');
const Config = require('../config/config');

class FileValidationService {
  constructor() {
    this.allowedMimeTypes = ['image/jpeg', 'image/png', 'image/webp'];
    this.maxFileSize = 5 * 1024 * 1024; // 5MB
  }

  validateFileType(file) {
    if (!this.allowedMimeTypes.includes(file.mimetype)) {
      throw new AppError(400, 'Desteklenmeyen dosya tipi. Sadece JPEG, PNG ve WEBP formatları kabul edilir.');
    }
  }

  validateFileSize(file) {
    if (file.size > this.maxFileSize) {
      throw new AppError(400, 'Dosya boyutu çok büyük. Maximum 5MB kabul edilir.');
    }
  }

  async validateImageDimensions(file) {
    // Görsel boyutları kontrolü eklenebilir
    return true;
  }
}

class FileStorageService {
  constructor() {
    this.validator = new FileValidationService();
    this.uploadDir = path.join(process.cwd(), 'uploads');
    this.setupStorage();
  }

  async setupStorage() {
    try {
      await fs.access(this.uploadDir);
    } catch {
      await fs.mkdir(this.uploadDir, { recursive: true });
    }
  }

  getMulterStorage() {
    return multer.diskStorage({
      destination: (req, file, cb) => {
        cb(null, this.uploadDir);
      },
      filename: (req, file, cb) => {
        const uniqueSuffix = `${Date.now()}-${Math.round(Math.random() * 1E9)}`;
        const ext = path.extname(file.originalname);
        cb(null, `${uniqueSuffix}${ext}`);
      }
    });
  }

  getMulterUpload() {
    const storage = this.getMulterStorage();
    
    return multer({
      storage,
      fileFilter: (req, file, cb) => {
        try {
          this.validator.validateFileType(file);
          cb(null, true);
        } catch (error) {
          cb(error);
        }
      },
      limits: {
        fileSize: this.validator.maxFileSize
      }
    });
  }

  async deleteFile(filename) {
    const filepath = path.join(this.uploadDir, filename);
    try {
      await fs.unlink(filepath);
    } catch (error) {
      console.error(`Dosya silinirken hata: ${error.message}`);
      throw new AppError(500, 'Dosya silinirken hata oluştu');
    }
  }
}

module.exports = new FileStorageService();