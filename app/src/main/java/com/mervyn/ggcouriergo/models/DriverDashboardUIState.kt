package com.mervyn.ggcouriergo.models

import com.mervyn.ggcouriergo.models.Parcel

sealed class DriverDashboardUIState {
    object Loading : DriverDashboardUIState()
    data class Success(val parcels: List<Parcel>) : DriverDashboardUIState()
    data class Error(val message: String) : DriverDashboardUIState()
}