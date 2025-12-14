package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.DriverStatus
import com.mervyn.ggcouriergo.models.ParcelDetailsData
import com.mervyn.ggcouriergo.models.ParcelDetailsUIState
import com.mervyn.ggcouriergo.repository.DriverRepository
import com.mervyn.ggcouriergo.repository.ParcelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ParcelDetailsViewModel(
    private val parcelRepository: ParcelRepository,
    private val driverRepository: DriverRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ParcelDetailsUIState>(ParcelDetailsUIState.Idle)
    val uiState: StateFlow<ParcelDetailsUIState> = _uiState

    fun loadParcelDetails(parcelId: String) {
        _uiState.value = ParcelDetailsUIState.Loading
        viewModelScope.launch {
            try {
                // 1. Fetch the target parcel
                val parcel = parcelRepository.getParcel(parcelId)

                // 2. Fetch the list of available drivers
                val drivers = driverRepository.getAvailableDrivers()

                if (parcel != null) {
                    _uiState.value = ParcelDetailsUIState.Success(
                        ParcelDetailsData(parcel = parcel, availableDrivers = drivers)
                    )
                } else {
                    _uiState.value = ParcelDetailsUIState.Error("Parcel ID not found: $parcelId")
                }
            } catch (e: Exception) {
                _uiState.value = ParcelDetailsUIState.Error(e.message ?: "Failed to load details.")
            }
        }
    }

    fun assignDriverToParcel(parcelId: String, driverId: String) {
        if (_uiState.value is ParcelDetailsUIState.Loading) return

        _uiState.value = ParcelDetailsUIState.Loading
        viewModelScope.launch {
            val success = parcelRepository.assignDriver(parcelId, driverId)

            if (success) {
                // Optionally update driver status to ON_DELIVERY here, if business logic dictates
                // driverRepository.updateDriverStatus(driverId, DriverStatus.ON_DELIVERY)
                _uiState.value = ParcelDetailsUIState.AssignmentSuccess
            } else {
                _uiState.value = ParcelDetailsUIState.Error("Failed to assign driver. Try again.")
            }
        }
    }
}

class ParcelDetailsViewModelFactory(
    private val parcelRepository: ParcelRepository,
    private val driverRepository: DriverRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParcelDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParcelDetailsViewModel(parcelRepository, driverRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}