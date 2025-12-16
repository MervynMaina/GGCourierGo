package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mervyn.ggcouriergo.navigation.ROUT_LOGIN
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    // A SharedFlow for one-time events like showing a Snackbar
    private val _themeEvent = MutableSharedFlow<String>()
    val themeEvent: SharedFlow<String> = _themeEvent.asSharedFlow()

    fun toggleDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        viewModelScope.launch {
            val mode = if (enabled) "Dark Mode" else "Light Mode"
            _themeEvent.emit("$mode Enabled")
        }
    }

    fun logout(navController: androidx.navigation.NavController) {
        auth.signOut()
        navController.navigate(ROUT_LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }
}