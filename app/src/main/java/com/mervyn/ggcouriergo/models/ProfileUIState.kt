package com.mervyn.ggcouriergo.models

sealed class ProfileUIState {
    object Loading : ProfileUIState()
    data class Success(val profile: UserProfile) : ProfileUIState()
    data class Error(val message: String) : ProfileUIState()
}