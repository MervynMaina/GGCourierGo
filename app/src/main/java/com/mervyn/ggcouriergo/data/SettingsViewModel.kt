package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    // In a real app, this would load from DataStore or SharedPreferences
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun toggleDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        // TODO: In a real app, save this state persistently (DataStore) and
        // notify the main activity/host composable to change the theme.
    }

    fun logout(navController: androidx.navigation.NavController) {
        auth.signOut()
        navController.navigate("login") {
            // Clear the back stack to prevent returning to the app after logging out
            popUpTo("login") { inclusive = true }
        }
    }
}