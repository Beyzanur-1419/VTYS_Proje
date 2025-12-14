package com.example.glowmance.data.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API Service interface for GLOWMANCE backend
 * Backend Base URL: http://10.0.2.2:3000 (Emulator) veya http://[IP]:3000 (Gerçek cihaz)
 */
interface ApiService {
    
    // ========== AUTHENTICATION (/api/v1/auth) ==========
    
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("api/v1/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>
    
    @POST("api/v1/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>
    
    @POST("api/v1/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>
    
    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<LogoutResponse>
    
    // ========== USER (/api/v1/user) ==========
    
    @GET("api/v1/user/profile")
    suspend fun getUserProfile(): Response<UserProfile>
    
    @PUT("api/v1/user/profile")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): Response<UserProfile>
    
    @PUT("api/v1/user/skin-profile")
    suspend fun updateSkinProfile(@Body request: UpdateSkinProfileRequest): Response<UserProfile>
    
    @GET("api/v1/user/settings")
    suspend fun getSettings(): Response<UserSettings>
    
    @PUT("api/v1/user/settings")
    suspend fun updateSettings(@Body request: UpdateSettingsRequest): Response<UserSettings>
    
    @GET("api/v1/user/notifications")
    suspend fun getNotifications(): Response<NotificationsResponse>
    
    @PUT("api/v1/user/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>
    
    // ========== UPLOAD (/api/v1/upload) ==========
    
    @Multipart
    @POST("api/v1/upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<UploadResponse>
    
    // ========== ANALYSIS HISTORY (/api/v1/analysis) ==========
    
    @GET("api/v1/analysis/history")
    suspend fun getAnalysisHistory(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<List<AnalysisHistoryItem>>
    
    @GET("api/v1/analysis/stats/summary")
    suspend fun getAnalysisStats(): Response<AnalysisStats>
    
    @GET("api/v1/analysis/history/{id}")
    suspend fun getAnalysisById(@Path("id") id: String): Response<AnalysisHistoryItem>
    
    @POST("api/v1/analysis")
    suspend fun createAnalysis(@Body request: CreateAnalysisRequest): Response<AnalysisHistoryItem>
    
    @DELETE("api/v1/analysis/history/{id}")
    suspend fun deleteAnalysis(@Path("id") id: String): Response<DeleteResponse>
    
    // ========== PRODUCTS (/api/v1/products) ==========
    
    @GET("api/v1/products/trending")
    suspend fun getTrendingProducts(@Query("limit") limit: Int? = null): Response<List<Product>>
    
    @GET("api/v1/products/recommendations")
    suspend fun getProductRecommendations(
        @Query("limit") limit: Int? = null,
        @Query("includeTrending") includeTrending: Boolean? = null
    ): Response<List<Product>>
    
    @POST("api/v1/products/scrape")
    suspend fun scrapeProducts(@Body request: ScrapeProductsRequest): Response<ScrapeProductsResponse>
    
    // ========== SYSTEM ==========
    
    @GET("health")
    suspend fun healthCheck(): Response<HealthResponse>
}

// ========== REQUEST/RESPONSE MODELS ==========

// Authentication
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val token: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String? = null,
    val user: UserProfile
)

data class ForgotPasswordResponse(
    val message: String
)

data class ResetPasswordResponse(
    val message: String
)

data class LogoutResponse(
    val message: String
)

// User
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val profileImageUrl: String? = null,
    val skinType: String? = null,
    val skinGoal: String? = null,
    val age: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class UpdateProfileRequest(
    val name: String? = null,
    val profileImageUrl: String? = null
)

data class UpdateSkinProfileRequest(
    val skinType: String? = null,
    val skinGoal: String? = null,
    val age: Int? = null
)

data class UserSettings(
    val notificationEnabled: Boolean = true,
    val emailNotifications: Boolean = true,
    val language: String = "tr",
    val theme: String = "dark"
)

data class UpdateSettingsRequest(
    val notificationEnabled: Boolean? = null,
    val emailNotifications: Boolean? = null
)

data class NotificationsResponse(
    val notifications: List<NotificationItem> = emptyList(),
    val unreadCount: Int = 0
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean = false,
    val createdAt: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class ChangePasswordResponse(
    val message: String
)

// Upload
data class UploadResponse(
    val url: String,
    val filename: String
)

// Analysis History
data class AnalysisHistoryItem(
    val id: String,
    val userId: String,
    val imageUrl: String? = null,
    val hasEczema: Boolean? = null,
    val eczemaLevel: String? = null,
    val hasAcne: Boolean? = null,
    val acneLevel: String? = null,
    val hasRosacea: Boolean? = null,
    val rosaceaLevel: String? = null,
    val isNormal: Boolean? = null,
    val createdAt: String,
    val updatedAt: String? = null
)

data class AnalysisStats(
    val totalAnalyses: Int,
    val lastAnalysisDate: String? = null,
    val eczemaCount: Int = 0,
    val acneCount: Int = 0,
    val rosaceaCount: Int = 0
)

data class CreateAnalysisRequest(
    val imageUrl: String? = null,
    val hasEczema: Boolean? = null,
    val eczemaLevel: String? = null,
    val hasAcne: Boolean? = null,
    val acneLevel: String? = null,
    val hasRosacea: Boolean? = null,
    val rosaceaLevel: String? = null,
    val isNormal: Boolean? = null
)

data class DeleteResponse(
    val message: String
)

// Products
data class Product(
    val id: String,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val price: Double? = null,
    val brand: String? = null,
    val url: String? = null,
    val category: String? = null
)

data class ScrapeProductsRequest(
    val query: String,
    val limit: Int? = null
)

data class ScrapeProductsResponse(
    val products: List<Product>,
    val count: Int
)

// System
data class HealthResponse(
    val status: String,
    val timestamp: String? = null
)
