package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.DispatcherDashboardUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

open class DispatcherDashboardViewModel(private val repository: ParcelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DispatcherDashboardUIState>(DispatcherDashboardUIState.Loading)
    val uiState: StateFlow<DispatcherDashboardUIState> = _uiState

    init {
        // FIX: Pointing to getPendingParcelsFlow (the actual name in your Repository)
        // Explicitly typing the Flow to solve "Cannot infer type" errors
        repository.getPendingParcelsFlow()
            .onEach { parcels: List<Parcel> ->
                _uiState.value = DispatcherDashboardUIState.Success(parcels)
            }
            .catch { e ->
                _uiState.value = DispatcherDashboardUIState.Error(e.message ?: "Failed to load parcels")
            }
            .launchIn(viewModelScope)
    }

    /**
     * Note: You must ensure 'assignDriver' exists in ParcelRepository.
     * If you haven't added it yet, this will stay red.
     */
    fun assignDriver(parcelId: String, driverId: String) {
        viewModelScope.launch {
            // Check your ParcelRepository for this function name
            repository.assignDriver(parcelId, driverId)
        }
    }
}

class DispatcherDashboardViewModelFactory(private val repository: ParcelRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DispatcherDashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DispatcherDashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}