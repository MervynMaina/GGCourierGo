package com.mervyn.ggcouriergo.models

sealed class SplashUIState {
    object Loading : SplashUIState()
    data class Success(val role: UserRole) : SplashUIState()
    data class Error(val message: String) : SplashUIState()
}

enum class UserRole {
    ADMIN,
    DISPATCHER,
    DRIVER,
    NEW_USER
}