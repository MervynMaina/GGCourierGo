package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.DispatcherDashboardUIState
import com.mervyn.ggcouriergo.repository.ParcelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class DispatcherDashboardViewModel(private val repository: ParcelRepository) : ViewModel() {

    val _uiState = MutableStateFlow<DispatcherDashboardUIState>(DispatcherDashboardUIState.Loading)
    val uiState: StateFlow<DispatcherDashboardUIState> = _uiState

    open fun loadParcels() {
        _uiState.value = DispatcherDashboardUIState.Loading
        viewModelScope.launch {
            try {
                val parcels = repository.getUnassignedParcels()
                _uiState.value = DispatcherDashboardUIState.Success(parcels)
            } catch (e: Exception) {
                _uiState.value = DispatcherDashboardUIState.Error(e.message ?: "Failed to load parcels")
            }
        }
    }

    fun assignDriver(parcelId: String, driverId: String) {
        viewModelScope.launch {
            val success = repository.assignDriver(parcelId, driverId)
            if (success) loadParcels() // reload after assignment
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
