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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = viewModel(
        factory = SplashViewModelFactory(SplashRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState(initial = SplashUIState.Loading)

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is SplashUIState.Loading -> {
                    CircularProgressIndicator()
                }
                is SplashUIState.Success -> {
                    LaunchedEffect(uiState) {
                        when ((uiState as SplashUIState.Success).role) {
                            UserRole.ADMIN -> navController.navigate("home") { popUpTo("splash") { inclusive = true } }
                            UserRole.DISPATCHER -> navController.navigate("dispatcher_dashboard") { popUpTo("splash") { inclusive = true } }
                            UserRole.DRIVER -> navController.navigate("driver_dashboard") { popUpTo("splash") { inclusive = true } }
                            UserRole.NEW_USER -> navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } }
                        }
                    }
                }
                is SplashUIState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Error: ${(uiState as SplashUIState.Error).message}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.retryCheckUserRole() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSplashScreenStates() {
    val navController = rememberNavController()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Loading State", style = MaterialTheme.typography.titleMedium)
        SplashScreen(
            navController = navController,
            viewModel = object : SplashViewModel(SplashRepository()) {
                init { _uiState.value = SplashUIState.Loading }
                override fun retryCheckUserRole() {}
            }
        )
        Spacer(Modifier.height(24.dp))

        Text("Success State", style = MaterialTheme.typography.titleMedium)
        SplashScreen(
            navController = navController,
            viewModel = object : SplashViewModel(SplashRepository()) {
                init { _uiState.value = SplashUIState.Success(UserRole.DRIVER) }
                override fun retryCheckUserRole() {}
            }
        )
        Spacer(Modifier.height(24.dp))

        Text("Error State", style = MaterialTheme.typography.titleMedium)
        SplashScreen(
            navController = navController,
            viewModel = object : SplashViewModel(SplashRepository()) {
                init { _uiState.value = SplashUIState.Error("Failed to fetch role") }
                override fun retryCheckUserRole() {}
            }
        )
    }
}
