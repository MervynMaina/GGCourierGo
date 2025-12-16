package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.AssignedParcelsUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import kotlinx.coroutines.flow.*

class AssignedParcelsViewModel(private val repository: ParcelRepository) : ViewModel() {

    // Explicitly typing the Flow to AssignedParcelsUIState to resolve compiler errors
    val uiState: StateFlow<AssignedParcelsUIState> = repository.getAllAssignedParcelsFlow()
        .map { parcels: List<Parcel> ->
            AssignedParcelsUIState.Success(parcels) as AssignedParcelsUIState
        }
        .onStart {
            emit(AssignedParcelsUIState.Loading)
        }
        .catch { e ->
            emit(AssignedParcelsUIState.Error(e.message ?: "Failed to connect to logistics stream."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AssignedParcelsUIState.Loading
        )
}

class AssignedParcelsViewModelFactory(private val repository: ParcelRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssignedParcelsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AssignedParcelsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}