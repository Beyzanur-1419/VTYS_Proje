package com.example.glowmance.data.api

import com.example.glowmance.data.api.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Authentication endpoints
    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("/api/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>
    
    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ErrorResponse>
    
    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ErrorResponse>
    
    @POST("/api/auth/logout")
    suspend fun logout(): Response<ErrorResponse>
    
    // User endpoints
    @GET("/api/user/profile")
    suspend fun getUserProfile(): Response<User>
    
    @PUT("/api/user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<User>
    
    @PUT("/api/user/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ErrorResponse>
    
    // Upload endpoints
    @Multipart
    @POST("/api/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<Map<String, Any>>
    
    // Analysis History endpoints
    @GET("/api/analysis-history")
    suspend fun getAnalysisHistory(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<Map<String, Any>>
    
    @GET("/api/analysis-history/stats/summary")
    suspend fun getAnalysisStatistics(): Response<Map<String, Any>>
    
    @GET("/api/analysis-history/{id}")
    suspend fun getAnalysisById(@Path("id") id: String): Response<Map<String, Any>>
    
    @POST("/api/analysis-history")
    suspend fun createAnalysis(@Body request: Map<String, Any>): Response<Map<String, Any>>
    
    @DELETE("/api/analysis-history/{id}")
    suspend fun deleteAnalysis(@Path("id") id: String): Response<Map<String, Any>>
    
    // Products endpoints
    @GET("/api/products/recommendations")
    suspend fun getProductRecommendations(
        @Query("limit") limit: Int? = null,
        @Query("includeTrending") includeTrending: Boolean? = null
    ): Response<Map<String, Any>>
    
    @GET("/api/products/trending")
    suspend fun getTrendingProducts(
        @Query("limit") limit: Int? = null
    ): Response<Map<String, Any>>
    
    // Health check
    @GET("/health")
    suspend fun healthCheck(): Response<Map<String, String>>
}

