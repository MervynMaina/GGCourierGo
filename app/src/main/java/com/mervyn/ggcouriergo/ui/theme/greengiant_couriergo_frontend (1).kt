// GreenGiant CourierGo - Frontend Skeleton
// Jetpack Compose + Navigation + Basic Screens

package com.greengiant.couriergo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CourierGoApp()
        }
    }
}

@Composable
fun CourierGoApp() {
    val navController = rememberNavController()
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavigation(navController)
        }
    }
}

// ---------------------- Navigation ----------------------

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object DispatcherDashboard : Screen("dispatcher_dashboard")
    data object DriverDashboard : Screen("driver_dashboard")
    data object ParcelDetails : Screen("parcel_details/{parcelId}")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.DispatcherDashboard.route) { DispatcherDashboardScreen(navController) }
        composable(Screen.DriverDashboard.route) { DriverDashboardScreen(navController) }
        composable(Screen.ParcelDetails.route) { backStackEntry ->
            val parcelId = backStackEntry.arguments?.getString("parcelId") ?: ""
            ParcelDetailsScreen(parcelId)
        }
    }
}

// ---------------------- Screens ----------------------

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val nav = rememberNavController()
    LoginScreen(nav)
}

@Composable
fun LoginScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "GreenGiant CourierGo", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { navController.navigate(Screen.DispatcherDashboard.route) }) {
            Text("Login as Dispatcher")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { navController.navigate(Screen.DriverDashboard.route) }) {
            Text("Login as Driver")
        }
    }
}

@Composable
fun DispatcherDashboardScreen(navController: NavHostController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Dispatcher Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { /* Navigate to parcel creation */ }) {
            Text("Create New Parcel")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { navController.navigate(Screen.ParcelDetails.route.replace("{parcelId}", "123")) }) {
            Text("View Parcel #123")
        }
    }
}

@Composable
fun DriverDashboardScreen(navController: NavHostController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Driver Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { navController.navigate(Screen.ParcelDetails.route.replace("{parcelId}", "123")) }) {
            Text("Open Assigned Parcel")
        }
    }
}

@Composable
fun ParcelDetailsScreen(parcelId: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Parcel Details", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Parcel ID: $parcelId")
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { /* Mark delivered */ }) {
            Text("Mark as Delivered")
        }
    }
}

// ---------------------- Theme ----------------------

// Color definitions
object GGColors {
    val GreenPrimary = Color(0xFF1B8F3A)
    val GreenSecondary = Color(0xFF37C15A)
    val GreenDark = Color(0xFF0E6122)
    val GreenLight = Color(0xFFA9F0C1)
    val GrayBackground = Color(0xFFF5F5F5)
    val GraySurface = Color(0xFFEDEDED)
}

@Composable
fun CourierGoTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = GGColors.GreenPrimary,
        secondary = GGColors.GreenSecondary,
        background = GGColors.GrayBackground,
        surface = GGColors.GraySurface,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}

// ---------------------- Previews ----------------------

@Preview(showBackground = true)
@Composable
fun DispatcherDashboardPreview() {
    val nav = rememberNavController()
    DispatcherDashboardScreen(nav)
}

@Preview(showBackground = true)
@Composable
fun DriverDashboardPreview() {
    val nav = rememberNavController()
    DriverDashboardScreen(nav)
}

@Preview(showBackground = true)
@Composable
fun ParcelDetailsPreview() {
    ParcelDetailsScreen(parcelId = "123")
}
