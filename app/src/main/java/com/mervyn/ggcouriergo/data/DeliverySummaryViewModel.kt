package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.DeliverySummary
import com.mervyn.ggcouriergo.models.DeliverySummaryUIState
import com.mervyn.ggcouriergo.repository.DriverParcelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeliverySummaryViewModel(private val repository: DriverParcelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DeliverySummaryUIState>(DeliverySummaryUIState.Loading)
    val uiState: StateFlow<DeliverySummaryUIState> = _uiState

    fun loadSummary(parcelId: String) {
        _uiState.value = DeliverySummaryUIState.Loading
        viewModelScope.launch {
            try {
                // We use getParcel which returns DriverParcelDetails, and map it to DeliverySummary
                val parcel = repository.getParcel(parcelId)

                if (parcel != null && parcel.status == "delivered") {
                    val summary = DeliverySummary(
                        id = parcel.id,
                        pickupAddress = parcel.pickupAddress,
                        dropoffAddress = parcel.dropoffAddress,
                        receiverName = parcel.receiverName,
                        receiverPhone = parcel.receiverPhone,
                        packageDetails = parcel.packageDetails,
                        deliveredAt = parcel.deliveredAt,
                        deliveryPhotoUrl = parcel.deliveryPhotoUrl // Added field for POD
                    )
                    _uiState.value = DeliverySummaryUIState.Success(summary)
                } else {
                    _uiState.value = DeliverySummaryUIState.Error("Delivery summary not found or parcel status is not 'delivered'.")
                }
            } catch (e: Exception) {
                _uiState.value = DeliverySummaryUIState.Error(e.message ?: "Failed to load delivery summary.")
            }
        }
    }
}

class DeliverySummaryViewModelFactory(private val repository: DriverParcelRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeliverySummaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeliverySummaryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}