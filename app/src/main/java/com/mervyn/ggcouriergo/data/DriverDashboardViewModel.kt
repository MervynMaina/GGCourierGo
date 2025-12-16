package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.DriverDashboardUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the Driver Dashboard.
 * Uses a reactive Flow to listen for parcels assigned to a specific driver.
 */
class DriverDashboardViewModel(private val repository: ParcelRepository) : ViewModel() {

    // Trigger for the driverId. Once set, the uiState flow will start.
    private val _driverId = MutableStateFlow<String?>(null)

    /**
     * The UI State stream.
     * It reacts to the _driverId and fetches data in real-time.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DriverDashboardUIState> = _driverId
        .filterNotNull()
        .flatMapLatest { id ->
            // Calling the flow version in repository (make sure to add this to ParcelRepository)
            repository.getDriverParcelsFlow(id)
                .map { parcels ->
                    DriverDashboardUIState.Success(parcels) as DriverDashboardUIState
                }
                .onStart { emit(DriverDashboardUIState.Loading) }
                .catch { e ->
                    emit(DriverDashboardUIState.Error(e.message ?: "Failed to load assigned parcels"))
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DriverDashboardUIState.Loading
        )

    /**
     * Call this when the driver logs in or the screen opens to start data sync.
     */
    fun setDriverId(id: String) {
        _driverId.value = id
    }

    /**
     * Action for the driver to update parcel status (e.g., "picked_up" or "delivered")
     */
    fun updateParcelStatus(parcelId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateParcelStatus(parcelId, newStatus)
        }
    }
}

/**
 * Factory for DriverDashboardViewModel
 */
class DriverDashboardViewModelFactory(private val repository: ParcelRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriverDashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DriverDashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}