const mongoose = require("mongoose");

async function connectMongoDB() {
  try {
    const mongoUri = process.env.MONGODB_URI || 'mongodb://localhost:27017/glowmance';
    
    await mongoose.connect(mongoUri, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });
    console.log("MongoDB bağlantısı başarılı.");
    return true;
  } catch (error) {
    console.error("MongoDB bağlantı hatası:", error);
    return false;
  }
}

// Bağlantı durumunu dinle
mongoose.connection.on("disconnected", () => {
  console.log("MongoDB bağlantısı kesildi");
});

mongoose.connection.on("error", (err) => {
  console.error("MongoDB bağlantı hatası:", err);
});

module.exports = {
  connectMongoDB,
  connection: mongoose.connection,
};
