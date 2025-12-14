package com.example.glowmance.data.repository

import android.util.Log
import com.example.glowmance.data.network.ApiService
import com.example.glowmance.data.network.AuthResponse
import com.example.glowmance.data.network.LoginRequest
import com.example.glowmance.data.network.RegisterRequest
import com.example.glowmance.data.network.ForgotPasswordRequest
import com.example.glowmance.data.network.ForgotPasswordResponse
import com.example.glowmance.data.network.ResetPasswordRequest
import com.example.glowmance.data.network.ResetPasswordResponse
import com.example.glowmance.data.network.RefreshTokenRequest
import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * Repository for authentication operations
 */
class AuthRepository(
    private val apiService: ApiService
) {
    private val gson = Gson()
    
    /**
     * Backend'den gelen JSON hata mesajını parse eder ve kullanıcı dostu bir mesaja çevirir
     * @param isRegister İşlem kayıt işlemi mi? (true: register, false: login)
     */
    private fun parseErrorMessage(errorBody: String?, statusCode: Int? = null, isRegister: Boolean = false): String {
        Log.d("AuthRepository", "Parsing error - Status: $statusCode, Body: $errorBody, IsRegister: $isRegister")
        
        if (errorBody.isNullOrBlank()) {
            return when (statusCode) {
                401 -> "Email veya şifre hatalı. Lütfen kontrol edip tekrar deneyin."
                404 -> if (isRegister) {
                    "Kayıt endpoint'i bulunamadı. Lütfen backend bağlantısını kontrol edin."
                } else {
                    "Kullanıcı bulunamadı. Email ve şifrenizi kontrol edin."
                }
                409 -> "Bu email adresi zaten kayıtlı. Lütfen farklı bir email kullanın veya giriş yapın."
                500 -> "Sunucu hatası. Lütfen daha sonra tekrar deneyin."
                else -> "Bir hata oluştu. Lütfen tekrar deneyin."
            }
        }
        
        return try {
            val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
            val message = when {
                jsonObject.has("message") -> jsonObject.get("message").asString
                jsonObject.has("error") -> jsonObject.get("error").asString
                else -> errorBody
            }
            
            Log.d("AuthRepository", "Extracted message: $message")
            
            // Teknik hataları kullanıcı dostu mesajlara çevir
            when {
                message.contains("user already exists", ignoreCase = true) -> {
                    "Bu email adresi zaten kayıtlı. Lütfen farklı bir email kullanın veya giriş yapın."
                }
                message.contains("invalid input syntax for type integer", ignoreCase = true) -> {
                    "⚠️ Backend Hatası: Veritabanı uyumsuzluğu. Backend geliştiricisiyle iletişime geçin."
                }
                message.contains("duplicate key", ignoreCase = true) -> {
                    "Bu email adresi zaten kayıtlı. Lütfen farklı bir email kullanın veya giriş yapın."
                }
                message.contains("validation error", ignoreCase = true) -> {
                    "Girilen bilgiler geçersiz. Lütfen kontrol edip tekrar deneyin."
                }
                message.contains("invalid email or password", ignoreCase = true) -> {
                    "Email veya şifre hatalı. Lütfen kontrol edip tekrar deneyin."
                }
                message.contains("invalid credentials", ignoreCase = true) -> {
                    "Email veya şifre hatalı. Lütfen kontrol edip tekrar deneyin."
                }
                message.contains("wrong password", ignoreCase = true) -> {
                    "Şifre hatalı. Lütfen tekrar deneyin."
                }
                message.contains("user not found", ignoreCase = true) -> {
                    "Bu email adresi ile kayıtlı bir kullanıcı bulunamadı. Lütfen kayıt olun."
                }
                // 404 hatası için özel kontrol - "Not Found" mesajı endpoint bulunamadı anlamına gelir
                (message.contains("not found", ignoreCase = true) && statusCode == 404) -> {
                    if (isRegister) {
                        "Kayıt endpoint'i bulunamadı. Backend bağlantısını kontrol edin veya backend'in çalıştığından emin olun."
                    } else {
                        "Endpoint bulunamadı. Backend bağlantısını kontrol edin."
                    }
                }
                // "user not found" özel kontrolü
                message.contains("user not found", ignoreCase = true) -> {
                    if (isRegister) {
                        "Kayıt işlemi başarısız. Lütfen tekrar deneyin."
                    } else {
                        "Bu email adresi ile kayıtlı bir kullanıcı bulunamadı. Lütfen kayıt olun."
                    }
                }
                message.contains("unauthorized", ignoreCase = true) -> {
                    "Yetkisiz erişim. Lütfen tekrar giriş yapın."
                }
                else -> message
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error parsing error message", e)
            // JSON parse edilemezse, direkt errorBody'i döndür veya status code'a göre mesaj ver
            when (statusCode) {
                401 -> "Email veya şifre hatalı. Lütfen kontrol edip tekrar deneyin."
                404 -> if (isRegister) {
                    "Kayıt endpoint'i bulunamadı. Lütfen backend bağlantısını kontrol edin."
                } else {
                    "Kullanıcı bulunamadı. Email ve şifrenizi kontrol edin."
                }
                409 -> "Bu email adresi zaten kayıtlı. Lütfen farklı bir email kullanın veya giriş yapın."
                500 -> "Sunucu hatası. Lütfen daha sonra tekrar deneyin."
                else -> errorBody.takeIf { it.isNotBlank() } ?: "Bir hata oluştu. Lütfen tekrar deneyin."
            }
        }
    }
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            Log.d("AuthRepository", "Attempting login for email: $email")
            val response = apiService.login(LoginRequest(email, password))
            
            Log.d("AuthRepository", "Login response - Code: ${response.code()}, Success: ${response.isSuccessful}")
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Token kontrolü
                if (authResponse.accessToken.isBlank()) {
                    Log.e("AuthRepository", "AccessToken is empty in response")
                    return Result.failure(Exception("Token alınamadı. Lütfen tekrar deneyin."))
                }
                Log.d("AuthRepository", "Login successful for user: ${authResponse.user.name}")
                return Result.success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.e("AuthRepository", "Login failed - Status: $statusCode, Error: $errorBody")
                
                val errorMessage = parseErrorMessage(errorBody, statusCode, isRegister = false)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e("AuthRepository", "Network error: UnknownHostException", e)
            Result.failure(Exception("Sunucuya bağlanılamadı. İnternet bağlantınızı kontrol edin."))
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("AuthRepository", "Network error: SocketTimeoutException", e)
            Result.failure(Exception("Bağlantı zaman aşımına uğradı. Lütfen tekrar deneyin."))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login exception", e)
            Result.failure(Exception("Bir hata oluştu: ${e.message ?: "Bilinmeyen hata"}"))
        }
    }
    
    suspend fun register(email: String, password: String, name: String): Result<AuthResponse> {
        return try {
            Log.d("AuthRepository", "Attempting registration for email: $email, name: $name")
            val response = apiService.register(RegisterRequest(email, password, name))
            
            Log.d("AuthRepository", "Register response - Code: ${response.code()}, Success: ${response.isSuccessful}")
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Token kontrolü
                if (authResponse.accessToken.isBlank()) {
                    Log.e("AuthRepository", "AccessToken is empty in response")
                    return Result.failure(Exception("Token alınamadı. Lütfen tekrar deneyin."))
                }
                Log.d("AuthRepository", "Registration successful for user: ${authResponse.user.name}")
                return Result.success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val statusCode = response.code()
                Log.e("AuthRepository", "Registration failed - Status: $statusCode, Error: $errorBody")
                
                val errorMessage = parseErrorMessage(errorBody, statusCode, isRegister = true)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e("AuthRepository", "Network error: UnknownHostException", e)
            Result.failure(Exception("Sunucuya bağlanılamadı. İnternet bağlantınızı kontrol edin."))
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("AuthRepository", "Network error: SocketTimeoutException", e)
            Result.failure(Exception("Bağlantı zaman aşımına uğradı. Lütfen tekrar deneyin."))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration exception", e)
            Result.failure(Exception("Bir hata oluştu: ${e.message ?: "Bilinmeyen hata"}"))
        }
    }
    
    suspend fun refreshToken(refreshToken: String): Result<AuthResponse> {
        return try {
            val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Token refresh failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun forgotPassword(email: String): Result<ForgotPasswordResponse> {
        return try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Forgot password failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun resetPassword(token: String, password: String): Result<ResetPasswordResponse> {
        return try {
            val response = apiService.resetPassword(ResetPasswordRequest(token, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Reset password failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return try {
            val response = apiService.logout()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Logout failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
