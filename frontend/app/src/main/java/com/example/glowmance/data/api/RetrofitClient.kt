package com.example.glowmance.data.api

import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Use BASIC or NONE to avoid out of memory with large images
        level = HttpLoggingInterceptor.Level.BODY 
    }

    // Interceptor to add headers for Localtunnel bypass
    private val headersInterceptor = Interceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .header("Bypass-Tunnel-Reminder", "true")
            .header("ngrok-skip-browser-warning", "true") // For ngrok fallback
            .method(original.method, original.body)
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headersInterceptor) // Add header interceptor
        .addInterceptor(loggingInterceptor)
        // Add a custom interceptor to handle connection errors or log specific events
        .addInterceptor { chain ->
             try {
                 val response = chain.proceed(chain.request())
                 response
             } catch (e: Exception) {
                 // Log error or rethrow a custom exception if needed
                 throw e
             }
        }
        .connectTimeout(NetworkConfig.TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(NetworkConfig.TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(NetworkConfig.TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    val authApi: AuthApi by lazy { instance.create(AuthApi::class.java) }
    val productApi: ProductApi by lazy { instance.create(ProductApi::class.java) }
    val userApi: UserApi by lazy { instance.create(UserApi::class.java) }
    
    // We will separate ML logic later, but for now keep it here or create a new one
    val analysisApi: AnalysisApi by lazy { instance.create(AnalysisApi::class.java) }
    val mlApi: MLApi by lazy { instance.create(MLApi::class.java) }
}
