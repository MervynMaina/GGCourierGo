package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.OnboardingUIState
import com.mervyn.ggcouriergo.repository.OnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(private val repository: OnboardingRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingUIState>(OnboardingUIState.Loading)
    val uiState: StateFlow<OnboardingUIState> = _uiState

    // Removed userId parameter as it's not needed for local onboarding flag
    fun completeOnboarding() {
        viewModelScope.launch {
            repository.completeOnboarding()
            _uiState.value = OnboardingUIState.Finished
        }
    }
}

class OnboardingViewModelFactory(private val repository: OnboardingRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            return OnboardingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}