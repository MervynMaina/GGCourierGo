package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mervyn.ggcouriergo.models.ParcelTracking
import com.mervyn.ggcouriergo.models.ParcelTrackingUIState
import com.mervyn.ggcouriergo.repository.ParcelTrackingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParcelTrackingViewModel(private val repository: ParcelTrackingRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ParcelTrackingUIState>(ParcelTrackingUIState.Idle)
    val uiState: StateFlow<ParcelTrackingUIState> = _uiState.asStateFlow()

    private val _parcelIdInput = MutableStateFlow("")
    val parcelIdInput: StateFlow<String> = _parcelIdInput.asStateFlow()

    // --- NEW: History State ---
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    init {
        fetchHistory()
    }

    fun updateParcelIdInput(newInput: String) {
        _parcelIdInput.value = newInput
        if (newInput.isBlank()) {
            _uiState.value = ParcelTrackingUIState.Idle
        }
    }

    private fun fetchHistory() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            // This calls a new method we discussed for the repository
            repository.getTrackingHistory(currentUserId).collect { historyList ->
                _searchHistory.value = historyList
            }
        }
    }

    fun trackParcel(parcelId: String) {
        if (parcelId.isBlank()) {
            _uiState.value = ParcelTrackingUIState.Error("Please enter a valid Parcel ID.")
            return
        }

        _uiState.value = ParcelTrackingUIState.Loading
        viewModelScope.launch {
            try {
                val trackingParcel = repository.getTrackingParcel(parcelId)

                if (trackingParcel != null) {
                    _uiState.value = ParcelTrackingUIState.Success(trackingParcel)

                    // --- NEW: Save to Firebase History on Success ---
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    if (currentUserId != null) {
                        repository.saveToHistory(currentUserId, parcelId)
                    }
                } else {
                    _uiState.value = ParcelTrackingUIState.Error("Parcel ID '$parcelId' not found.")
                }
            } catch (e: Exception) {
                _uiState.value = ParcelTrackingUIState.Error("A network error occurred: ${e.message}")
            }
        }
    }

    // Optional: Function to clear history from the UI
    fun clearHistory() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            repository.clearHistory(currentUserId)
        }
    }
}

class ParcelTrackingViewModelFactory(private val repository: ParcelTrackingRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParcelTrackingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParcelTrackingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}