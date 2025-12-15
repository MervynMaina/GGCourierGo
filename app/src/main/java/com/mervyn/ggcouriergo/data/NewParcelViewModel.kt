package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.NewParcelsUIState
import com.mervyn.ggcouriergo.repository.ParcelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class NewParcelsViewModel(
    private val parcelRepository: ParcelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewParcelsUIState>(NewParcelsUIState.Loading)
    val uiState: StateFlow<NewParcelsUIState> = _uiState.asStateFlow()

    init {
        fetchNewParcels()
    }

    // In NewParcelsViewModel.kt

    fun fetchNewParcels() {
        viewModelScope.launch {
            _uiState.value = NewParcelsUIState.Loading
            try {
                // Load ALL parcels using the safe repository function
                val allParcels = parcelRepository.getAllParcelsForClientFilter()

                // 💥 FINAL, GUARANTEED FIX: Filter client-side to accept all "unassigned" states
                val unassignedParcels = allParcels.filter { parcel ->
                    // Check 1: Must be in 'pending' status (Primary filter)
                    parcel.status == "pending" &&

                            // Check 2: assignedDriver must be one of the known 'unassigned' markers
                            (parcel.assignedDriver == "UNASSIGNED" || // New parcels saved with placeholder
                                    parcel.assignedDriver == null ||        // Old parcels saved with true null
                                    parcel.assignedDriver.isNullOrBlank())  // Parcels saved with null/empty string
                }

                _uiState.value = NewParcelsUIState.Success(unassignedParcels)

            } catch (e: Exception) {
                _uiState.value = NewParcelsUIState.Error("Failed to load new parcels: ${e.message}")
            }
        }
    }

    // TODO: Add function for dispatchers to assign a driver to a parcel
    fun assignDriverToParcel(parcelId: String, driverId: String) {
        // This is where assignment logic would go, triggering a repository call and then a re-fetch.
        // For now, leave as placeholder.
    }
}

class NewParcelsViewModelFactory(
    private val parcelRepository: ParcelRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewParcelsViewModel::class.java)) {
            return NewParcelsViewModel(parcelRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}