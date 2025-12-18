package com.example.glowmance.data.api

import com.example.glowmance.data.model.LoginRequest
import com.example.glowmance.data.model.LoginResponse
import com.example.glowmance.data.model.RegisterRequest
import com.example.glowmance.data.model.RegisterResponse
import com.example.glowmance.data.model.ChangePasswordRequest
import com.example.glowmance.data.model.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/change-password")
    suspend fun changePassword(
        @retrofit2.http.Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<BaseResponse>

    @retrofit2.http.DELETE("auth/delete-account")
    suspend fun deleteAccount(
        @retrofit2.http.Header("Authorization") token: String
    ): Response<BaseResponse>
}
