package com.mervyn.ggcouriergo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mervyn.ggcouriergo.data.SettingsViewModel
import com.mervyn.ggcouriergo.navigation.AppNavHost
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 1. Initialize the SettingsViewModel
            val settingsViewModel: SettingsViewModel = viewModel()

            // 2. Observe the dark theme state
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            // 3. Pass the state into your Theme
            GGCourierGoTheme(darkTheme = isDarkTheme) {
                // 4. Surface ensures the background color changes correctly
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}