package com.example.glowmance.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glowmance.data.model.LoginRequest
import com.example.glowmance.data.model.RegisterRequest
import com.example.glowmance.data.model.User
import com.example.glowmance.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    var loginState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    var registerState by mutableStateOf<AuthState>(AuthState.Idle)
        private set
    
    // Basit bir hata mesajı yönetimi
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginState = AuthState.Loading
            errorMessage = null
            
            val result = repository.login(LoginRequest(email, password))
            
            result.onSuccess { response ->
                // Backend başarılı ise (200 OK), direkt başarılı kabul ediyoruz.
                if (response.accessToken != null || response.user != null) {
                     loginState = AuthState.Success(response.user, response.accessToken)
                } else {
                     loginState = AuthState.Error("Invalid response from server")
                     errorMessage = "Invalid response"
                }
            }.onFailure { e ->
                val errorText = e.message ?: "Network error"
                loginState = AuthState.Error(errorText)
                errorMessage = errorText
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            registerState = AuthState.Loading
            errorMessage = null
            
            val result = repository.register(RegisterRequest(name, email, password))
            
            result.onSuccess { response ->
                if (response.accessToken != null || response.user != null) {
                    registerState = AuthState.Success(response.user, response.accessToken)
                } else {
                    registerState = AuthState.Error("Invalid response from server")
                    errorMessage = "Invalid response"
                }
            }.onFailure { e ->
                val errorText = e.message ?: "Network error"
                registerState = AuthState.Error(errorText)
                errorMessage = errorText
            }
        }
    }
    
    // Hata mesajını temizle
    fun clearError() {
        errorMessage = null
    }
    
    // State'leri sıfırla
    fun resetState() {
        loginState = AuthState.Idle
        registerState = AuthState.Idle
        errorMessage = null
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User?, val token: String?) : AuthState()
    data class Error(val message: String) : AuthState()
}
