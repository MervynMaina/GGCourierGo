package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.SplashUIState
import com.mervyn.ggcouriergo.repository.SplashRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class SplashViewModel(private val repository: SplashRepository) : ViewModel() {

    internal val _uiState = MutableStateFlow<SplashUIState>(SplashUIState.Loading)
    val uiState: StateFlow<SplashUIState> = _uiState

    init {
        checkUserRole()
    }

    private fun checkUserRole() {
        viewModelScope.launch {
            _uiState.value = SplashUIState.Loading
            delay(1500) // Aesthetic delay for branding visibility
            val result = repository.getUserRole()
            _uiState.value = result.fold(
                onSuccess = { role -> SplashUIState.Success(role) },
                onFailure = { e -> SplashUIState.Error(e.message ?: "Unknown error") }
            )
        }
    }

    open fun retryCheckUserRole() {
        checkUserRole()
    }
}

class SplashViewModelFactory(private val repository: SplashRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}