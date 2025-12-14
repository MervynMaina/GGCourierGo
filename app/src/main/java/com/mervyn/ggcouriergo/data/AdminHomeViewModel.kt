package com.mervyn.ggcouriergo.data

import AdminDashboardData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.AdminHomeUIState
import com.mervyn.ggcouriergo.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminHomeViewModel(private val repository: AdminRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminHomeUIState>(AdminHomeUIState.Idle)
    val uiState: StateFlow<AdminHomeUIState> = _uiState

    init {
        fetchDashboardData()
    }

    fun fetchDashboardData() {
        _uiState.value = AdminHomeUIState.Loading

        viewModelScope.launch {
            val result = repository.fetchDashboardData()

            _uiState.value = result.fold(
                onSuccess = { (users, parcels) ->
                    // Calculate Analytics
                    val deliveredParcels = parcels.count { it.status.lowercase() == "delivered" }
                    val totalParcels = parcels.size
                    val pendingParcels = totalParcels - deliveredParcels

                    val data = AdminDashboardData(
                        users = users,
                        parcels = parcels,
                        totalParcels = totalParcels,
                        deliveredParcels = deliveredParcels,
                        pendingParcels = pendingParcels
                    )
                    AdminHomeUIState.Success(data)
                },
                onFailure = { e ->
                    AdminHomeUIState.Error(e.message ?: "Failed to load dashboard data.")
                }
            )
        }
    }
}

class AdminHomeViewModelFactory(private val repository: AdminRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminHomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminHomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}