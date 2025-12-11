package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mervyn.ggcouriergo.models.AdminHomeUIState
import com.mervyn.ggcouriergo.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdminHomeViewModel(private val repository: AdminRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminHomeUIState>(AdminHomeUIState.Idle)
    val uiState: StateFlow<AdminHomeUIState> = _uiState

    // Example function to fetch stats
    fun fetchStats() {
        val stats = repository.getDashboardStats()
        // Could update _uiState if we create a data class to hold stats
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
