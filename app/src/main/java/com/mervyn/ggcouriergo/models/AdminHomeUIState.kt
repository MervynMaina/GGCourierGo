package com.mervyn.ggcouriergo.models

import AdminDashboardData

sealed class AdminHomeUIState {
    object Idle : AdminHomeUIState()
    object Loading : AdminHomeUIState()
    data class Success(val data: AdminDashboardData) : AdminHomeUIState()
    data class Error(val message: String) : AdminHomeUIState()
}