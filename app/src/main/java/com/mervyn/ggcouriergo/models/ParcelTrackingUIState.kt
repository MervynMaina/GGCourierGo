package com.mervyn.ggcouriergo.models

sealed class ParcelTrackingUIState {
    // Initial state before any search or when search bar is empty
    object Idle : ParcelTrackingUIState()
    // State while waiting for data after search button press
    object Loading : ParcelTrackingUIState()
    // Success state, displaying the found tracking data
    data class Success(val parcel: ParcelTracking) : ParcelTrackingUIState()
    // Error state, including "Parcel not found"
    data class Error(val message: String) : ParcelTrackingUIState()
}