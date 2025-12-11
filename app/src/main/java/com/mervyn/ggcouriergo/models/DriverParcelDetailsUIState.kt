package com.mervyn.ggcouriergo.models

sealed class DriverParcelDetailsUIState {
    object Loading : DriverParcelDetailsUIState()
    data class Success(val parcel: DriverParcelDetails) : DriverParcelDetailsUIState()
    data class Error(val message: String) : DriverParcelDetailsUIState()
}