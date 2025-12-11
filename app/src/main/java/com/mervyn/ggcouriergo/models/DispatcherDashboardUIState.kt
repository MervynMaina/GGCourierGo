package com.mervyn.ggcouriergo.models

sealed class DispatcherDashboardUIState {
    object Idle : DispatcherDashboardUIState()
    object Loading : DispatcherDashboardUIState()
    data class Success(val parcels: List<Parcel>) : DispatcherDashboardUIState()
    data class Error(val message: String) : DispatcherDashboardUIState()
}