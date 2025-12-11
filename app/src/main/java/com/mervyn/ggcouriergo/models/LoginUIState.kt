package com.mervyn.ggcouriergo.models

sealed class LoginUIState {
    object Idle : LoginUIState()
    object Loading : LoginUIState()
    data class Error(val message: String) : LoginUIState()
    object SuccessDriver : LoginUIState()
    object SuccessDispatcher : LoginUIState()
    object SuccessAdmin : LoginUIState()
}