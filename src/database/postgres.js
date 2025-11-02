const { Sequelize } = require("sequelize");

let sequelize;

if (process.env.DATABASE_URL) {
  // Heroku veya diğer cloud servisleri için SSL yapılandırması
  sequelize = new Sequelize(process.env.DATABASE_URL, {
    dialect: "postgres",
    dialectOptions: {
      ssl: {
        require: true,
        rejectUnauthorized: false, // Self-signed sertifikalar için
      },
    },
    logging: false,
  });
} else {
  // Yerel geliştirme ortamı için
  sequelize = new Sequelize({
    host: process.env.DB_HOST || "localhost",
    port: process.env.DB_PORT || 5432,
    database: process.env.DB_NAME || "glowmance",
    username: process.env.DB_USER || "postgres",
    password: process.env.DB_PASSWORD || "1234",
    dialect: "postgres",
    logging: false,
  });
}

// Bağlantı test fonksiyonu
async function testConnection() {
  try {
    await sequelize.authenticate();
    console.log("PostgreSQL bağlantısı başarılı.");
    return true;
  } catch (error) {
    console.error("PostgreSQL bağlantı hatası:", error);
    return false;
  }
}

async function connectPostgres() {
  return testConnection();
}

module.exports = {
  sequelize,
  testConnection,
  connectPostgres,
};
