package com.example.glowmance.data.api.models

data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

