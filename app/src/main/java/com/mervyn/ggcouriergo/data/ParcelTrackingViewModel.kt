package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.ParcelTracking
import com.mervyn.ggcouriergo.models.ParcelTrackingUIState
import com.mervyn.ggcouriergo.repository.ParcelTrackingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParcelTrackingViewModel(private val repository: ParcelTrackingRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ParcelTrackingUIState>(ParcelTrackingUIState.Idle)
    val uiState: StateFlow<ParcelTrackingUIState> = _uiState.asStateFlow()

    private val _parcelIdInput = MutableStateFlow("")
    val parcelIdInput: StateFlow<String> = _parcelIdInput.asStateFlow()

    fun updateParcelIdInput(newInput: String) {
        _parcelIdInput.value = newInput
        // Clear state if the user deletes the input
        if (newInput.isBlank()) {
            _uiState.value = ParcelTrackingUIState.Idle
        }
    }

    fun trackParcel(parcelId: String) {
        if (parcelId.isBlank()) {
            _uiState.value = ParcelTrackingUIState.Error("Please enter a valid Parcel ID.")
            return
        }

        _uiState.value = ParcelTrackingUIState.Loading
        viewModelScope.launch {
            try {
                // FIX: Removed the incorrect manual mapping logic.
                // The repository method is trusted to return the final ParcelTracking object.
                val trackingParcel = repository.getTrackingParcel(parcelId)

                if (trackingParcel != null) {
                    _uiState.value = ParcelTrackingUIState.Success(trackingParcel)
                } else {
                    _uiState.value = ParcelTrackingUIState.Error("Parcel ID '$parcelId' not found.")
                }
            } catch (e: Exception) {
                _uiState.value = ParcelTrackingUIState.Error("A network error occurred: ${e.message}")
            }
        }
    }
}

class ParcelTrackingViewModelFactory(private val repository: ParcelTrackingRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParcelTrackingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParcelTrackingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}