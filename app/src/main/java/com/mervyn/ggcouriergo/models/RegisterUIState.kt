package com.mervyn.ggcouriergo.models

sealed class RegisterUIState {
    object Idle: RegisterUIState()
    object Loading: RegisterUIState()
    object Success: RegisterUIState()
    data class Error(val message: String): RegisterUIState()
}