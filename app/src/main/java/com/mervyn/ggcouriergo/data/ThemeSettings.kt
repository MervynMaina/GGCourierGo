package com.mervyn.ggcouriergo.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Explicitly define the extension property
private val Context.dataStore by preferencesDataStore(name = "settings")

class ThemeSettings(private val context: Context) {
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    // Explicitly mapping the flow to handle the nullable preference
    val darkModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    suspend fun toggleTheme() {
        context.dataStore.edit { preferences ->
            val current = preferences[DARK_MODE_KEY] ?: false
            preferences[DARK_MODE_KEY] = !current
        }
    }
}