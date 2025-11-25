# .env Dosyası Oluşturma Script'i
# Kullanım: .\create-env.ps1

Write-Host ""
Write-Host "Creating .env file..." -ForegroundColor Cyan
Write-Host ""

$envContent = @"
# Server Configuration
PORT=3000
NODE_ENV=development

# PostgreSQL Database Configuration (Optional - server can start without it)
DB_HOST=localhost
DB_PORT=5432
DB_NAME=glowmance
DB_USER=postgres
DB_PASSWORD=1234

# MongoDB Configuration (Optional - server can start without it)
MONGODB_URI=mongodb://localhost:27017/glowmance

# JWT Configuration - REQUIRED for authentication endpoints
JWT_SECRET=glowmance-super-secret-jwt-key-change-this-in-production-12345
REFRESH_TOKEN_SECRET=glowmance-super-secret-refresh-token-key-change-this-in-production-67890

# JWT Expiration (Optional - defaults provided)
JWT_EXPIRES_IN=1h
REFRESH_TOKEN_EXPIRES_IN=7d

# CORS Configuration (Optional)
CORS_ORIGIN=*

# ML Service Configuration (Optional - for AI analysis)
ML_SERVICE_URL=http://localhost:8000

# Cloud Storage Configuration (Optional - for image uploads)
# Cloudinary (recommended)
# CLOUDINARY_CLOUD_NAME=your-cloud-name
# CLOUDINARY_API_KEY=your-api-key
# CLOUDINARY_API_SECRET=your-api-secret

# AWS S3 (alternative)
# AWS_ACCESS_KEY_ID=your-access-key
# AWS_SECRET_ACCESS_KEY=your-secret-key
# AWS_BUCKET_NAME=your-bucket-name
# AWS_REGION=eu-central-1
"@

try {
    $envContent | Out-File -FilePath .env -Encoding utf8 -Force
    Write-Host "[OK] .env file created successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "IMPORTANT: Update JWT_SECRET and REFRESH_TOKEN_SECRET in production!" -ForegroundColor Yellow
    Write-Host ""
} catch {
    Write-Host "[FAIL] Error creating .env file: $($_.Exception.Message)" -ForegroundColor Red
}

