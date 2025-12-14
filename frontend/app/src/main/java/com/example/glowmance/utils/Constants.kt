package com.example.glowmance.utils

object Constants {
    // Backend API Base URL
    // Android Emulator için: http://10.0.2.2:3000
    // Gerçek cihaz için: Bilgisayarınızın IP adresi (örn: http://192.168.1.100:3000)
    const val BASE_URL = "http://10.0.2.2:3000"
    
    // API Endpoints
    const val API_AUTH = "/api/auth"
    const val API_UPLOAD = "/api/upload"
    const val API_USER = "/api/user"
    const val API_ANALYSIS_HISTORY = "/api/analysis-history"
    const val API_PRODUCTS = "/api/products"
    
    // SharedPreferences Keys
    const val PREFS_NAME = "glowmance_prefs"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_USER_EMAIL = "user_email"
}

