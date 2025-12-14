package com.mervyn.ggcouriergo.models

sealed class LoginUIState {
    object Idle : LoginUIState()
    object Loading : LoginUIState()
    data class Error(val message: String) : LoginUIState()

    // Success states (Trigger Navigation in LoginScreen)
    object SuccessDriver : LoginUIState()
    object SuccessDispatcher : LoginUIState()
    object SuccessAdmin : LoginUIState()

    // Password Reset States
    object PasswordResetSent: LoginUIState()
    data class PasswordResetError(val message: String): LoginUIState()
}