# GLOWMANCE Backend API - Endpoint Özeti

Backend API'nin tüm endpoint'leri ve kullanım kılavuzu.

## 📍 Base URL

```
Development: http://localhost:3000
Production: https://api.glowmance.com
```

---

## 🔐 Authentication Endpoints (`/api/auth`)

### POST `/api/auth/register`
**Kullanıcı kaydı** - Yeni kullanıcı oluşturur

### POST `/api/auth/login`
**Kullanıcı girişi** - Token alır

### POST `/api/auth/refresh-token`
**Token yenileme** - Yeni access token alır

### POST `/api/auth/logout`
**Çıkış** - Token temizler (✅ Auth gerekli)

### POST `/api/auth/forgot-password`
**Şifre sıfırlama isteği** - Reset token oluşturur

### POST `/api/auth/reset-password`
**Şifre sıfırlama** - Token ile şifre sıfırlar

---

## 📤 Upload Endpoints (`/api/upload`)

### POST `/api/upload`
**Görsel yükleme ve analiz** - Görsel yükler, cloud'a kaydeder, ML analizi yapar (✅ Auth gerekli)

**Request:** `multipart/form-data` (field: `image`)

**Response:** `{ image, analysis }`

---

## 📊 Analysis History Endpoints (`/api/analysis-history`)

### GET `/api/analysis-history`
**Analiz geçmişi listesi** - Pagination ile (✅ Auth gerekli)

**Query Params:** `limit`, `page`, `offset`

### GET `/api/analysis-history/:id`
**Tek analiz** - ID ile analiz getirir (✅ Auth gerekli)

### GET `/api/analysis-history/stats/summary`
**İstatistikler** - Kullanıcı analiz istatistikleri (✅ Auth gerekli)

### POST `/api/analysis-history`
**Yeni analiz** - Manuel analiz kaydı (✅ Auth gerekli)

### DELETE `/api/analysis-history/:id`
**Analiz sil** - Analiz kaydını siler (✅ Auth gerekli)

---

## 👤 User Endpoints (`/api/user`)

### GET `/api/user/profile`
**Profil bilgileri** - Kullanıcı profili (✅ Auth gerekli)

### PUT `/api/user/profile`
**Profil güncelle** - Profil bilgilerini günceller (✅ Auth gerekli)

### PUT `/api/user/password`
**Şifre değiştir** - Kullanıcı şifresini değiştirir (✅ Auth gerekli)

---

## 🛍️ Products Endpoints (`/api/products`)

### GET `/api/products/recommendations`
**Kişiselleştirilmiş öneriler** - Kullanıcı geçmişine göre ürün önerileri (✅ Auth gerekli)

**Query Params:** `limit`, `includeTrending`

### GET `/api/products/trending`
**Trend ürünler** - Popüler ürünler listesi

**Query Params:** `limit`

### POST `/api/products/scrape`
**Ürün tarama** - Belirli kategori için ürün tarar (test için) (✅ Auth gerekli)

---

## 🏥 Health Check (`/health`)

### GET `/health`
**Health check** - API'nin çalışıp çalışmadığını kontrol eder

---

## ✅ Authentication

**Format:**
```
Authorization: Bearer <access-token>
```

**Token Alımı:**
1. `POST /api/auth/login` veya `POST /api/auth/register`
2. Response'dan `accessToken` al
3. Her request'te `Authorization` header'ına ekle

**Token Süreleri:**
- Access Token: 1 saat
- Refresh Token: 7 gün

---

## 📋 Detaylı Dokümantasyon

- `BACKEND_API_DOCUMENTATION.md` - Tüm endpoint detayları
- `FRONTEND_INTEGRATION_GUIDE.md` - Frontend entegrasyon rehberi
- `DATABASE_INTEGRATION_GUIDE.md` - Database entegrasyon rehberi
- `ML_SERVICE_INTEGRATION_GUIDE.md` - ML Service entegrasyon rehberi

