package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.SplashViewModel
import com.mervyn.ggcouriergo.data.SplashViewModelFactory
import com.mervyn.ggcouriergo.models.SplashUIState
import com.mervyn.ggcouriergo.models.UserRole
import com.mervyn.ggcouriergo.repository.SplashRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import com.mervyn.ggcouriergo.ui.theme.GGColors // Import for direct color access (e.g., GraySurface)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = viewModel(
        factory = SplashViewModelFactory(SplashRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState(initial = SplashUIState.Loading)

    // Use Surface and set the background color to the primary brand green
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is SplashUIState.Loading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Branding: "GG" Logo Placeholder
                        Text(
                            text = "GG",
                            style = MaterialTheme.typography.displayLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimary // White text
                            ),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        // Branding: App Name
                        Text(
                            text = "GreenGiant Courier Go",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimary // White text
                            )
                        )
                        Spacer(Modifier.height(32.dp))

                        // Loading Indicator (using White color for contrast)
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 4.dp
                        )
                    }
                }

                is SplashUIState.Success -> {
                    // CRITICAL FLOW FIX: LaunchedEffect keyed on Unit ensures navigation only happens once.
                    LaunchedEffect(Unit) {
                        when ((uiState as SplashUIState.Success).role) {
                            UserRole.ADMIN -> navController.navigate("adminhome") { popUpTo("splash") { inclusive = true } }
                            UserRole.DISPATCHER -> navController.navigate("dispatcher_dashboard") { popUpTo("splash") { inclusive = true } }
                            UserRole.DRIVER -> navController.navigate("driver_dashboard") { popUpTo("splash") { inclusive = true } }
                            UserRole.NEW_USER -> navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } }
                        }
                    }
                }

                is SplashUIState.Error -> {
                    // Themed Error state
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Connection Failed",
                            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onPrimary),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = (uiState as SplashUIState.Error).message,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { viewModel.retryCheckUserRole() },
                            // Use GraySurface for the button background to stand out on the green splash background
                            colors = ButtonDefaults.buttonColors(containerColor = GGColors.GraySurface)
                        ) {
                            Text("RETRY", color = MaterialTheme.colorScheme.primary) // Green text on gray button
                        }
                    }
                }
            }
        }
    }
}

// Simplified Preview Function for design-time checking
@Preview(showBackground = true, name = "Splash Screen - Loading")
@Composable
fun SimpleSplashScreenPreview() {
    val navController = rememberNavController()
    // Mock a simple ViewModel that always shows the Loading state
    val mockViewModel = object : SplashViewModel(SplashRepository()) {
        init { _uiState.value = SplashUIState.Loading }
        override fun retryCheckUserRole() {}
    }

    // Ensure the unified GGCourierGoTheme is applied
    GGCourierGoTheme {
        SplashScreen(
            navController = navController,
            viewModel = mockViewModel
        )
    }
}