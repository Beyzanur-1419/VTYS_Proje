package com.example.glowmance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glowmance.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SignUpUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class SignUpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()
    
    fun signUp(username: String, email: String, password: String, confirmPassword: String, onSuccess: () -> Unit) {
        // Validation
        when {
            username.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Username is required")
                return
            }
            email.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Email is required")
                return
            }
            password.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Password is required")
                return
            }
            password != confirmPassword -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Passwords do not match")
                return
            }
            password.length < 6 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 6 characters")
                return
            }
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            val result = authRepository.register(email, password, username)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null
                )
                onSuccess()
            } else {
                val exception = result.exceptionOrNull()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = exception?.message ?: "Registration failed"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

