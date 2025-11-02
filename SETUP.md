# GLOWMANCE Backend - Kurulum Rehberi

Bu rehber, GLOWMANCE backend projesini çalıştırmak için gerekli tüm adımları içerir.

## 📋 Ön Koşullar

Aşağıdaki yazılımların sisteminizde yüklü olması gerekir:

1. **Node.js** (v14 veya üzeri) - [İndir](https://nodejs.org/)
2. **PostgreSQL** (v12 veya üzeri) - [İndir](https://www.postgresql.org/download/)
3. **MongoDB** (v4.4 veya üzeri) - [İndir](https://www.mongodb.com/try/download/community)
4. **Git** (opsiyonel) - [İndir](https://git-scm.com/)

### Alternatif: Docker ile Kurulum

Eğer Docker kullanmak isterseniz, tüm servisler `docker-compose.yml` dosyası ile otomatik kurulur:

```bash
docker-compose up -d
```

## 🚀 Kurulum Adımları

### 1. Proje Dizinini Açın

```powershell
cd "C:\Users\KUMRU ÇELİK\Desktop\GLOWMANCE"
```

### 2. Bağımlılıkları Yükleyin

```powershell
npm install
```

### 3. Environment Dosyasını Oluşturun

`.env.example` dosyasını `.env` olarak kopyalayın ve içeriğini düzenleyin:

```powershell
copy .env.example .env
```

Ardından `.env` dosyasını açın ve şu değerleri güncelleyin:

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=glowmance
DB_USER=postgres
DB_PASSWORD=1234  # PostgreSQL şifrenizi buraya yazın

MONGODB_URI=mongodb://localhost:27017/glowmance

# JWT Configuration - PRODÜKSİYON İÇİN MUTLAKA DEĞİŞTİRİN!
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
REFRESH_TOKEN_SECRET=your-super-secret-refresh-token-key-change-this-in-production
```

### 4. PostgreSQL Veritabanını Oluşturun

PostgreSQL'e bağlanın ve veritabanını oluşturun:

```sql
-- PostgreSQL psql komut satırında:
CREATE DATABASE glowmance;
```

Veya PostgreSQL yüklü değilse, Docker kullanabilirsiniz:

```powershell
docker run --name glowmance-postgres -e POSTGRES_PASSWORD=1234 -e POSTGRES_DB=glowmance -p 5432:5432 -d postgres:13-alpine
```

### 5. MongoDB'yi Başlatın

MongoDB'nin çalıştığından emin olun:

```powershell
# MongoDB servisini başlat (Windows)
net start MongoDB

# Veya Docker kullanarak:
docker run --name glowmance-mongo -p 27017:27017 -d mongo:4.4
```

### 6. Veritabanı Migration'larını Çalıştırın

```powershell
npm run migrate
```

### 7. (Opsiyonel) Seed Data Ekleme

Örnek veriler eklemek için:

```powershell
npm run seed
```

### 8. Server'ı Başlatın

#### Development Modu (Otomatik yeniden başlatma ile):

```powershell
npm run dev
```

#### Production Modu:

```powershell
npm start
```

## ✅ Kurulum Kontrolü

Server başarıyla çalışıyorsa, aşağıdaki çıktıyı görmelisiniz:

```
PostgreSQL bağlantısı başarılı.
MongoDB bağlantısı başarılı.
Tüm veritabanı bağlantıları başarıyla kuruldu
Server is running on port 3000
Environment: development
```

### Health Check

Tarayıcınızda veya Postman'de şu URL'yi test edin:

```
GET http://localhost:3000/health
```

Başarılı yanıt:

```json
{
  "status": "ok",
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

## 🐛 Sorun Giderme

### PostgreSQL Bağlantı Hatası

**Hata:** `PostgreSQL bağlantı hatası`

**Çözüm:**
1. PostgreSQL servisinin çalıştığından emin olun
2. `.env` dosyasındaki `DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASSWORD` değerlerini kontrol edin
3. PostgreSQL'de `glowmance` veritabanının oluşturulduğunu kontrol edin

### MongoDB Bağlantı Hatası

**Hata:** `MongoDB bağlantı hatası`

**Çözüm:**
1. MongoDB servisinin çalıştığından emin olun
2. `.env` dosyasındaki `MONGODB_URI` değerini kontrol edin
3. MongoDB portunun (27017) açık olduğunu kontrol edin

### Port Zaten Kullanımda

**Hata:** `EADDRINUSE: address already in use :::3000`

**Çözüm:**
1. 3000 portunu kullanan process'i bulun ve kapatın
2. Veya `.env` dosyasında `PORT` değerini değiştirin

## 📝 Notlar

- Development modunda `nodemon` otomatik olarak değişiklikleri algılar ve server'ı yeniden başlatır
- Production modunda environment variable'ların doğru ayarlandığından emin olun
- JWT secret'larını production'da mutlaka güçlü, rastgele değerlerle değiştirin

## 🎯 Sonraki Adımlar

1. API endpoint'lerini test edin (Postman collection kullanarak)
2. Frontend uygulamasını backend'e bağlayın
3. Production deployment için environment variable'ları ayarlayın

## 📚 Ek Kaynaklar

- [PostgreSQL Dokümantasyonu](https://www.postgresql.org/docs/)
- [MongoDB Dokümantasyonu](https://docs.mongodb.com/)
- [Express.js Dokümantasyonu](https://expressjs.com/)
- [Sequelize Dokümantasyonu](https://sequelize.org/)

