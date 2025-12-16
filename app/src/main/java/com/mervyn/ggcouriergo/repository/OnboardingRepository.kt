package com.mervyn.ggcouriergo.repository

import android.content.Context
import kotlinx.coroutines.delay

class OnboardingRepository(context: Context) {
    // Shared preferences to store the persistent flag
    private val sharedPrefs = context.getSharedPreferences("gg_courier_prefs", Context.MODE_PRIVATE)

    /**
     * Checks if the user has already completed the onboarding process.
     */
    fun isOnboardingCompleted(): Boolean {
        return sharedPrefs.getBoolean("onboarding_completed", false)
    }

    /**
     * Saves the completion flag permanently to the device.
     */
    suspend fun completeOnboarding() {
        delay(500) // Aesthetic delay for smooth transition
        sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()
    }
}