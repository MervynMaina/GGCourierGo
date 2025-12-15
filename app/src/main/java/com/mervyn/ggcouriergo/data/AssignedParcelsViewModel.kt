package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.AssignedParcelsUIState
import com.mervyn.ggcouriergo.repository.ParcelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch      // NEW: Import for error handling
import kotlinx.coroutines.flow.launchIn  // NEW: Import to start flow collection
import kotlinx.coroutines.flow.onEach    // NEW: Import to handle flow emissions

class AssignedParcelsViewModel(private val repository: ParcelRepository) : ViewModel() {

    // Note: We use a StateFlow for the list of ALL assigned parcels (not driver specific)
    private val _uiState = MutableStateFlow<AssignedParcelsUIState>(AssignedParcelsUIState.Loading)
    val uiState: StateFlow<AssignedParcelsUIState> = _uiState

    init {
        // CRITICAL FIX: Collect the real-time Flow in the init block
        repository.getAllAssignedParcelsFlow() // <- NOW CALLING THE REAL-TIME FLOW FUNCTION
            .onEach { parcels ->
                // Update the state immediately on new data
                _uiState.value = AssignedParcelsUIState.Success(parcels)
            }
            .catch { e ->
                // Handle errors from the real-time stream
                _uiState.value = AssignedParcelsUIState.Error(e.message ?: "Failed to load assigned parcels.")
            }
            // Start the collector and tie its lifecycle to the ViewModel
            .launchIn(viewModelScope)
    }

    // REMOVED: The manual loadAssignedParcels() function is no longer necessary!
    // The Flow in the init block handles all loading and refreshing.
}

class AssignedParcelsViewModelFactory(private val repository: ParcelRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssignedParcelsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AssignedParcelsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}