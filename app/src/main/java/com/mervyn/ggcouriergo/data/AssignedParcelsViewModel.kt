package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.AssignedParcelsUIState
import com.mervyn.ggcouriergo.repository.ParcelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AssignedParcelsViewModel(private val repository: ParcelRepository) : ViewModel() {

    // Note: We use a StateFlow for the list of ALL assigned parcels (not driver specific)
    private val _uiState = MutableStateFlow<AssignedParcelsUIState>(AssignedParcelsUIState.Loading)
    val uiState: StateFlow<AssignedParcelsUIState> = _uiState

    init {
        // Load initial data when ViewModel is created
        loadAssignedParcels()
    }

    // Since this is the Dispatcher's view, we want ALL assigned parcels, regardless of driver.
    // We achieve this by querying the repository for parcels where 'assignedDriver' is NOT null
    // (This requires a new method in ParcelRepository)
    fun loadAssignedParcels() {
        _uiState.value = AssignedParcelsUIState.Loading
        viewModelScope.launch {
            try {
                // The repository method must be able to fetch parcels assigned to ANY driver
                val parcels = repository.getAllAssignedParcels()
                _uiState.value = AssignedParcelsUIState.Success(parcels)
            } catch (e: Exception) {
                _uiState.value = AssignedParcelsUIState.Error(e.message ?: "Failed to load assigned parcels.")
            }
        }
    }
}

class AssignedParcelsViewModelFactory(private val repository: ParcelRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssignedParcelsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AssignedParcelsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}