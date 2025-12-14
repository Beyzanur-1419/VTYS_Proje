# GLOWMANCE ML Service

FastAPI-based ML service for skin analysis. This service receives images from the backend Node.js application and returns AI-powered analysis results.

## 🚀 Quick Start

### 1. Install Dependencies

```bash
cd ml_service
pip install -r requirements.txt
```

### 2. Run the Service

```bash
python main.py
```

Or with uvicorn:

```bash
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

### 3. Test the Service

```bash
# Health check
curl http://localhost:8000/health

# Analyze image (using curl)
curl -X POST "http://localhost:8000/analyze" \
  -H "Content-Type: multipart/form-data" \
  -F "image=@path/to/image.jpg"
```

## 📡 API Endpoints

### GET `/`
Health check endpoint

**Response:**
```json
{
  "status": "ok",
  "service": "GLOWMANCE ML Service",
  "version": "1.0.0",
  "timestamp": "2024-01-01T00:00:00"
}
```

### GET `/health`
Health check endpoint

### POST `/analyze`
Analyze skin image

**Request:**
- Content-Type: `multipart/form-data`
- Field: `image` (file)

**Response:**
```json
{
  "status": "success",
  "data": {
    "confidence": 0.92,
    "conditions": ["Akne", "Kuruluk"],
    "recommendations": ["Nemlendirici", "Akne Kremi"],
    "metadata": {
      "image_format": "JPEG",
      "image_size": [1920, 1080],
      "file_size": 245678,
      "processed_at": "2024-01-01T00:00:00"
    }
  }
}
```

### POST `/analyze/base64`
Analyze skin image from base64 encoded string

**Request:**
```json
{
  "image": "base64_encoded_image_string"
}
```

**Response:** Same as `/analyze`

## 🔧 Configuration

Copy `.env.example` to `.env` and configure:

```env
ML_SERVICE_HOST=0.0.0.0
ML_SERVICE_PORT=8000
MODEL_PATH=./models/skin_analysis_model.h5
LOG_LEVEL=INFO
```

## 📝 Notes

- Currently returns mock analysis results
- To integrate actual ML model, replace the mock logic in `/analyze` endpoint
- Image validation: max 5MB, allowed types: JPEG, PNG, WebP
- Service runs on port 8000 by default

## 🐳 Docker Support

```bash
# Build image
docker build -t glowmance-ml-service .

# Run container
docker run -p 8000:8000 glowmance-ml-service
```

## 📚 Documentation

Once the service is running, visit:
- API Docs: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

