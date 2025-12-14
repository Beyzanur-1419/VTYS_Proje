package com.example.glowmance.data.api.models

data class AuthResponse(
    val token: String,
    val refreshToken: String
)

data class ErrorResponse(
    val status: String? = null,
    val message: String? = null,
    val error: String? = null
)

