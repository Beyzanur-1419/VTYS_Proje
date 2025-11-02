const fs = require('fs').promises;
const path = require('path');
const { AppError } = require('../middleware/errorHandler');

class FileCleanupService {
  constructor() {
    this.tempDir = path.join(process.cwd(), 'temp');
    this.uploadsDir = path.join(process.cwd(), 'uploads');
    this.maxAge = 24 * 60 * 60 * 1000; // 24 saat
    this.setupDirectories();
  }

  async setupDirectories() {
    try {
      await fs.mkdir(this.tempDir, { recursive: true });
      await fs.mkdir(this.uploadsDir, { recursive: true });
    } catch (error) {
      console.error('Directory setup error:', error);
    }
  }

  async cleanupTempFiles() {
    try {
      const files = await fs.readdir(this.tempDir);
      const now = Date.now();

      for (const file of files) {
        const filePath = path.join(this.tempDir, file);
        const stats = await fs.stat(filePath);
        const age = now - stats.mtime.getTime();

        if (age > this.maxAge) {
          await fs.unlink(filePath);
        }
      }
    } catch (error) {
      console.error('Temp cleanup error:', error);
    }
  }

  async moveToUploads(tempPath, filename) {
    const targetPath = path.join(this.uploadsDir, filename);
    try {
      await fs.rename(tempPath, targetPath);
      return targetPath;
    } catch (error) {
      throw new AppError(500, 'Dosya taşıma hatası');
    }
  }

  async deleteFile(filename) {
    try {
      const filePath = path.join(this.uploadsDir, filename);
      await fs.unlink(filePath);
    } catch (error) {
      throw new AppError(500, 'Dosya silme hatası');
    }
  }

  startCleanupTask() {
    setInterval(() => {
      this.cleanupTempFiles().catch(console.error);
    }, 60 * 60 * 1000); // Her saat çalış
  }
}

module.exports = new FileCleanupService();