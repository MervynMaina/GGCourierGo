package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.NewParcelsUIState
import com.mervyn.ggcouriergo.repository.ParcelRepository
import kotlinx.coroutines.flow.*
import androidx.lifecycle.ViewModelProvider

class NewParcelsViewModel(private val repository: ParcelRepository) : ViewModel() {

    // FIX: Explicitly set the type to the Sealed Class NewParcelsUIState
    // to allow Loading, Success, and Error to coexist in the same stream.
    val uiState: StateFlow<NewParcelsUIState> = repository.getPendingParcelsFlow()
        .map { parcels ->
            // Explicitly cast to the sealed interface/class
            NewParcelsUIState.Success(parcels) as NewParcelsUIState
        }
        .onStart {
            emit(NewParcelsUIState.Loading)
        }
        .catch { e ->
            emit(NewParcelsUIState.Error(e.message ?: "Failed to connect to database"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NewParcelsUIState.Loading // This now matches the StateFlow type
        )
}

class NewParcelsViewModelFactory(private val repository: ParcelRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewParcelsViewModel::class.java)) {
            return NewParcelsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}