package com.example.glowmance.data.repository

import com.example.glowmance.data.api.AuthApi
import com.example.glowmance.data.api.RetrofitClient
import com.example.glowmance.data.model.LoginRequest
import com.example.glowmance.data.model.LoginResponse
import com.example.glowmance.data.model.RegisterRequest
import com.example.glowmance.data.model.RegisterResponse

class AuthRepository {
    private val authApi: AuthApi = RetrofitClient.instance.create(AuthApi::class.java)

    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = authApi.login(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    if (errorBody != null) {
                        // Basit JSON parsing: "error":"..." veya "message":"..."
                        val json = org.json.JSONObject(errorBody)
                        if (json.has("error")) json.getString("error")
                        else if (json.has("message")) json.getString("message")
                        else errorBody
                    } else {
                        "Login failed"
                    }
                } catch (e: Exception) {
                    errorBody ?: "Login failed"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = authApi.register(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
