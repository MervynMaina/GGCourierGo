package com.mervyn.ggcouriergo.models

sealed class OnboardingUIState {
    object Loading : OnboardingUIState()
    object Finished : OnboardingUIState()
}
