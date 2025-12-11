package com.mervyn.ggcouriergo.models

sealed class CreateParcelUIState {
    object Idle : CreateParcelUIState()
    object Loading : CreateParcelUIState()
    object Success : CreateParcelUIState()
    data class Error(val message: String) : CreateParcelUIState()
}