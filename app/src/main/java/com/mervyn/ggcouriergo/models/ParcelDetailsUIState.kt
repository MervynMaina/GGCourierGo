package com.mervyn.ggcouriergo.models

// Data class to hold all necessary information for the Dispatcher screen
data class ParcelDetailsData(
    val parcel: Parcel,
    val availableDrivers: List<Driver>
)

sealed class ParcelDetailsUIState {
    object Loading : ParcelDetailsUIState()
    data class Success(val data: ParcelDetailsData) : ParcelDetailsUIState()
    data class Error(val message: String) : ParcelDetailsUIState()
    object AssignmentSuccess : ParcelDetailsUIState()
    object Idle : ParcelDetailsUIState()
}