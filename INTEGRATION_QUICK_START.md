# 🚀 GLOWMANCE Backend - Hızlı Entegrasyon Rehberi

Backend API'sini database ve frontend ile entegre etmek için hızlı başlangıç rehberi.

## 📋 Genel Bakış

Backend API **standalone** çalışabilir (database olmadan bile). Database'ler ve ML Service **optional**'dır.

---

## ✅ Backend API Çalıştırma (Database Olmadan)

Backend API database olmadan da çalışabilir (limited mode):

```powershell
# 1. .env dosyası oluştur (en az JWT_SECRET gerekli)
# 2. npm install
npm install

# 3. Server'ı başlat
npm run dev
```

**Minimum .env:**
```env
JWT_SECRET=your-secret-key-here
REFRESH_TOKEN_SECRET=your-refresh-secret-key-here
```

**Database Olmadan:**
- ✅ Health check çalışır
- ✅ API endpoint'leri çalışır
- ⚠️ Database-dependent endpoint'ler hata verebilir

---

## 🗄️ Database Entegrasyonu

### PostgreSQL Entegrasyonu

**1. PostgreSQL Servisini Başlatın:**

```powershell
# Windows Services'den başlatın:
# 1. Windows + R
# 2. services.msc
# 3. "postgresql" servisini bulun ve başlatın

# Veya:
net start postgresql-x64-13
```

**2. .env Dosyasını Güncelleyin:**

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=glowmance
DB_USER=postgres
DB_PASSWORD=your_password
```

**3. Database Oluşturun:**

```sql
CREATE DATABASE glowmance;
```

**4. Migration Çalıştırın:**

```powershell
npm run migrate
```

**Detaylı Rehber:** `DATABASE_INTEGRATION_GUIDE.md`

---

### MongoDB Entegrasyonu

**1. MongoDB Servisini Başlatın:**

```powershell
# Windows Services'den başlatın:
# 1. Windows + R
# 2. services.msc
# 3. "MongoDB" servisini bulun ve başlatın

# Veya:
net start MongoDB
```

**2. .env Dosyasını Güncelleyin:**

```env
MONGODB_URI=mongodb://localhost:27017/glowmance
```

**3. Backend Otomatik Bağlanır:**

Backend başladığında MongoDB'ye otomatik bağlanır.

**Detaylı Rehber:** `DATABASE_INTEGRATION_GUIDE.md`

---

## 🎨 Frontend Entegrasyonu

### 1. Base URL Yapılandırması

```javascript
// Frontend'de
const API_BASE_URL = 'http://localhost:3000/api';
// Production: 'https://api.glowmance.com/api'
```

### 2. Authentication Setup

```javascript
// Axios interceptor
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### 3. API Call Örnekleri

**Login:**
```javascript
const response = await axios.post(`${API_BASE_URL}/auth/login`, {
  email: 'user@example.com',
  password: 'Test123'
});

const { accessToken, refreshToken, user } = response.data.data;
localStorage.setItem('accessToken', accessToken);
```

**Upload Image:**
```javascript
const formData = new FormData();
formData.append('image', imageFile);

const response = await axios.post(`${API_BASE_URL}/upload`, formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
});

const { image, analysis } = response.data.data;
```

**Get Analysis History:**
```javascript
const response = await axios.get(`${API_BASE_URL}/analysis-history`, {
  params: { limit: 20, page: 1 }
});

const { analyses, pagination } = response.data.data;
```

**Detaylı Rehber:** `FRONTEND_INTEGRATION_GUIDE.md`

---

## 🤖 ML Service Entegrasyonu

### 1. ML Service Başlatma

```bash
cd ml_service
pip install -r requirements.txt
python main.py
```

### 2. Backend Yapılandırması

**.env dosyası:**
```env
ML_SERVICE_URL=http://localhost:8000
```

### 3. ML Service Response Format

**Beklenen Format:**
```json
{
  "status": "success",
  "data": {
    "confidence": 0.92,
    "conditions": ["Akne", "Kuruluk"],
    "recommendations": ["Nemlendirici", "Akne Kremi"],
    "metadata": {}
  }
}
```

**Alternatif Format (Doğrudan data):**
```json
{
  "confidence": 0.92,
  "conditions": ["Akne", "Kuruluk"],
  "recommendations": ["Nemlendirici", "Akne Kremi"],
  "metadata": {}
}
```

Backend her iki formatı da destekler.

**Detaylı Rehber:** `ML_SERVICE_INTEGRATION_GUIDE.md`

---

## 🔄 Entegrasyon Akışı

### Tam Entegrasyon Senaryosu

```
1. Frontend → POST /api/auth/login
   ↓
2. Backend → PostgreSQL (User verification)
   ↓
3. Backend → Frontend (accessToken, refreshToken)
   ↓
4. Frontend → POST /api/upload (image file)
   ↓
5. Backend → Cloud Storage (Cloudinary/S3/Local)
   ↓
6. Backend → MongoDB (image metadata)
   ↓
7. Backend → ML Service (image buffer)
   ↓
8. ML Service → Backend (analysis results)
   ↓
9. Backend → PostgreSQL (AnalysisHistory)
   ↓
10. Backend → Frontend (image + analysis)
```

---

## ✅ Backend Başlatma Checklist

- [ ] `.env` dosyası oluşturuldu
- [ ] `JWT_SECRET` ve `REFRESH_TOKEN_SECRET` eklendi
- [ ] `npm install` çalıştırıldı
- [ ] PostgreSQL servisi çalışıyor (optional)
- [ ] MongoDB servisi çalışıyor (optional)
- [ ] ML Service çalışıyor (optional)
- [ ] `npm run dev` çalıştırıldı
- [ ] Health check başarılı: `http://localhost:3000/health`

---

## 🔧 Backend Yapılandırması

### Minimum .env (Database Olmadan)

```env
PORT=3000
NODE_ENV=development
JWT_SECRET=your-super-secret-jwt-key-12345
REFRESH_TOKEN_SECRET=your-super-secret-refresh-token-key-67890
```

### Tam .env (Tüm Özelliklerle)

```env
# Server
PORT=3000
NODE_ENV=development

# PostgreSQL
DB_HOST=localhost
DB_PORT=5432
DB_NAME=glowmance
DB_USER=postgres
DB_PASSWORD=your_password

# MongoDB
MONGODB_URI=mongodb://localhost:27017/glowmance

# JWT
JWT_SECRET=your-super-secret-jwt-key-12345
REFRESH_TOKEN_SECRET=your-super-secret-refresh-token-key-67890

# ML Service
ML_SERVICE_URL=http://localhost:8000

# Cloud Storage (Optional)
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret

# AWS S3 (Optional)
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_BUCKET_NAME=your-bucket-name
AWS_REGION=eu-central-1
```

---

## 📚 Detaylı Dokümantasyon

### Backend API

- **`BACKEND_API_DOCUMENTATION.md`** - Tüm API endpoint'leri ve detayları
- **`API_ENDPOINTS_SUMMARY.md`** - Hızlı endpoint özeti

### Entegrasyon Rehberleri

- **`FRONTEND_INTEGRATION_GUIDE.md`** - Frontend entegrasyon rehberi
  - API client setup
  - Authentication
  - React hooks örnekleri
  - TypeScript type definitions
  - Error handling

- **`DATABASE_INTEGRATION_GUIDE.md`** - Database entegrasyon rehberi
  - PostgreSQL schema
  - MongoDB schema
  - Database queries
  - Migration guide

- **`ML_SERVICE_INTEGRATION_GUIDE.md`** - ML Service entegrasyon rehberi
  - ML Service API endpoints
  - Response format requirements
  - Backend entegrasyonu
  - Fallback mekanizması

### Test Rehberleri

- **`API_AUTH_TEST.md`** - Auth endpoint testleri
- **`UPLOAD_FLOW_TEST.md`** - Upload endpoint testleri
- **`ANALYSIS_HISTORY_LIST_API.md`** - Analysis history testleri
- **`RECOMMENDATION_API_VERIFICATION.md`** - Recommendations testleri

### Güvenlik

- **`DATA_SECURITY_VERIFICATION.md`** - Veri güvenliği doğrulama raporu

---

## 🆘 Sorun Giderme

### Backend Başlamıyor

1. `.env` dosyasını kontrol edin
2. `JWT_SECRET` ve `REFRESH_TOKEN_SECRET` var mı?
3. `npm install` çalıştırıldı mı?

**Detaylı Rehber:** `NODEMON_TROUBLESHOOTING.md`

### Database Bağlantı Hatası

1. Database servisleri çalışıyor mu?
2. `.env` dosyasındaki connection bilgileri doğru mu?
3. Database oluşturuldu mu?

**Not:** Database olmadan da backend çalışır (limited mode)

### ML Service Bağlantı Hatası

1. ML Service çalışıyor mu? (`http://localhost:8000/health`)
2. `.env` dosyasında `ML_SERVICE_URL` doğru mu?

**Not:** ML Service olmadan da backend çalışır (fallback mode)

---

## 🎯 Hızlı Test

### 1. Health Check

```bash
curl http://localhost:3000/health
```

**Beklenen:**
```json
{
  "status": "ok",
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

### 2. API Endpoint Test

```bash
# Login test (database olmadan çalışmaz)
curl -X POST "http://localhost:3000/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123"}'
```

---

## 📞 İletişim

**Backend API Dokümantasyonu:**
- `BACKEND_API_DOCUMENTATION.md` - Tüm endpoint'ler
- `FRONTEND_INTEGRATION_GUIDE.md` - Frontend entegrasyon
- `DATABASE_INTEGRATION_GUIDE.md` - Database entegrasyon
- `ML_SERVICE_INTEGRATION_GUIDE.md` - ML Service entegrasyon

