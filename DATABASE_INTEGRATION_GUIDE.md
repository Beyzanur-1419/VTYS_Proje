# Database Entegrasyon Rehberi

Database geliştiricileri için backend API ile entegrasyon rehberi.

## 🗄️ Database Yapısı

### PostgreSQL (SQL Database)

**Kullanım Alanları:**
- Kullanıcı bilgileri (Users tablosu)
- Analiz geçmişi (AnalysisHistory tablosu)

**Bağlantı Bilgileri:**

```env
# .env dosyası
DB_HOST=localhost
DB_PORT=5432
DB_NAME=glowmance
DB_USER=postgres
DB_PASSWORD=your_password

# Veya connection string
DATABASE_URL=postgresql://postgres:password@localhost:5432/glowmance
```

**Bağlantı Test:**
```javascript
// src/database/postgres.js
// Backend'de otomatik bağlanır
```

---

### MongoDB (NoSQL Database)

**Kullanım Alanları:**
- Görsel metadata (AIImage koleksiyonu)

**Bağlantı Bilgileri:**

```env
# .env dosyası
MONGODB_URI=mongodb://localhost:27017/glowmance
# Veya authenticated:
# MONGODB_URI=mongodb://username:password@localhost:27017/glowmance
```

**Bağlantı Test:**
```javascript
// src/database/mongodb.js
// Backend'de otomatik bağlanır
```

---

## 📊 Database Schema

### PostgreSQL Schema

#### Users Tablosu

```sql
CREATE TABLE "Users" (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL, -- bcrypt hashed
  name VARCHAR(255),
  refreshToken TEXT,
  resetToken VARCHAR(255),
  resetTokenExpires TIMESTAMP,
  createdAt TIMESTAMP DEFAULT NOW(),
  updatedAt TIMESTAMP DEFAULT NOW()
);
```

**Kullanım:**
- Kullanıcı kayıt/giriş
- Password hash: bcrypt (salt rounds: 10)
- JWT refresh token storage

---

#### AnalysisHistory Tablosu

```sql
CREATE TABLE "AnalysisHistories" (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  userId UUID NOT NULL REFERENCES "Users"(id),
  imageId VARCHAR(255) NOT NULL, -- MongoDB AIImage document ID
  results JSONB NOT NULL, -- ML analysis results
  timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
  createdAt TIMESTAMP DEFAULT NOW(),
  updatedAt TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_analysis_user ON "AnalysisHistories"(userId);
CREATE INDEX idx_analysis_image ON "AnalysisHistories"(imageId);
```

**Results JSONB Format:**
```json
{
  "confidence": 0.92,
  "conditions": ["Akne", "Kuruluk"],
  "recommendations": ["Nemlendirici", "Akne Kremi"],
  "metadata": {
    "processed_at": "2024-01-01T00:00:00.000Z",
    "fallback": false
  }
}
```

---

### MongoDB Schema

#### AIImage Koleksiyonu

```javascript
{
  _id: ObjectId,
  userId: String, // UUID from PostgreSQL
  originalName: String,
  mimeType: String,
  size: Number,
  url: String,
  cloudProvider: String, // 'cloudinary' | 's3' | 'local'
  publicId: String, // Cloudinary public_id (optional)
  cloudUrl: String, // Cloud storage URL (optional)
  width: Number,
  height: Number,
  format: String,
  timestamp: Date,
  createdAt: Date,
  updatedAt: Date
}
```

**Indexes:**
```javascript
// userId index
db.aiimages.createIndex({ userId: 1 });

// timestamp index
db.aiimages.createIndex({ timestamp: -1 });
```

---

## 🔌 Backend Database Entegrasyonu

### Bağlantı Kurulumu

Backend otomatik olarak database'lere bağlanır:

```javascript
// src/database/index.js
async function initializeDatabases() {
  // PostgreSQL bağlantısı
  const postgresConnected = await connectPostgres();
  
  // MongoDB bağlantısı
  const mongoConnected = await connectMongoDB();
  
  return postgresConnected && mongoConnected;
}
```

### Bağlantı Kontrolü

**Health Check:**
```bash
curl http://localhost:3000/health
```

**Response:**
```json
{
  "status": "ok",
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

---

## 📝 Database Migration

### Sequelize Migration

**Migration Çalıştırma:**
```bash
npm run migrate
```

**Migration Dosyası:**
- `src/database/migrations/20251020000000-initial-schema.js`

**Migration İçeriği:**
- Users tablosu oluşturma
- AnalysisHistory tablosu oluşturma
- Index'ler oluşturma

---

## 🔄 Veri Akışı

### 1. Kullanıcı Kaydı

```
Frontend → POST /api/auth/register
  ↓
Backend → PostgreSQL (Users tablosu)
  ↓
Password hash (bcrypt)
  ↓
User kaydı oluşturulur
  ↓
Response → Frontend
```

### 2. Görsel Yükleme

```
Frontend → POST /api/upload (multipart/form-data)
  ↓
Backend → Multer (file upload)
  ↓
Cloud Storage (Cloudinary/S3/Local)
  ↓
MongoDB → AIImage koleksiyonuna metadata kaydedilir
  ↓
ML Service → Görsel analizi
  ↓
PostgreSQL → AnalysisHistory tablosuna sonuç kaydedilir
  ↓
Response → Frontend (image + analysis)
```

### 3. Analiz Geçmişi Listeleme

```
Frontend → GET /api/analysis-history
  ↓
Backend → PostgreSQL (AnalysisHistory tablosu)
  ↓
MongoDB → AIImage koleksiyonundan image bilgileri alınır
  ↓
Response → Frontend (enriched analyses)
```

---

## 🛠️ Database Yönetimi

### PostgreSQL

**Veritabanı Oluşturma:**
```sql
CREATE DATABASE glowmance;
```

**Kullanıcı Oluşturma:**
```sql
CREATE USER glowmance_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE glowmance TO glowmance_user;
```

**Tablo Kontrol:**
```sql
-- Users tablosu
SELECT * FROM "Users";

-- AnalysisHistory tablosu
SELECT * FROM "AnalysisHistories";
```

---

### MongoDB

**Veritabanı Oluşturma:**
- Otomatik oluşturulur (ilk kayıtta)

**Koleksiyon Kontrol:**
```javascript
// MongoDB shell
use glowmance
db.aiimages.find()
db.aiimages.count()
```

---

## 🔍 Database Query Örnekleri

### PostgreSQL Queries

**Kullanıcı Analizleri:**
```sql
SELECT 
  ah.id,
  ah.userId,
  ah.imageId,
  ah.results->>'confidence' as confidence,
  ah.results->'conditions' as conditions,
  ah.results->'recommendations' as recommendations,
  ah.timestamp
FROM "AnalysisHistories" ah
WHERE ah.userId = 'user-uuid-here'
ORDER BY ah.timestamp DESC
LIMIT 20;
```

**İstatistikler:**
```sql
SELECT 
  COUNT(*) as total_analyses,
  AVG((ah.results->>'confidence')::float) as avg_confidence
FROM "AnalysisHistories" ah
WHERE ah.userId = 'user-uuid-here';
```

---

### MongoDB Queries

**Kullanıcı Görselleri:**
```javascript
db.aiimages.find({ userId: "user-uuid-here" })
  .sort({ timestamp: -1 })
  .limit(20);
```

**Cloud Provider'a Göre:**
```javascript
db.aiimages.find({ cloudProvider: "cloudinary" });
```

---

## 🔧 Backend Database Servisleri

### UserService (PostgreSQL)

**Konum:** `src/services/UserService.js`

**Metodlar:**
- `create(userData)` - Yeni kullanıcı
- `findById(id)` - ID ile bul
- `findByEmail(email)` - Email ile bul
- `update(id, data)` - Güncelle
- `comparePassword(user, password)` - Şifre kontrolü

---

### AnalysisHistoryService (PostgreSQL)

**Konum:** `src/services/AnalysisHistoryService.js`

**Metodlar:**
- `createAnalysis(userId, imageId, results)` - Yeni analiz
- `getUserAnalysisHistory(userId, options)` - Kullanıcı geçmişi
- `getAnalysisById(id, userId)` - ID ile bul
- `getAnalysisByImageId(imageId, userId)` - Image ID ile bul
- `deleteAnalysis(id, userId)` - Sil
- `getUserStatistics(userId)` - İstatistikler

---

### AIImageService (MongoDB)

**Konum:** `src/services/AIImageService.js`

**Metodlar:**
- `create(data)` - Yeni görsel metadata
- `findById(id)` - ID ile bul
- `findByUserId(userId)` - Kullanıcı görselleri
- `update(id, data)` - Güncelle
- `delete(id)` - Sil

---

## ⚙️ Database Configuration

### PostgreSQL Configuration

**Connection Pool:**
```javascript
// src/database/postgres.js
sequelize = new Sequelize({
  host: process.env.DB_HOST || "localhost",
  port: process.env.DB_PORT || 5432,
  database: process.env.DB_NAME || "glowmance",
  username: process.env.DB_USER || "postgres",
  password: process.env.DB_PASSWORD,
  dialect: "postgres",
  logging: false,
  pool: {
    max: 5,
    min: 0,
    acquire: 30000,
    idle: 10000
  }
});
```

### MongoDB Configuration

**Connection Options:**
```javascript
// src/database/mongodb.js
await mongoose.connect(mongoUri, {
  useNewUrlParser: true,
  useUnifiedTopology: true,
  maxPoolSize: 10,
  serverSelectionTimeoutMS: 5000,
  socketTimeoutMS: 45000,
});
```

---

## 🔄 Veri Senkronizasyonu

### PostgreSQL ↔ MongoDB

**İlişki:**
- `AnalysisHistory.imageId` → MongoDB `AIImage._id`
- `AIImage.userId` → PostgreSQL `Users.id`

**Örnek Query (Join-like):**
```javascript
// Backend'de otomatik join
const analysis = await AnalysisHistory.findOne({
  where: { id: analysisId }
});

const image = await AIImage.findById(analysis.imageId);
```

---

## 🧪 Database Test

### PostgreSQL Test

```javascript
// Test connection
const { sequelize } = require('./src/database/postgres');
await sequelize.authenticate();
console.log('PostgreSQL connected');
```

### MongoDB Test

```javascript
// Test connection
const mongoose = require('mongoose');
await mongoose.connect(mongoUri);
console.log('MongoDB connected');
```

---

## 📚 Detaylı Dokümantasyon

- `BACKEND_API_DOCUMENTATION.md` - API endpoint'leri
- `DATA_SECURITY_VERIFICATION.md` - Güvenlik ve encryption
- `ANALYSIS_HISTORY_VERIFICATION.md` - AnalysisHistory detayları

