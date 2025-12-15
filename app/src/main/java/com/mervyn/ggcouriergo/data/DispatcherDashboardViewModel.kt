package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.DispatcherDashboardUIState
import com.mervyn.ggcouriergo.repository.ParcelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch // NEW: Import for error handling on flow
import kotlinx.coroutines.flow.launchIn // NEW: Import to start flow collection
import kotlinx.coroutines.flow.onEach   // NEW: Import to handle each flow emission
import kotlinx.coroutines.launch

open class DispatcherDashboardViewModel(private val repository: ParcelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DispatcherDashboardUIState>(DispatcherDashboardUIState.Loading)
    val uiState: StateFlow<DispatcherDashboardUIState> = _uiState

    init {
        // CRITICAL FIX: Collect the real-time Flow in the init block
        repository.getUnassignedParcelsFlow()
            .onEach { parcels ->
                // Update the state immediately on new data
                _uiState.value = DispatcherDashboardUIState.Success(parcels)
            }
            .catch { e ->
                // Handle errors from the real-time stream
                _uiState.value = DispatcherDashboardUIState.Error(e.message ?: "Failed to load parcels")
            }
            // Start the collector and tie its lifecycle to the ViewModel
            .launchIn(viewModelScope)
    }

    // REMOVED: open fun loadParcels() is no longer needed, as the Flow handles loading and refreshing

    // Updated assignDriver: No more manual loadParcels() call needed!
    fun assignDriver(parcelId: String, driverId: String) {
        // We do not set _uiState to Loading here because the Flow will handle the subsequent update
        viewModelScope.launch {
            // Write the assignment to Firestore
            val success = repository.assignDriver(parcelId, driverId)

            // If successful, the Firestore listener (Flow) will detect the change,
            // and the 'onEach' block above will refresh the UI automatically.
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