package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.CreateParcelUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateParcelViewModel(private val repository: ParcelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateParcelUIState>(CreateParcelUIState.Idle)
    val uiState: StateFlow<CreateParcelUIState> = _uiState

    fun createParcel(parcel: Parcel) {
        // --- INPUT VALIDATION (Added for robustness) ---
        if (parcel.senderName.isBlank() || parcel.receiverName.isBlank() ||
            parcel.pickupAddress.isBlank() || parcel.dropoffAddress.isBlank() ||
            parcel.packageDetails.isBlank()) {
            _uiState.value = CreateParcelUIState.Error("All fields (Names, Addresses, Details) must be filled.")
            return
        }
        // ----------------------------------------------

        _uiState.value = CreateParcelUIState.Loading
        viewModelScope.launch {
            try {
                val success = repository.addParcel(parcel.copy(status = "pending", createdAt = System.currentTimeMillis()))
                if (success) _uiState.value = CreateParcelUIState.Success
                else _uiState.value = CreateParcelUIState.Error("Failed to create parcel in database.")
            } catch (e: Exception) {
                _uiState.value = CreateParcelUIState.Error(e.message ?: "Network or internal error creating parcel.")
            }
        }
    }
}

class CreateParcelViewModelFactory(private val repository: ParcelRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateParcelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateParcelViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}