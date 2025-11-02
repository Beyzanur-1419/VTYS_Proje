const cloudinary = require("cloudinary").v2;
const Config = require("./config");

// Cloudinary configuration
if (Config.CLOUDINARY_URL) {
  cloudinary.config({
    cloud_name: Config.CLOUDINARY_CLOUD_NAME,
    api_key: Config.CLOUDINARY_API_KEY,
    api_secret: Config.CLOUDINARY_API_SECRET,
  });
}

const uploadToCloudinary = async (file) => {
  if (!Config.CLOUDINARY_URL) {
    return null;
  }

  try {
    const result = await cloudinary.uploader.upload(file.path, {
      folder: "glowmance/uploads",
      resource_type: "auto",
    });

    return {
      url: result.secure_url,
      cloudinaryId: result.public_id,
    };
  } catch (error) {
    console.error("Cloudinary upload error:", error);
    return null;
  }
};

module.exports = {
  cloudinary,
  uploadToCloudinary,
};
