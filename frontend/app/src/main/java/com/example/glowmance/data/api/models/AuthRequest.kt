package com.example.glowmance.data.api.models

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val token: String,
    val password: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

