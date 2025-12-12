package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.SplashUIState
import com.mervyn.ggcouriergo.repository.SplashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class SplashViewModel(private val repository: SplashRepository) : ViewModel() {

    // Backing state
    internal val _uiState = MutableStateFlow<SplashUIState>(SplashUIState.Loading)
    val uiState: StateFlow<SplashUIState> = _uiState

    init {
        checkUserRole()
    }

    // Private function to load user role
    private fun checkUserRole() {
        viewModelScope.launch {
            _uiState.value = SplashUIState.Loading
            val result = repository.getUserRole()
            _uiState.value = result.fold(
                onSuccess = { role -> SplashUIState.Success(role) },
                onFailure = { e -> SplashUIState.Error(e.message ?: "Unknown error") }
            )
        }
    }

    // Public retry function for Retry button
    open fun retryCheckUserRole() {
        checkUserRole()
    }
}

// Factory remains unchanged
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
