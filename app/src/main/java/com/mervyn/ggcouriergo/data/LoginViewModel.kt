package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Idle)
    val uiState: StateFlow<LoginUIState> = _uiState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUIState.Error("Email and password cannot be empty")
            return
        }

        _uiState.value = LoginUIState.Loading

        viewModelScope.launch {
            repository.login(email, password) { result ->
                when (result) {
                    is AuthRepository.LoginResult.Success -> {
                        _uiState.value = when (result.role) {
                            "driver" -> LoginUIState.SuccessDriver
                            "dispatcher" -> LoginUIState.SuccessDispatcher
                            "admin" -> LoginUIState.SuccessAdmin
                            else -> LoginUIState.Error("Unknown role")
                        }
                    }
                    is AuthRepository.LoginResult.Error -> {
                        _uiState.value = LoginUIState.Error(result.message)
                    }
                }
            }
        }
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

