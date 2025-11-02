# GLOWMANCE Backend - Proje Rehberi

## 📁 Proje Yapısı

### ✅ Ana Dosyalar

```
GLOWMANCE/
├── src/                          # Kaynak kod
│   ├── server.js                # Server başlatma dosyası
│   ├── app.js                   # Express uygulaması
│   ├── config/                  # Yapılandırma
│   │   └── config.js           # Environment değişkenleri
│   ├── controllers/             # Route controller'ları
│   ├── routes/                  # API route'ları
│   ├── services/                # İş mantığı servisleri
│   ├── models/                  # Veritabanı modelleri
│   ├── middleware/              # Middleware'ler
│   ├── database/                # Veritabanı bağlantıları
│   ├── utils/                   # Yardımcı fonksiyonlar
│   └── validators/              # Validation'lar
├── .env                          # Environment değişkenleri (oluşturulmalı)
├── package.json                  # NPM bağımlılıkları
├── create-env.ps1               # .env dosyası oluşturma script'i
└── README.md                    # Ana dokümantasyon
```

---

## 🚀 Projeyi Çalıştırma

### Adım 1: .env Dosyasını Oluşturun

**PowerShell'de:**
```powershell
.\create-env.ps1
```

**Veya manuel olarak:**
1. Proje klasöründe `.env` dosyası oluşturun
2. Şu içeriği ekleyin:

```env
PORT=3000
NODE_ENV=development
JWT_SECRET=glowmance-super-secret-jwt-key-change-this-in-production-12345
REFRESH_TOKEN_SECRET=glowmance-super-secret-refresh-token-key-change-this-in-production-67890
```

---

### Adım 2: Bağımlılıkları Yükleyin

```powershell
npm install
```

---

### Adım 3: Server'ı Başlatın

**Development modu (nodemon ile):**
```powershell
npm run dev
```

**Production modu:**
```powershell
npm start
```

---

### Adım 4: Test Edin

**Tarayıcıda:**
```
http://localhost:3000/health
```

**Beklenen Response:**
```json
{
  "status": "ok",
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

---

## 📋 API Endpoint'leri

### Authentication
- `POST /api/auth/register` - Kullanıcı kaydı
- `POST /api/auth/login` - Kullanıcı girişi
- `POST /api/auth/refresh-token` - Token yenileme
- `POST /api/auth/forgot-password` - Şifre sıfırlama isteği
- `POST /api/auth/reset-password` - Şifre sıfırlama
- `POST /api/auth/logout` - Çıkış (Auth gerektirir)

### Upload & Analysis
- `POST /api/upload` - Görsel yükleme ve analiz (Auth gerektirir)
- `GET /api/analysis-history` - Analiz geçmişi listesi (Auth gerektirir)
- `GET /api/analysis-history/:id` - Analiz detayı (Auth gerektirir)

### Products
- `GET /api/products/recommend` - Önerilen ürünler (Auth gerektirir)
- `GET /api/products` - Ürün listesi

### User
- `GET /api/user/profile` - Kullanıcı profili (Auth gerektirir)
- `PUT /api/user/profile` - Profil güncelleme (Auth gerektirir)

### Health Check
- `GET /health` - Server durumu kontrolü

---

## 🔧 Yaygın Komutlar

### Development
```powershell
npm run dev          # Server'ı development modunda başlat (nodemon)
npm start            # Server'ı production modunda başlat
```

### Testing
```powershell
npm test             # Tüm testleri çalıştır
npm run test:smoke   # Smoke test çalıştır
```

### Database
```powershell
npm run setup:db     # Database kurulumu
npm run migrate      # Migration çalıştır
npm run seed         # Seed data ekle
```

### Code Quality
```powershell
npm run lint         # ESLint kontrolü
npm run lint:fix     # ESLint düzeltmeleri
```

---

## 📚 Dokümantasyon Dosyaları

### Gerekli Dokümantasyon
- **`BACKEND_API_DOCUMENTATION.md`** - Tüm API endpoint'lerinin detaylı dokümantasyonu
- **`FRONTEND_INTEGRATION_GUIDE.md`** - Frontend geliştiriciler için entegrasyon rehberi
- **`DATABASE_INTEGRATION_GUIDE.md`** - Database admin'ler için entegrasyon rehberi
- **`ML_SERVICE_INTEGRATION_GUIDE.md`** - ML servis geliştiriciler için entegrasyon rehberi
- **`INTEGRATION_QUICK_START.md`** - Hızlı başlangıç rehberi
- **`API_ENDPOINTS_SUMMARY.md`** - API endpoint'lerinin özeti
- **`DATA_SECURITY_VERIFICATION.md`** - Güvenlik dokümantasyonu
- **`SETUP.md`** - Kurulum rehberi
- **`README.md`** - Ana proje dokümantasyonu

---

## 🛠️ Yapılan Temizlik İşlemleri

### ✅ Kaldırılan Gereksiz Dosyalar (32 dosya)

1. **Backup dosyaları** (.bak)
2. **Test/Verification dosyaları** (9 adet)
3. **Troubleshoot rehberleri** (15 adet)
4. **Gereksiz script'ler** (4 adet)
5. **Kullanılmayan kod dosyaları** (4 adet)

### ✅ Temizlenen Dosyalar

- **`src/app.js`** - Gereksiz `healthRoutes` import ve kullanımı kaldırıldı

---

## 📁 Önemli Klasörler ve Dosyalar

### `src/` - Kaynak Kod

#### `src/server.js`
- Server'ı başlatan ana dosya
- Database bağlantılarını initialize eder
- Port 3000'de server'ı başlatır

#### `src/app.js`
- Express uygulaması yapılandırması
- Middleware'ler (CORS, cookie-parser, body-parser)
- Route tanımlamaları
- Error handling

#### `src/config/config.js`
- Environment değişkenlerini yönetir
- JWT, Database, AWS, Cloudinary ayarları

#### `src/database/`
- `index.js` - Database bağlantılarını initialize eder
- `postgres.js` - PostgreSQL bağlantısı
- `mongodb.js` - MongoDB bağlantısı

#### `src/models/`
- `user.model.js` - PostgreSQL User modeli
- `AnalysisHistory.js` - MongoDB AnalysisHistory modeli
- `aiImage.model.js` - MongoDB AIImage modeli

#### `src/routes/`
- `authRoutes.js` - Authentication route'ları
- `uploadRoutes.js` - Upload route'ları
- `analysisHistoryRoutes.js` - Analiz geçmişi route'ları
- `userRoutes.js` - User route'ları
- `products.routes.js` - Ürün route'ları

#### `src/controllers/`
- `authController.js` - Authentication işlemleri
- `uploadController.js` - Upload ve analiz işlemleri
- `analysisHistoryController.js` - Analiz geçmişi işlemleri
- `userController.js` - User işlemleri

#### `src/services/`
- `UserService.js` - User iş mantığı
- `MLAnalysisService.js` - ML servis entegrasyonu
- `AnalysisHistoryService.js` - Analiz geçmişi iş mantığı
- `FileUploadService.js` - Dosya yükleme işlemleri
- `RecommendationService.js` - Ürün öneri iş mantığı

---

## 🔐 Güvenlik

### Environment Variables (.env)

**Gerekli:**
- `JWT_SECRET` - JWT token imzalama için
- `REFRESH_TOKEN_SECRET` - Refresh token imzalama için

**Opsiyonel:**
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` - PostgreSQL
- `MONGODB_URI` - MongoDB
- `ML_SERVICE_URL` - ML servis URL'i
- `CLOUDINARY_*` - Cloudinary ayarları
- `AWS_*` - AWS S3 ayarları

---

## 🐛 Sorun Giderme

### Server Başlamıyor

1. **.env dosyasını kontrol edin:**
   ```powershell
   if (Test-Path .env) { Write-Host ".env exists" } else { Write-Host ".env missing!" }
   ```

2. **JWT_SECRET kontrol edin:**
   ```powershell
   Get-Content .env | Select-String "JWT_SECRET"
   ```

3. **Port 3000 kullanımda mı kontrol edin:**
   ```powershell
   netstat -ano | findstr :3000
   ```

4. **node_modules kontrol edin:**
   ```powershell
   if (Test-Path node_modules) { Write-Host "OK" } else { npm install }
   ```

### Database Bağlantı Hatası

**Durum:** Server çalışır ama database-dependent endpoint'ler çalışmaz

**Çözüm:**
- Database servislerini başlatın (PostgreSQL ve MongoDB)
- Veya database olmadan devam edin (limited mode)

---

## 📝 Geliştirme Notları

### Yeni Route Ekleme

1. `src/routes/` klasöründe yeni route dosyası oluşturun
2. `src/app.js` içinde route'u import edin ve kullanın:
   ```javascript
   const newRoutes = require("./routes/newRoutes");
   app.use("/api/new", newRoutes);
   ```

### Yeni Controller Ekleme

1. `src/controllers/` klasöründe yeni controller dosyası oluşturun
2. Route dosyasında controller'ı import edin ve kullanın

### Yeni Service Ekleme

1. `src/services/` klasöründe yeni service dosyası oluşturun
2. Controller'larda service'i import edin ve kullanın

---

## ✅ Proje Durumu

- ✅ Server başlatma: `src/server.js` hazır
- ✅ Database bağlantıları: PostgreSQL ve MongoDB desteği var
- ✅ Authentication: JWT tabanlı auth sistemi
- ✅ File Upload: Multer ile görsel yükleme
- ✅ ML Integration: FastAPI servis entegrasyonu
- ✅ Error Handling: Merkezi error handling
- ✅ Validation: express-validator ile input validation

---

## 📞 Yardım

**Detaylı dokümantasyon için:**
- `BACKEND_API_DOCUMENTATION.md` - API detayları
- `SETUP.md` - Kurulum rehberi
- `INTEGRATION_QUICK_START.md` - Hızlı başlangıç

---

## 🎯 Sonraki Adımlar

1. ✅ `.env` dosyasını oluşturun (`.\create-env.ps1`)
2. ✅ `npm install` çalıştırın
3. ✅ `npm run dev` ile server'ı başlatın
4. ✅ `http://localhost:3000/health` ile test edin
5. ✅ API endpoint'lerini test edin

---

**Proje temizlendi ve çalışmaya hazır! 🚀**

