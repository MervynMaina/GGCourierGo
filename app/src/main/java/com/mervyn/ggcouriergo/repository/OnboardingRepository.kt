package com.mervyn.ggcouriergo.repository

import kotlinx.coroutines.delay

class OnboardingRepository {

    // Simulating saving the onboarding completion flag locally.
    // This function should eventually write a simple boolean to DataStore or SharedPreferences.
    suspend fun completeOnboarding() {
        // Example: context.dataStore.edit { preferences -> preferences[ONBOARDING_KEY] = true }
        delay(500) // simulate local write delay
    }
}