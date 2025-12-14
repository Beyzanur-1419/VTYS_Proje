# Backend-Frontend Bağlantı Rehberi

## ✅ Tamamlanan İşlemler

Backend ve frontend başarıyla bağlandı! Aşağıdaki özellikler eklendi:

### 1. Network Yapılandırması
- ✅ AndroidManifest.xml'e internet izinleri eklendi
- ✅ Retrofit ve OkHttp kütüphaneleri eklendi
- ✅ API Client ve Service katmanı oluşturuldu

### 2. API Entegrasyonu
- ✅ Authentication API'leri entegre edildi:
  - Register (Kayıt)
  - Login (Giriş)
  - Forgot Password (Şifre Sıfırlama)
  - Logout (Çıkış)

### 3. State Management
- ✅ ViewModel'ler oluşturuldu
- ✅ Repository pattern uygulandı
- ✅ Token yönetimi (SharedPreferences ile)

### 4. UI Entegrasyonu
- ✅ SignInScreen - ViewModel ile bağlandı
- ✅ SignUpScreen - ViewModel ile bağlandı
- ✅ ForgotPasswordScreen - ViewModel ile bağlandı
- ✅ Loading states ve error handling eklendi

---

## 🚀 Kullanım

### 1. Backend'i Başlatın

```powershell
cd backend
npm install
npm run dev
```

Backend `http://localhost:3000` adresinde çalışacak.

### 2. Base URL Yapılandırması

**Android Emulator için:**
- `Constants.kt` dosyasında `BASE_URL = "http://10.0.2.2:3000"` olarak ayarlanmış
- Bu ayar emulator için doğru çalışır

**Gerçek Cihaz için:**
- Bilgisayarınızın IP adresini bulun (örn: `192.168.1.100`)
- `Constants.kt` dosyasında `BASE_URL` değerini güncelleyin:
  ```kotlin
  const val BASE_URL = "http://192.168.1.100:3000"
  ```
- **Önemli:** Bilgisayar ve telefon aynı WiFi ağında olmalı

### 3. Android Uygulamasını Çalıştırın

Android Studio'da projeyi açın ve çalıştırın.

---

## 📁 Oluşturulan Dosya Yapısı

```
Frontend/app/src/main/java/com/example/glowmance/
├── data/
│   ├── api/
│   │   ├── ApiClient.kt          # Retrofit client yapılandırması
│   │   ├── ApiService.kt          # API endpoint tanımları
│   │   └── models/
│   │       ├── AuthRequest.kt      # Request modelleri
│   │       ├── AuthResponse.kt     # Response modelleri
│   │       └── User.kt            # User modeli
│   └── repository/
│       └── AuthRepository.kt       # API çağrıları ve token yönetimi
├── ui/
│   ├── screens/
│   │   ├── SignInScreen.kt        # ✅ ViewModel ile entegre
│   │   ├── SignUpScreen.kt        # ✅ ViewModel ile entegre
│   │   └── ForgotPasswordScreen.kt # ✅ ViewModel ile entegre
│   └── viewmodel/
│       ├── SignInViewModel.kt
│       ├── SignUpViewModel.kt
│       ├── ForgotPasswordViewModel.kt
│       └── ViewModelFactory.kt
└── utils/
    └── Constants.kt                # BASE_URL ve diğer sabitler
```

---

## 🔧 Özellikler

### Authentication Flow
1. **Kayıt (Sign Up):**
   - Kullanıcı bilgileri backend'e gönderilir
   - Başarılı olursa token'lar kaydedilir ve Home ekranına yönlendirilir
   - Hata durumunda kullanıcıya mesaj gösterilir

2. **Giriş (Sign In):**
   - Email ve şifre backend'e gönderilir
   - Başarılı olursa token'lar kaydedilir ve Home ekranına yönlendirilir
   - Hata durumunda kullanıcıya mesaj gösterilir

3. **Şifre Sıfırlama (Forgot Password):**
   - Email backend'e gönderilir
   - Başarılı olursa kullanıcıya bilgi mesajı gösterilir

### Token Yönetimi
- Access token ve refresh token SharedPreferences'te saklanır
- Token'lar otomatik olarak API isteklerinde kullanılır
- Logout işleminde token'lar temizlenir

### Error Handling
- Network hataları yakalanır ve kullanıcıya gösterilir
- Validation hataları anında gösterilir
- Loading state'leri ile kullanıcı bilgilendirilir

---

## 🐛 Sorun Giderme

### Backend'e Bağlanamıyor

1. **Backend çalışıyor mu?**
   - Tarayıcıda `http://localhost:3000/health` adresini açın
   - "ok" mesajı görmelisiniz

2. **Emulator kullanıyorsanız:**
   - `BASE_URL = "http://10.0.2.2:3000"` olmalı
   - Bu emulator için özel IP adresidir

3. **Gerçek cihaz kullanıyorsanız:**
   - Bilgisayar ve telefon aynı WiFi'de olmalı
   - Firewall backend portunu engellememeli
   - `BASE_URL` bilgisayarın IP adresi olmalı

### CORS Hatası

Backend'de `config/config.js` dosyasında CORS ayarlarını kontrol edin:
```javascript
static get CORS_ORIGIN() {
    return process.env.CORS_ORIGIN || '*';
}
```

Development için `*` kabul edilebilir, production'da spesifik URL kullanın.

### Token Sorunları

- Token'lar SharedPreferences'te saklanır
- Logout sonrası token'lar temizlenir
- Yeni giriş yapıldığında eski token'lar üzerine yazılır

---

## 📝 Sonraki Adımlar

1. **Diğer API'leri entegre edin:**
   - Upload API (cilt analizi için)
   - Analysis History API
   - Products API
   - User Profile API

2. **Token refresh mekanizması ekleyin:**
   - Access token süresi dolduğunda otomatik refresh

3. **Offline support:**
   - Room database ile local cache
   - Network durumu kontrolü

4. **Error handling iyileştirmeleri:**
   - Daha detaylı hata mesajları
   - Retry mekanizması

---

## ✅ Test Etme

1. Backend'i başlatın
2. Android uygulamasını çalıştırın
3. Sign Up ekranından yeni kullanıcı oluşturun
4. Sign In ekranından giriş yapın
5. Home ekranına yönlendirildiğinizi kontrol edin

**Başarılı! 🎉**

