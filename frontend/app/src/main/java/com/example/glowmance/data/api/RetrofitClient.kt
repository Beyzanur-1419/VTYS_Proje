package com.example.glowmance.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Android emulator access to localhost
    // Updated for physical device connection (detected IP)
    // Android emulator access to localhost
    private const val BASE_URL = "http://10.0.2.2:3001/api/v1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    val authApi: AuthApi by lazy {
        instance.create(AuthApi::class.java)
    }

    val productApi: ProductApi by lazy {
        instance.create(ProductApi::class.java)
    }

    val userApi: UserApi by lazy {
        instance.create(UserApi::class.java)
    }
}
