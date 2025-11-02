const { body } = require("express-validator");

const uploadValidator = [
  body("file").custom((value, { req }) => {
    if (!req.file) {
      throw new Error("Lütfen bir görsel seçin");
    }

    // Dosya tipi kontrolü
    const allowedTypes = ["image/jpeg", "image/png", "image/webp"];
    if (!allowedTypes.includes(req.file.mimetype)) {
      throw new Error("Sadece JPEG, PNG ve WebP formatları desteklenir");
    }

    // Dosya boyutu kontrolü (5MB)
    const maxSize = 5 * 1024 * 1024;
    if (req.file.size > maxSize) {
      throw new Error("Dosya boyutu 5MB'dan küçük olmalıdır");
    }

    return true;
  }),
];

const analysisMetadataValidator = [
  body("skinConditions")
    .isArray()
    .withMessage("Cilt durumları liste formatında olmalıdır")
    .notEmpty()
    .withMessage("En az bir cilt durumu belirtilmelidir"),

  body("confidence")
    .isFloat({ min: 0, max: 1 })
    .withMessage("Güven değeri 0-1 arasında olmalıdır"),

  body("recommendations")
    .isArray()
    .withMessage("Öneriler liste formatında olmalıdır")
    .notEmpty()
    .withMessage("En az bir öneri belirtilmelidir"),
];

module.exports = {
  uploadValidator,
  analysisMetadataValidator,
};
