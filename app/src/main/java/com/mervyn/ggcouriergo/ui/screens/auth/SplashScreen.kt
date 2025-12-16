package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.SplashViewModel
import com.mervyn.ggcouriergo.data.SplashViewModelFactory
import com.mervyn.ggcouriergo.models.SplashUIState
import com.mervyn.ggcouriergo.models.UserRole
import com.mervyn.ggcouriergo.repository.SplashRepository
import com.mervyn.ggcouriergo.repository.OnboardingRepository
import com.mervyn.ggcouriergo.ui.components.GGCourierLogo
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import com.mervyn.ggcouriergo.ui.theme.GGColors

/**
 * The entry point of the Green Giant app.
 * Handles the logic for routing users based on:
 * 1. Authentication Status (Firebase)
 * 2. Role (Admin, Driver, Dispatcher)
 * 3. First Time Status (Onboarding)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = viewModel(
        factory = SplashViewModelFactory(SplashRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Check local persistence for onboarding status
    val onboardingRepo = remember { OnboardingRepository(context) }

    // Floating Logo Animation (Moves up and down gently)
    val infiniteTransition = rememberInfiniteTransition(label = "logoFloat")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "yOffset"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is SplashUIState.Loading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // The Brand Logo with floating animation
                        GGCourierLogo(
                            modifier = Modifier
                                .size(130.dp)
                                .graphicsLayer(translationY = offsetY),
                            color = Color.White
                        )

                        Spacer(Modifier.height(32.dp))

                        Text(
                            text = "GREEN GIANT",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 4.sp,
                                fontSize = 32.sp
                            )
                        )
                        Text(
                            text = "COURIER GO",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White.copy(alpha = 0.7f),
                                letterSpacing = 8.sp,
                                fontSize = 16.sp
                            )
                        )

                        Spacer(Modifier.height(48.dp))

                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                is SplashUIState.Success -> {
                    LaunchedEffect(Unit) {
                        when (state.role) {
                            UserRole.ADMIN -> {
                                navController.navigate("adminhome") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                            UserRole.DISPATCHER -> {
                                navController.navigate("dispatcher_dashboard") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                            UserRole.DRIVER -> {
                                navController.navigate("driver_dashboard") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                            UserRole.NEW_USER -> {
                                // FIRST LAUNCH CHECK:
                                // If repo says they haven't finished onboarding, go there.
                                // If they have, but aren't logged in, go to Login.
                                if (onboardingRepo.isOnboardingCompleted()) {
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("onboarding") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                }

                is SplashUIState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Connection Failed",
                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = state.message,
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.retryCheckUserRole() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        ) {
                            Text("RETRY CONNECTION", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    GGCourierGoTheme {
        SplashScreen(rememberNavController())
    }
}