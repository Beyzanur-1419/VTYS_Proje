# ML Service Entegrasyon Rehberi

ML Service geliştiricileri için backend API ile entegrasyon rehberi.

## 🤖 ML Service Yapısı

### FastAPI Service

**Konum:** `ml_service/main.py`

**Port:** `8000` (default)

**Base URL:** `http://localhost:8000`

---

## 📡 ML Service API Endpoints

### 1. GET `/health` - Health Check

**Description:** ML Service'in çalışıp çalışmadığını kontrol eder

**Request:**
```bash
curl http://localhost:8000/health
```

**Response:**
```json
{
  "status": "healthy",
  "service": "Glowmance ML Service",
  "version": "1.0.0"
}
```

---

### 2. POST `/analyze` - Görsel Analizi

**Description:** Görsel alır ve analiz sonuçları döner

**Content-Type:** `multipart/form-data`

**Request:**
```bash
curl -X POST "http://localhost:8000/analyze" \
  -H "Content-Type: multipart/form-data" \
  -F "image=@path/to/image.jpg"
```

**Response:**
```json
{
  "status": "success",
  "data": {
    "confidence": 0.92,
    "conditions": ["Akne", "Kuruluk"],
    "recommendations": ["Nemlendirici", "Akne Kremi"],
    "metadata": {
      "original_filename": "image.jpg",
      "content_type": "image/jpeg",
      "file_size_bytes": 245678,
      "processed_at": "2024-01-01T00:00:00.000Z"
    }
  }
}
```

**Response Format (Backend Beklentisi):**

```python
# ml_service/main.py
class AnalysisResult(BaseModel):
    confidence: float  # 0.0 - 1.0
    conditions: List[str]  # Örnek: ["Akne", "Kuruluk"]
    recommendations: List[str]  # Örnek: ["Nemlendirici", "Akne Kremi"]
    metadata: dict = {}  # Optional metadata
```

---

### 3. POST `/analyze/base64` - Base64 Görsel Analizi

**Description:** Base64 encoded görsel alır ve analiz sonuçları döner

**Content-Type:** `application/json`

**Request:**
```json
{
  "image": "base64_encoded_image_string"
}
```

**Response:** Aynı format `/analyze` gibi

---

## 🔌 Backend Entegrasyonu

### MLAnalysisService

**Konum:** `src/services/MLAnalysisService.js`

**Özellikler:**
- `analyzeImage(imagePath)` - Dosya yolu ile analiz
- `analyzeImageBuffer(buffer, filename, mimetype)` - Buffer ile analiz
- `analyzeImageBase64(base64Image)` - Base64 ile analiz
- `checkServiceHealth()` - Health check

**Kullanım:**

```javascript
const MLAnalysisService = require('./services/MLAnalysisService');

// Buffer ile gönder (önerilen)
const results = await MLAnalysisService.analyzeImageBuffer(
  imageBuffer,
  'image.jpg',
  'image/jpeg'
);

// Veya dosya yolu ile
const results = await MLAnalysisService.analyzeImage('/path/to/image.jpg');
```

---

### Upload Controller Entegrasyonu

**Konum:** `src/controllers/uploadController.js`

**Akış:**
```javascript
// 1. Görsel yüklendi
// 2. MongoDB'ye metadata kaydedildi
// 3. ML Service'e gönderildi
const modelResults = await this.mlAnalysisService.analyzeImageBuffer(
  imageBuffer,
  req.file.originalname,
  req.file.mimetype
);
// 4. Sonuçlar PostgreSQL'e kaydedildi
```

---

## 🔧 ML Service Yapılandırması

### Environment Variables

**Backend (.env):**
```env
ML_SERVICE_URL=http://localhost:8000
# Production: ML_SERVICE_URL=https://ml-api.glowmance.com
```

**ML Service (.env):**
```env
ML_SERVICE_HOST=0.0.0.0
ML_SERVICE_PORT=8000
LOG_LEVEL=INFO
```

---

## 📊 Response Format Requirements

### Backend Beklentisi

ML Service'in döndürmesi gereken format:

```json
{
  "status": "success",
  "data": {
    "confidence": 0.92,  // REQUIRED: 0.0 - 1.0
    "conditions": ["Akne", "Kuruluk"],  // REQUIRED: Array<string>
    "recommendations": ["Nemlendirici", "Akne Kremi"],  // REQUIRED: Array<string>
    "metadata": {  // OPTIONAL
      "original_filename": "image.jpg",
      "content_type": "image/jpeg",
      "file_size_bytes": 245678,
      "processed_at": "2024-01-01T00:00:00.000Z",
      "model_version": "1.0",
      "processing_time_ms": 1234
    }
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

---

## 🔄 ML Service Çağrı Akışı

### Tam Akış

```
1. Frontend → POST /api/upload (image file)
   ↓
2. Backend → Multer (file upload)
   ↓
3. Backend → Cloud Storage (Cloudinary/S3/Local)
   ↓
4. Backend → MongoDB (image metadata)
   ↓
5. Backend → ML Service (POST /analyze)
   ↓
6. ML Service → Görsel analizi
   ↓
7. ML Service → Analysis results döner
   {
     confidence: 0.92,
     conditions: ["Akne"],
     recommendations: ["Akne Kremi"],
     metadata: {}
   }
   ↓
8. Backend → Results validate edilir
   ↓
9. Backend → PostgreSQL (AnalysisHistory tablosu)
   ↓
10. Backend → Frontend (image + analysis response)
```

---

## 🛠️ ML Service Geliştirme

### Mevcut Yapı

**ml_service/main.py:**
- FastAPI application
- Mock analysis results (şu an)
- Image validation
- Error handling

### Gerçek Model Entegrasyonu

**Model Eklemek İçin:**

```python
# ml_service/main.py
from your_model import SkinAnalysisModel

model = SkinAnalysisModel.load('path/to/model.h5')

@app.post("/analyze", response_model=AnalysisResult)
async def analyze_image(file: UploadFile = File(...)):
    # 1. Image okuma
    contents = await file.read()
    
    # 2. Image preprocessing
    image = preprocess_image(contents)
    
    # 3. Model prediction
    predictions = model.predict(image)
    
    # 4. Results formatting
    results = format_predictions(predictions)
    
    return AnalysisResult(
        confidence=results['confidence'],
        conditions=results['conditions'],
        recommendations=results['recommendations'],
        metadata={
            "processed_at": datetime.now().isoformat(),
            "model_version": "1.0"
        }
    )
```

---

## 🧪 ML Service Test

### Health Check

```bash
curl http://localhost:8000/health
```

### Image Analysis Test

```bash
curl -X POST "http://localhost:8000/analyze" \
  -H "Content-Type: multipart/form-data" \
  -F "image=@test-image.jpg"
```

### Python Test

```python
import requests

url = "http://localhost:8000/analyze"

with open("test-image.jpg", "rb") as f:
    files = {"image": f}
    response = requests.post(url, files=files)
    print(response.json())
```

---

## 🔧 Backend ML Service Configuration

### MLAnalysisService Yapılandırması

**Konum:** `src/services/MLAnalysisService.js`

**Constructor:**
```javascript
constructor() {
  this.mlServiceUrl = process.env.ML_SERVICE_URL || 'http://localhost:8000';
  this.timeout = 30000; // 30 seconds
}
```

**Error Handling:**
- ML Service kapalıysa: Fallback mock data döner
- Connection error: `503 Service Unavailable`
- Invalid response: `500 Internal Server Error`

---

## 📋 ML Service Checklist

ML Service geliştirme için:

- [ ] FastAPI service çalışıyor
- [ ] `/health` endpoint çalışıyor
- [ ] `/analyze` endpoint çalışıyor
- [ ] Response format backend beklentisi ile uyumlu
- [ ] Image validation yapılıyor
- [ ] Error handling var
- [ ] Timeout handling var
- [ ] Logging yapılıyor

---

## 🔄 Fallback Mekanizması

**Backend Fallback:**

Eğer ML Service kapalıysa veya hata verirse:

```javascript
// Backend fallback
const modelResults = {
  confidence: 0.85,
  conditions: ['Kuruluk'],
  recommendations: ['Nemlendirici'],
  metadata: {
    processed_at: new Date().toISOString(),
    fallback: true,
    error: mlError.message
  }
};
```

**Metadata Flag:**
- `fallback: true` - ML Service kullanılmadı
- `fallback: false` - ML Service başarıyla kullanıldı

---

## 📚 Detaylı Dokümantasyon

- `ML_SERVICE_TEST.md` - ML Service test rehberi
- `TEST_ML_INTEGRATION.md` - Entegrasyon test rehberi
- `ml_service/README.md` - ML Service dokümantasyonu

---

## 🚀 Hızlı Başlangıç

### ML Service Başlatma

```bash
cd ml_service
pip install -r requirements.txt
python main.py
```

### Backend'den Test

```bash
# Backend'den ML Service'e istek
curl -X POST "http://localhost:8000/analyze" \
  -H "Content-Type: multipart/form-data" \
  -F "image=@test-image.jpg"
```

---

## 📝 Response Format Örnekleri

### Başarılı Response

```json
{
  "status": "success",
  "data": {
    "confidence": 0.92,
    "conditions": ["Akne", "Kuruluk"],
    "recommendations": ["Nemlendirici", "Akne Kremi"],
    "metadata": {
      "processed_at": "2024-01-01T00:00:00.000Z",
      "model_version": "1.0"
    }
  }
}
```

### Hata Response

```json
{
  "status": "error",
  "detail": "Error message here"
}
```

---

## ✅ ML Service Entegrasyon Checklist

- [ ] ML Service FastAPI ile çalışıyor
- [ ] `/health` endpoint çalışıyor
- [ ] `/analyze` endpoint çalışıyor
- [ ] Response format backend ile uyumlu
- [ ] Image validation var
- [ ] Error handling var
- [ ] Backend'den test edildi

