package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.LoginUIState
import com.mervyn.ggcouriergo.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Idle)
    val uiState: StateFlow<LoginUIState> = _uiState

    /**
     * Login function.
     * Authenticates user and relies on AuthRepository to determine the correct role-based Success state.
     */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUIState.Error("Email and password cannot be empty")
            return
        }

        _uiState.value = LoginUIState.Loading

        viewModelScope.launch {
            // AuthRepository performs login, role lookup, and returns the final UI state
            val result = repository.login(email, password)
            _uiState.value = result
        }
    }

    /**
     * Forgot Password function
     */
    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _uiState.value = LoginUIState.Error("Please enter your email to reset your password.")
            return
        }

        _uiState.value = LoginUIState.Loading

        viewModelScope.launch {
            val result = repository.sendPasswordResetEmail(email)
            _uiState.value = result.fold(
                onSuccess = { LoginUIState.PasswordResetSent },
                onFailure = { e -> LoginUIState.PasswordResetError(e.message ?: "Failed to send reset email.") }
            )
        }
    }

    /**
     * Helper to clear transient messages (like reset success/error)
     */
    fun resetStateToIdle() {
        _uiState.value = LoginUIState.Idle
    }
}

class LoginViewModelFactory(private val repository: AuthRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}