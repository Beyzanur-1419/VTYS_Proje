package com.example.glowmance.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Token provider - TokenProvider'dan token alır
 */
private fun getToken(): String? {
    return TokenProvider.getToken()
}

/**
 * Network configuration module
 * 
 * BACKEND URL'İNİZİ BURAYA EKLEYİN:
 * - Android Emulator: "http://10.0.2.2:3000"
 * - Gerçek Cihaz: "http://[BILGISAYAR_IP]:3000" (örn: "http://192.168.1.100:3000")
 * - Production: "https://your-backend-url.com"
 */
object NetworkModule {
    
    // ⚠️ BACKEND URL'İNİZİ BURAYA EKLEYİN ⚠️
    // Android Emulator için:
    private const val BASE_URL = "http://10.0.2.2:3000"
    
    // Gerçek cihaz için (bilgisayarınızın IP adresini kullanın):
    // private const val BASE_URL = "http://192.168.1.100:3000"
    
    // Production için:
    // private const val BASE_URL = "https://api.glowmance.com"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Development için
        // Production'da Level.NONE kullanın
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor { getToken() })
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

