package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.DriverParcelDetailsUIState
import com.mervyn.ggcouriergo.repository.DriverParcelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DriverParcelDetailsViewModel(private val repository: DriverParcelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DriverParcelDetailsUIState>(DriverParcelDetailsUIState.Loading)
    val uiState: StateFlow<DriverParcelDetailsUIState> = _uiState

    fun loadParcel(parcelId: String) {
        _uiState.value = DriverParcelDetailsUIState.Loading
        viewModelScope.launch {
            val parcel = repository.getParcel(parcelId)
            _uiState.value = if (parcel != null)
                DriverParcelDetailsUIState.Success(parcel)
            else
                DriverParcelDetailsUIState.Error("Parcel not found")
        }
    }

    fun updateStatus(parcelId: String, newStatus: String, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            val success = repository.updateStatus(parcelId, newStatus)
            if (success) loadParcel(parcelId)
            onComplete?.invoke()
        }
    }
}

class DriverParcelDetailsViewModelFactory(private val repository: DriverParcelRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriverParcelDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DriverParcelDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
