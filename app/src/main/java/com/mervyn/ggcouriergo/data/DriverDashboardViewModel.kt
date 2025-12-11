package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.DriverDashboardUIState
import com.mervyn.ggcouriergo.repository.ParcelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DriverDashboardViewModel(private val repository: ParcelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DriverDashboardUIState>(DriverDashboardUIState.Loading)
    val uiState: StateFlow<DriverDashboardUIState> = _uiState

    fun loadAssignedParcels(driverId: String) {
        _uiState.value = DriverDashboardUIState.Loading
        viewModelScope.launch {
            try {
                val parcels = repository.getAssignedParcels(driverId)
                _uiState.value = DriverDashboardUIState.Success(parcels)
            } catch (e: Exception) {
                _uiState.value = DriverDashboardUIState.Error(e.message ?: "Failed to load parcels")
            }
        }
    }
}

class DriverDashboardViewModelFactory(private val repository: ParcelRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriverDashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DriverDashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}