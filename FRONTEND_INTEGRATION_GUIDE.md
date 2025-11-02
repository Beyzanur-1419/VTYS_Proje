# Frontend Entegrasyon Rehberi

Frontend geliştiricileri için backend API entegrasyon rehberi.

## 🚀 Hızlı Başlangıç

### 1. Base URL

```javascript
const API_BASE_URL = 'http://localhost:3000/api';
// Production: 'https://api.glowmance.com/api'
```

### 2. Authentication Setup

```javascript
// Token storage (localStorage veya state management)
let accessToken = localStorage.getItem('accessToken');
let refreshToken = localStorage.getItem('refreshToken');

// API client setup (axios örneği)
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:3000/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Token interceptor
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Refresh token interceptor (401 hatası için)
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post(`${API_BASE_URL}/auth/refresh-token`, {
          refreshToken,
        });
        
        const { accessToken, refreshToken: newRefreshToken } = response.data.data;
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);
        
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        // Refresh token hatası - logout
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);
```

---

## 📋 API Endpoint'leri

### 🔐 Authentication

#### 1. Kullanıcı Kaydı

```javascript
const register = async (email, password, name) => {
  try {
    const response = await apiClient.post('/auth/register', {
      email,
      password,
      name,
    });
    
    const { accessToken, refreshToken, user } = response.data.data;
    
    // Token'ları sakla
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    
    return { user, accessToken, refreshToken };
  } catch (error) {
    throw error.response?.data || error;
  }
};
```

**Kullanım:**
```javascript
try {
  const { user, accessToken } = await register('user@example.com', 'Test123', 'Test User');
  console.log('Kayıt başarılı:', user);
} catch (error) {
  console.error('Kayıt hatası:', error.message);
}
```

---

#### 2. Kullanıcı Girişi

```javascript
const login = async (email, password) => {
  try {
    const response = await apiClient.post('/auth/login', {
      email,
      password,
    });
    
    const { accessToken, refreshToken, user } = response.data.data;
    
    // Token'ları sakla
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    
    return { user, accessToken, refreshToken };
  } catch (error) {
    throw error.response?.data || error;
  }
};
```

**Kullanım:**
```javascript
try {
  const { user } = await login('user@example.com', 'Test123');
  console.log('Giriş başarılı:', user);
  // Redirect to dashboard
} catch (error) {
  console.error('Giriş hatası:', error.message);
}
```

---

#### 3. Çıkış

```javascript
const logout = async () => {
  try {
    await apiClient.post('/auth/logout');
    
    // Token'ları temizle
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    
    // Redirect to login
    window.location.href = '/login';
  } catch (error) {
    console.error('Çıkış hatası:', error);
  }
};
```

---

### 📤 Görsel Yükleme

#### Görsel Yükleme ve Analiz

```javascript
const uploadImage = async (imageFile) => {
  try {
    const formData = new FormData();
    formData.append('image', imageFile);
    
    const response = await apiClient.post('/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    
    const { image, analysis } = response.data.data;
    
    return { image, analysis };
  } catch (error) {
    throw error.response?.data || error;
  }
};
```

**Kullanım (React örneği):**
```javascript
const handleImageUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  try {
    setLoading(true);
    const { image, analysis } = await uploadImage(file);
    
    console.log('Yükleme başarılı:', image);
    console.log('Analiz sonuçları:', analysis);
    
    // UI'da göster
    setAnalysisResults(analysis);
    setImageUrl(image.url);
  } catch (error) {
    console.error('Yükleme hatası:', error.message);
    alert('Görsel yükleme başarısız: ' + error.message);
  } finally {
    setLoading(false);
  }
};

// JSX
<input 
  type="file" 
  accept="image/*" 
  onChange={handleImageUpload}
  disabled={loading}
/>
```

---

### 📊 Analiz Geçmişi

#### Analiz Geçmişini Listeleme

```javascript
const getAnalysisHistory = async (page = 1, limit = 20) => {
  try {
    const response = await apiClient.get('/analysis-history', {
      params: {
        page,
        limit,
      },
    });
    
    const { analyses, pagination } = response.data.data;
    
    return { analyses, pagination };
  } catch (error) {
    throw error.response?.data || error;
  }
};
```

**Kullanım:**
```javascript
const [analyses, setAnalyses] = useState([]);
const [pagination, setPagination] = useState(null);

useEffect(() => {
  const loadHistory = async () => {
    try {
      const { analyses, pagination } = await getAnalysisHistory(1, 20);
      setAnalyses(analyses);
      setPagination(pagination);
    } catch (error) {
      console.error('Geçmiş yükleme hatası:', error);
    }
  };
  
  loadHistory();
}, []);
```

---

#### Tek Analiz Getir

```javascript
const getAnalysis = async (analysisId) => {
  try {
    const response = await apiClient.get(`/analysis-history/${analysisId}`);
    return response.data.data.analysis;
  } catch (error) {
    throw error.response?.data || error;
  }
};
```

---

#### İstatistikler

```javascript
const getStatistics = async () => {
  try {
    const response = await apiClient.get('/analysis-history/stats/summary');
    return response.data.data.statistics;
  } catch (error) {
    throw error.response?.data || error;
  }
};
```

---

### 🛍️ Ürün Önerileri

#### Kişiselleştirilmiş Öneriler

```javascript
const getRecommendations = async (limit = 10, includeTrending = true) => {
  try {
    const response = await apiClient.get('/products/recommendations', {
      params: {
        limit,
        includeTrending,
      },
    });
    
    return response.data.data;
  } catch (error) {
    throw error.response?.data || error;
  }
};
```

**Kullanım:**
```javascript
const [recommendations, setRecommendations] = useState([]);
const [insights, setInsights] = useState(null);

useEffect(() => {
  const loadRecommendations = async () => {
    try {
      const data = await getRecommendations(10);
      setRecommendations(data.recommendations);
      setInsights(data.insights);
      
      console.log('Öneriler:', data.recommendations);
      console.log('Insights:', data.insights);
    } catch (error) {
      console.error('Öneriler yükleme hatası:', error);
    }
  };
  
  loadRecommendations();
}, []);
```

---

#### Trend Ürünler

```javascript
const getTrendingProducts = async (limit = 10) => {
  try {
    const response = await apiClient.get('/products/trending', {
      params: {
        limit,
      },
    });
    
    return response.data.data;
  } catch (error) {
    throw error.response?.data || error;
  }
};
```

---

### 👤 Kullanıcı Profili

#### Profil Bilgileri

```javascript
const getProfile = async () => {
  try {
    const response = await apiClient.get('/user/profile');
    return response.data.data.user;
  } catch (error) {
    throw error.response?.data || error;
  }
};
```

---

#### Profil Güncelleme

```javascript
const updateProfile = async (name, email) => {
  try {
    const response = await apiClient.put('/user/profile', {
      name,
      email,
    });
    
    return response.data.data.user;
  } catch (error) {
    throw error.response?.data || error;
  }
};
```

---

#### Şifre Değiştirme

```javascript
const changePassword = async (currentPassword, newPassword) => {
  try {
    await apiClient.put('/user/password', {
      currentPassword,
      newPassword,
    });
    
    return true;
  } catch (error) {
    throw error.response?.data || error;
  }
};
```

---

## 🔄 Error Handling

### Global Error Handler

```javascript
// Error handling utility
const handleApiError = (error) => {
  if (error.response) {
    // Server responded with error
    const { status, data } = error.response;
    
    switch (status) {
      case 400:
        return `Validation Error: ${data.message}`;
      case 401:
        // Token expired - refresh token logic should handle this
        return 'Authentication required';
      case 403:
        return 'Access denied';
      case 404:
        return 'Not found';
      case 500:
        return 'Server error. Please try again later.';
      default:
        return data.message || 'An error occurred';
    }
  } else if (error.request) {
    // Request made but no response
    return 'Network error. Please check your connection.';
  } else {
    // Something else happened
    return error.message || 'An unexpected error occurred';
  }
};
```

---

## 🎨 React Hook Örnekleri

### useAuth Hook

```javascript
import { useState, useEffect } from 'react';
import { login, register, logout, getProfile } from '../services/auth';

export const useAuth = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem('accessToken');
      if (token) {
        try {
          const userData = await getProfile();
          setUser(userData);
        } catch (error) {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
        }
      }
      setLoading(false);
    };

    initAuth();
  }, []);

  const handleLogin = async (email, password) => {
    try {
      const { user } = await login(email, password);
      setUser(user);
      return user;
    } catch (error) {
      throw error;
    }
  };

  const handleRegister = async (email, password, name) => {
    try {
      const { user } = await register(email, password, name);
      setUser(user);
      return user;
    } catch (error) {
      throw error;
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
      setUser(null);
    } catch (error) {
      console.error('Logout error:', error);
    }
  };

  return {
    user,
    loading,
    login: handleLogin,
    register: handleRegister,
    logout: handleLogout,
    isAuthenticated: !!user,
  };
};
```

---

### useAnalysisHistory Hook

```javascript
import { useState, useEffect } from 'react';
import { getAnalysisHistory, getStatistics } from '../services/analysis';

export const useAnalysisHistory = (page = 1, limit = 20) => {
  const [analyses, setAnalyses] = useState([]);
  const [pagination, setPagination] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadHistory = async () => {
      try {
        setLoading(true);
        setError(null);
        const { analyses, pagination } = await getAnalysisHistory(page, limit);
        setAnalyses(analyses);
        setPagination(pagination);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    loadHistory();
  }, [page, limit]);

  return { analyses, pagination, loading, error };
};
```

---

### useRecommendations Hook

```javascript
import { useState, useEffect } from 'react';
import { getRecommendations } from '../services/products';

export const useRecommendations = (limit = 10) => {
  const [recommendations, setRecommendations] = useState([]);
  const [insights, setInsights] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadRecommendations = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await getRecommendations(limit);
        setRecommendations(data.recommendations);
        setInsights(data.insights);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    loadRecommendations();
  }, [limit]);

  return { recommendations, insights, loading, error };
};
```

---

## 📝 TypeScript Type Definitions

```typescript
// types/api.ts

export interface User {
  id: string;
  email: string;
  name: string;
}

export interface AuthResponse {
  status: 'success';
  data: {
    user: User;
    accessToken: string;
    refreshToken: string;
  };
}

export interface Image {
  _id: string;
  userId: string;
  originalName: string;
  mimeType: string;
  size: number;
  url: string;
  cloudProvider: 'cloudinary' | 's3' | 'local';
  publicId?: string;
  cloudUrl?: string;
  width?: number;
  height?: number;
  format?: string;
  timestamp: string;
}

export interface AnalysisResult {
  confidence: number;
  conditions: string[];
  recommendations: string[];
  metadata: {
    processed_at?: string;
    fallback?: boolean;
    error?: string;
  };
}

export interface Analysis {
  id: string;
  userId: string;
  imageId: string;
  results: AnalysisResult;
  timestamp: string;
  image?: Image;
}

export interface Pagination {
  total: number;
  limit: number;
  offset: number;
  currentPage: number;
  totalPages: number;
  hasNextPage: boolean;
  hasPreviousPage: boolean;
}

export interface AnalysisHistoryResponse {
  status: 'success';
  data: {
    analyses: Analysis[];
    pagination: Pagination;
  };
}

export interface Product {
  id: string;
  name: string;
  category: string;
  price: number;
  rating: number;
  imageUrl: string;
  tags: string[];
  isRecommended?: boolean;
  isTrending?: boolean;
}

export interface RecommendationsResponse {
  status: 'success';
  data: {
    recommendations: Product[];
    source: 'personalized' | 'trending';
    message: string;
    basedOnHistory: boolean;
    insights?: {
      conditions: string[];
      recommendations: string[];
      totalAnalyses: number;
    };
  };
}
```

---

## 🔧 Configuration

### Environment Variables

Frontend için örnek `.env` dosyası:

```env
REACT_APP_API_URL=http://localhost:3000/api
# Production: REACT_APP_API_URL=https://api.glowmance.com/api
```

### API Client Setup

```javascript
// config/api.js
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:3000/api';

export default API_BASE_URL;
```

---

## ✅ Checklist

Frontend entegrasyonu için kontrol listesi:

- [ ] API base URL yapılandırıldı
- [ ] Authentication token yönetimi eklendi
- [ ] Token refresh mekanizması eklendi
- [ ] Error handling yapıldı
- [ ] Loading states eklendi
- [ ] Form validation yapıldı
- [ ] File upload (multipart/form-data) yapılandırıldı
- [ ] CORS ayarları kontrol edildi

---

## 📚 Detaylı Dokümantasyon

- `BACKEND_API_DOCUMENTATION.md` - Tüm API endpoint'leri
- `API_AUTH_TEST.md` - Auth endpoint testleri
- `UPLOAD_FLOW_TEST.md` - Upload endpoint testleri
- `ANALYSIS_HISTORY_LIST_API.md` - Analysis history detayları

