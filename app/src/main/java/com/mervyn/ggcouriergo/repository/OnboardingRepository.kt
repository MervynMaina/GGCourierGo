package com.mervyn.ggcouriergo.repository

import kotlinx.coroutines.delay

class OnboardingRepository {

    // Simulate saving onboarding completion flag
    suspend fun completeOnboarding(userId: String) {
        // Could write to Firestore or DataStore
        delay(500) // simulate network/database write
    }
}
