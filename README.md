# GLOWMANCE - AI-Powered Skincare Backend API

GLOWMANCE, AI destekli cilt bakım analizi ve ürün önerisi sunan backend API projesidir.

## 🚀 Hızlı Başlangıç

### 1. .env Dosyasını Oluşturun

```powershell
.\create-env.ps1
```

### 2. Bağımlılıkları Yükleyin

```powershell
npm install
```

### 3. Server'ı Başlatın

```powershell
npm run dev
```

### 4. Test Edin

Tarayıcıda: `http://localhost:3000/health`

## 📋 Ön Koşullar

- Node.js (14+)
- PostgreSQL (opsiyonel)
- MongoDB (opsiyonel)

## 📚 Dokümantasyon

- **`PROJECT_GUIDE.md`** - Proje yapısı ve kullanım rehberi
- **`BACKEND_API_DOCUMENTATION.md`** - Tüm API endpoint'lerinin detaylı dokümantasyonu
- **`SETUP.md`** - Detaylı kurulum rehberi
- **`INTEGRATION_QUICK_START.md`** - Entegrasyon rehberi

## 🔧 Ana Komutlar

```powershell
npm run dev          # Development modunda başlat
npm start            # Production modunda başlat
npm test             # Testleri çalıştır
npm run lint         # Code quality kontrolü
```

## 📁 Proje Yapısı

```
src/
├── server.js         # Server başlatma
├── app.js           # Express uygulaması
├── config/          # Yapılandırma
├── controllers/      # Route controller'ları
├── routes/          # API route'ları
├── services/        # İş mantığı servisleri
├── models/          # Veritabanı modelleri
├── middleware/      # Middleware'ler
├── database/        # Veritabanı bağlantıları
├── utils/           # Yardımcı fonksiyonlar
└── validators/      # Validation'lar
```

## 🔐 Güvenlik

- JWT tabanlı authentication
- Bcrypt ile şifre hashleme
- Input validation
- Error handling

## 📞 Yardım

Detaylı bilgi için `PROJECT_GUIDE.md` dosyasına bakın.
