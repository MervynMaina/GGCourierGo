package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mervyn.ggcouriergo.models.DriverShiftUIState
import com.mervyn.ggcouriergo.repository.DriverShiftRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DriverShiftViewModel(private val repository: DriverShiftRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DriverShiftUIState>(DriverShiftUIState.Loading)
    val uiState: StateFlow<DriverShiftUIState> = _uiState

    // Job for updating the running shift timer in the UI
    private var timerJob: Job? = null

    init {
        // Start listening to the shift status from the repository
        repository.getShiftStatus()
            .onEach { shift ->
                _uiState.value = DriverShiftUIState.Success(shift)
                manageTimer(shift.isActive)
            }
            .catch { e ->
                _uiState.value = DriverShiftUIState.Error(e.message ?: "Failed to load shift status.")
            }
            .launchIn(viewModelScope)
    }

    private fun manageTimer(isActive: Boolean) {
        if (isActive) {
            // Start the timer job if not already running
            if (timerJob == null || timerJob?.isActive == false) {
                timerJob = viewModelScope.launch {
                    while (true) {
                        delay(1000L) // Wait 1 second
                        if (_uiState.value is DriverShiftUIState.Success) {
                            // Re-assign the current state to trigger a recomposition in the UI
                            // necessary to update the live running clock time.
                            _uiState.value = (_uiState.value as DriverShiftUIState.Success).copy(shift = (_uiState.value as DriverShiftUIState.Success).shift)
                        }
                    }
                }
            }
        } else {
            // Cancel the timer job when clocked out
            timerJob?.cancel()
            timerJob = null
        }
    }

    fun toggleShift() {
        val currentShift = (_uiState.value as? DriverShiftUIState.Success)?.shift ?: return

        // Ideally, temporarily set to loading state here
        viewModelScope.launch {
            repository.toggleShift(currentShift)
            // No need to manually update state; the Firebase snapshot listener handles the refresh.
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel() // Ensure the coroutine is cancelled when the ViewModel is destroyed
    }
}

class DriverShiftViewModelFactory(private val repository: DriverShiftRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriverShiftViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DriverShiftViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}