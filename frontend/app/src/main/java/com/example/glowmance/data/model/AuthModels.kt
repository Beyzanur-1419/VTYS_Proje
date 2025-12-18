package com.example.glowmance.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val user: User?
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val user: User?
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

data class NotificationSettingsRequest(
    val notificationEnabled: Boolean,
    val emailNotifications: Boolean,
    val analysisReminder: Boolean,
    val campaigns: Boolean,
    val tips: Boolean
)

data class BaseResponse(
    val success: Boolean,
    val message: String?
)
