# GLOWMANCE Backend API Dokümantasyonu

Backend API'nin tüm endpoint'leri, request/response formatları ve entegrasyon rehberi.

## 🌐 Base URL

```
Development: http://localhost:3000
Production: https://api.glowmance.com
```

## 📋 API Endpoint'leri

### 🔐 Authentication Endpoints

**Base URL:** `/api/auth`

#### 1. POST `/api/auth/register` - Kullanıcı Kaydı

**Description:** Yeni kullanıcı kaydı oluşturur

**Authentication:** Gerekli değil

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "Test123",
  "name": "Test User"
}
```

**Response (201):**
```json
{
  "status": "success",
  "data": {
    "user": {
      "id": "uuid-here",
      "email": "user@example.com",
      "name": "Test User"
    },
    "accessToken": "jwt-access-token",
    "refreshToken": "jwt-refresh-token"
  }
}
```

**Validation:**
- Email: Valid email format
- Password: Min 6 karakter, en az 1 rakam, 1 küçük harf, 1 büyük harf
- Name: Min 2 karakter

---

#### 2. POST `/api/auth/login` - Kullanıcı Girişi

**Description:** Kullanıcı girişi ve token oluşturma

**Authentication:** Gerekli değil

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "Test123"
}
```

**Response (200):**
```json
{
  "status": "success",
  "data": {
    "user": {
      "id": "uuid-here",
      "email": "user@example.com",
      "name": "Test User"
    },
    "accessToken": "jwt-access-token",
    "refreshToken": "jwt-refresh-token"
  }
}
```

---

#### 3. POST `/api/auth/refresh-token` - Token Yenileme

**Description:** Refresh token ile yeni access token al

**Authentication:** Refresh token gerekli

**Request Body:**
```json
{
  "refreshToken": "jwt-refresh-token"
}
```

**Response (200):**
```json
{
  "status": "success",
  "data": {
    "accessToken": "new-jwt-access-token",
    "refreshToken": "new-jwt-refresh-token"
  }
}
```

---

#### 4. POST `/api/auth/logout` - Çıkış

**Description:** Kullanıcı çıkışı ve token temizleme

**Authentication:** ✅ Bearer Token gerekli

**Response (200):**
```json
{
  "status": "success",
  "message": "Successfully logged out"
}
```

---

#### 5. POST `/api/auth/forgot-password` - Şifre Sıfırlama İsteği

**Description:** Şifre sıfırlama token'ı oluşturur

**Authentication:** Gerekli değil

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response (200):**
```json
{
  "status": "success",
  "message": "If your email is registered, you will receive a password reset link",
  "resetToken": "reset-token-here"
}
```

---

#### 6. POST `/api/auth/reset-password` - Şifre Sıfırlama

**Description:** Token ile şifre sıfırlama

**Authentication:** Gerekli değil

**Request Body:**
```json
{
  "token": "reset-token",
  "password": "NewTest123"
}
```

**Response (200):**
```json
{
  "status": "success",
  "message": "Password successfully reset"
}
```

---

### 📤 Upload Endpoints

**Base URL:** `/api/upload`

#### 1. POST `/api/upload` - Görsel Yükleme ve Analiz

**Description:** Görsel yükleme, cloud storage'a kaydetme ve ML analizi

**Authentication:** ✅ Bearer Token gerekli

**Content-Type:** `multipart/form-data`

**Request:**
```
POST /api/upload
Authorization: Bearer <access-token>
Content-Type: multipart/form-data

image: <file>
```

**Response (201):**
```json
{
  "status": "success",
  "data": {
    "image": {
      "_id": "mongodb-image-id",
      "userId": "user-uuid",
      "originalName": "test-image.jpg",
      "mimeType": "image/jpeg",
      "size": 245678,
      "url": "https://res.cloudinary.com/.../image.jpg",
      "cloudProvider": "cloudinary",
      "publicId": "glowmance/skin-images/...",
      "cloudUrl": "https://res.cloudinary.com/.../image.jpg",
      "width": 1920,
      "height": 1080,
      "format": "jpg",
      "timestamp": "2024-01-01T00:00:00.000Z"
    },
    "analysis": {
      "id": "analysis-uuid",
      "userId": "user-uuid",
      "imageId": "mongodb-image-id",
      "results": {
        "confidence": 0.92,
        "conditions": ["Akne", "Kuruluk"],
        "recommendations": ["Nemlendirici", "Akne Kremi"],
        "metadata": {
          "processed_at": "2024-01-01T00:00:00.000Z"
        }
      },
      "timestamp": "2024-01-01T00:00:00.000Z"
    }
  }
}
```

**File Requirements:**
- Max size: 5MB
- Allowed types: JPEG, PNG, WebP, GIF
- Field name: `image`

---

### 📊 Analysis History Endpoints

**Base URL:** `/api/analysis-history`

#### 1. GET `/api/analysis-history` - Analiz Geçmişi Listeleme

**Description:** Kullanıcının analiz geçmişini listeler (pagination)

**Authentication:** ✅ Bearer Token gerekli

**Query Parameters:**
- `limit` (optional, default: 20, max: 100)
- `page` (optional, default: 1)
- `offset` (optional, alternative to page)

**Request:**
```
GET /api/analysis-history?limit=20&page=1
Authorization: Bearer <access-token>
```

**Response (200):**
```json
{
  "status": "success",
  "data": {
    "analyses": [
      {
        "id": "analysis-uuid",
        "userId": "user-uuid",
        "imageId": "mongodb-image-id",
        "results": {
          "confidence": 0.92,
          "conditions": ["Akne", "Kuruluk"],
          "recommendations": ["Nemlendirici", "Akne Kremi"],
          "metadata": {}
        },
        "timestamp": "2024-01-01T00:00:00.000Z",
        "image": {
          "id": "mongodb-image-id",
          "url": "https://res.cloudinary.com/.../image.jpg",
          "cloudProvider": "cloudinary",
          "originalName": "test-image.jpg"
        }
      }
    ],
    "pagination": {
      "total": 45,
      "limit": 20,
      "offset": 0,
      "currentPage": 1,
      "totalPages": 3,
      "hasNextPage": true,
      "hasPreviousPage": false
    }
  }
}
```

---

#### 2. GET `/api/analysis-history/:id` - Tek Analiz Getir

**Description:** Belirli bir analizi ID ile getirir

**Authentication:** ✅ Bearer Token gerekli

**Response (200):**
```json
{
  "status": "success",
  "data": {
    "analysis": {
      "id": "analysis-uuid",
      "userId": "user-uuid",
      "imageId": "mongodb-image-id",
      "results": {
        "confidence": 0.92,
        "conditions": ["Akne"],
        "recommendations": ["Akne Kremi"],
        "metadata": {}
      },
      "image": {
        "id": "mongodb-image-id",
        "url": "https://res.cloudinary.com/.../image.jpg"
      }
    }
  }
}
```

---

#### 3. GET `/api/analysis-history/stats/summary` - İstatistikler

**Description:** Kullanıcının analiz istatistiklerini getirir

**Authentication:** ✅ Bearer Token gerekli

**Response (200):**
```json
{
  "status": "success",
  "data": {
    "statistics": {
      "totalAnalyses": 45,
      "averageConfidence": 0.87,
      "topConditions": [
        { "condition": "Akne", "count": 25 },
        { "condition": "Kuruluk", "count": 18 }
      ],
      "topRecommendations": [
        { "recommendation": "Nemlendirici", "count": 30 },
        { "recommendation": "Akne Kremi", "count": 22 }
      ]
    }
  }
}
```

---

#### 4. POST `/api/analysis-history` - Yeni Analiz Oluştur

**Description:** Manuel olarak analiz kaydı oluşturur

**Authentication:** ✅ Bearer Token gerekli

**Request Body:**
```json
{
  "imageId": "mongodb-image-id",
  "results": {
    "confidence": 0.92,
    "conditions": ["Akne"],
    "recommendations": ["Akne Kremi"],
    "metadata": {}
  }
}
```

---

#### 5. DELETE `/api/analysis-history/:id` - Analiz Sil

**Description:** Belirli bir analizi siler

**Authentication:** ✅ Bearer Token gerekli

**Response (200):**
```json
{
  "status": "success",
  "message": "Analysis deleted successfully"
}
```

---

### 👤 User Endpoints

**Base URL:** `/api/user`

#### 1. GET `/api/user/profile` - Kullanıcı Profili

**Description:** Kullanıcının profil bilgilerini getirir

**Authentication:** ✅ Bearer Token gerekli

**Response (200):**
```json
{
  "status": "success",
  "data": {
    "user": {
      "id": "user-uuid",
      "email": "user@example.com",
      "name": "Test User"
    }
  }
}
```

---

#### 2. PUT `/api/user/profile` - Profil Güncelle

**Description:** Kullanıcı profil bilgilerini günceller

**Authentication:** ✅ Bearer Token gerekli

**Request Body:**
```json
{
  "name": "Updated Name",
  "email": "newemail@example.com"
}
```

**Response (200):**
```json
{
  "status": "success",
  "data": {
    "user": {
      "id": "user-uuid",
      "email": "newemail@example.com",
      "name": "Updated Name"
    }
  }
}
```

---

#### 3. PUT `/api/user/password` - Şifre Değiştir

**Description:** Kullanıcı şifresini değiştirir

**Authentication:** ✅ Bearer Token gerekli

**Request Body:**
```json
{
  "currentPassword": "OldTest123",
  "newPassword": "NewTest123"
}
```

**Response (200):**
```json
{
  "status": "success",
  "message": "Password updated successfully"
}
```

---

### 🛍️ Products Endpoints

**Base URL:** `/api/products`

#### 1. GET `/api/products/recommendations` - Kişiselleştirilmiş Öneriler

**Description:** Kullanıcının analiz geçmişine göre ürün önerileri

**Authentication:** ✅ Bearer Token gerekli

**Query Parameters:**
- `limit` (optional, default: 10, max: 50)
- `includeTrending` (optional, default: true)

**Request:**
```
GET /api/products/recommendations?limit=10&includeTrending=true
Authorization: Bearer <access-token>
```

**Response (200):**
```json
{
  "status": "success",
  "data": {
    "recommendations": [
      {
        "id": "product-1",
        "name": "Nemlendirici - Önerilen Ürün 1",
        "category": "Nemlendirici",
        "price": 299,
        "rating": 4.5,
        "imageUrl": "https://example.com/products/1.jpg",
        "tags": ["Kuruluk", "Nemlendirici"],
        "isRecommended": true
      }
    ],
    "source": "personalized",
    "message": "Personalized recommendations based on your analysis history",
    "basedOnHistory": true,
    "insights": {
      "conditions": ["Kuruluk", "Akne"],
      "recommendations": ["Nemlendirici", "Akne Kremi"],
      "totalAnalyses": 5
    }
  }
}
```

---

#### 2. GET `/api/products/trending` - Trend Ürünler

**Description:** Popüler/trend ürünleri listeler

**Authentication:** Gerekli değil

**Query Parameters:**
- `limit` (optional, default: 10, max: 50)

**Response (200):**
```json
{
  "status": "success",
  "data": [
    {
      "id": "trend-1",
      "name": "Nemlendirici Serum - Hyaluronik Asit",
      "category": "Nemlendirici",
      "price": 299,
      "rating": 4.5,
      "imageUrl": "https://example.com/products/trend-1.jpg",
      "tags": ["Nemlendirici", "Hyaluronik Asit"],
      "isTrending": true
    }
  ]
}
```

---

#### 3. POST `/api/products/scrape` - Ürün Tarama

**Description:** Belirli kategori ve condition için ürün tarar (test için)

**Authentication:** ✅ Bearer Token gerekli

**Request Body:**
```json
{
  "category": "Nemlendirici",
  "condition": "Kuruluk"
}
```

---

### 🏥 Health Check Endpoint

**Base URL:** `/health`

#### GET `/health` - Sağlık Kontrolü

**Description:** API'nin çalışıp çalışmadığını kontrol eder

**Authentication:** Gerekli değil

**Response (200):**
```json
{
  "status": "ok",
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

---

## 🔑 Authentication

### JWT Token Kullanımı

**Token Format:**
```
Authorization: Bearer <access-token>
```

**Token Alımı:**
1. `POST /api/auth/login` veya `POST /api/auth/register` ile token alın
2. Token'ı response'dan `accessToken` alanından alın
3. Her request'te `Authorization` header'ına ekleyin

**Token Yenileme:**
- Access token süresi dolduğunda `POST /api/auth/refresh-token` endpoint'ini kullanın
- Refresh token ile yeni access token alın

**Token Süreleri:**
- Access Token: 1 saat (default)
- Refresh Token: 7 gün (default)

---

## 📝 Response Format

### Başarılı Response

```json
{
  "status": "success",
  "data": {
    // Response data
  }
}
```

### Hata Response

```json
{
  "status": "error",
  "message": "Error message here"
}
```

### Validation Error Response

```json
{
  "status": "fail",
  "message": "Validation error message"
}
```

---

## 🔒 Güvenlik

### CORS

- Backend CORS ayarlanabilir (`.env` dosyasında `CORS_ORIGIN`)
- Default: `*` (tüm origin'lere açık - development için)

### HTTPS

- Production'da HTTPS kullanılmalı
- SSL sertifikası yapılandırılmalı

### Rate Limiting

- Production'da rate limiting eklenebilir
- Şu an yok (isteğe bağlı)

---

## 🐛 Error Codes

| Status Code | Açıklama |
|-------------|----------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request (Validation error) |
| 401 | Unauthorized (Authentication required) |
| 403 | Forbidden (Access denied) |
| 404 | Not Found |
| 500 | Internal Server Error |

---

## 📚 Tüm Endpoint Özeti

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | ❌ | Kullanıcı kaydı |
| POST | `/api/auth/login` | ❌ | Kullanıcı girişi |
| POST | `/api/auth/refresh-token` | ❌ | Token yenileme |
| POST | `/api/auth/logout` | ✅ | Çıkış |
| POST | `/api/auth/forgot-password` | ❌ | Şifre sıfırlama isteği |
| POST | `/api/auth/reset-password` | ❌ | Şifre sıfırlama |
| POST | `/api/upload` | ✅ | Görsel yükleme ve analiz |
| GET | `/api/analysis-history` | ✅ | Analiz geçmişi listesi |
| GET | `/api/analysis-history/:id` | ✅ | Tek analiz |
| GET | `/api/analysis-history/stats/summary` | ✅ | İstatistikler |
| POST | `/api/analysis-history` | ✅ | Yeni analiz |
| DELETE | `/api/analysis-history/:id` | ✅ | Analiz sil |
| GET | `/api/user/profile` | ✅ | Kullanıcı profili |
| PUT | `/api/user/profile` | ✅ | Profil güncelle |
| PUT | `/api/user/password` | ✅ | Şifre değiştir |
| GET | `/api/products/recommendations` | ✅ | Kişiselleştirilmiş öneriler |
| GET | `/api/products/trending` | ❌ | Trend ürünler |
| POST | `/api/products/scrape` | ✅ | Ürün tarama |
| GET | `/health` | ❌ | Health check |

---

## 🧪 Test

### Postman Collection

Postman collection dosyası: `tests/Glowmance.postman_collection.json`

### cURL Örnekleri

Detaylı test örnekleri için:
- `API_AUTH_TEST.md` - Auth endpoint testleri
- `UPLOAD_FLOW_TEST.md` - Upload endpoint testleri
- `ANALYSIS_HISTORY_LIST_API.md` - Analysis history testleri
- `RECOMMENDATION_API_VERIFICATION.md` - Recommendations testleri

