package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mervyn.ggcouriergo.data.SplashViewModel
import com.mervyn.ggcouriergo.data.SplashViewModelFactory
import com.mervyn.ggcouriergo.repository.SplashRepository
import com.mervyn.ggcouriergo.models.SplashUIState
import com.mervyn.ggcouriergo.models.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(navController: NavController, viewModel: SplashViewModel = viewModel(
    factory = SplashViewModelFactory(SplashRepository())
)) {
    val uiState = viewModel.uiState.collectAsState().value

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
                    // Navigate according to role
                    LaunchedEffect(uiState) {
                        CoroutineScope(Dispatchers.Main).launch {
                            when (uiState.role) {
                                UserRole.ADMIN -> navController.navigate("home") { popUpTo("splash") { inclusive = true } }
                                UserRole.DISPATCHER -> navController.navigate("dispatcher_dashboard") { popUpTo("splash") { inclusive = true } }
                                UserRole.DRIVER -> navController.navigate("driver_dashboard") { popUpTo("splash") { inclusive = true } }
                                UserRole.NEW_USER -> navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } }
                            }
                        }
                    }
                }
                is SplashUIState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${uiState.message}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.checkUserRole() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}
