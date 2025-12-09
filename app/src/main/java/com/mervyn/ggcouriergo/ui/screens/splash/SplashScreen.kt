package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.navigation.ROUT_DRIVER_DASHBOARD
import com.mervyn.ggcouriergo.navigation.ROUT_DISPATCHER_DASHBOARD
import com.mervyn.ggcouriergo.navigation.ROUT_ONBOARDING
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var isLoading by remember { mutableStateOf(true) }

    // Simple Splash / Delay + Role / Onboarding Check
    LaunchedEffect(Unit) {
        delay(1000L) // show splash for 1 second
        val user = auth.currentUser
        if (user == null) {
            // Not logged in → go to onboarding
            navController.navigate(ROUT_ONBOARDING) {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            // Logged in → fetch role and onboarding status
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    val role = doc.getString("role") ?: "driver"
                    val onboardingCompleted = doc.getBoolean("onboardingCompleted") ?: false
                    if (!onboardingCompleted) {
                        navController.navigate(ROUT_ONBOARDING) {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        if (role == "driver") navController.navigate(ROUT_DRIVER_DASHBOARD) {
                            popUpTo("splash") { inclusive = true }
                        } else navController.navigate(ROUT_DISPATCHER_DASHBOARD) {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
                .addOnFailureListener {
                    navController.navigate(ROUT_ONBOARDING) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
        }
        isLoading = false
    }

    // UI while checking
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                "Welcome to CourierGo",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSplashScreen() {
    val navController = rememberNavController() // Fake NavController for preview
    SplashScreen(navController = navController)
}
