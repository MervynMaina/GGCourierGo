package com.mervyn.ggcouriergo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.ui.screens.auth.LoginScreen
import com.mervyn.ggcouriergo.ui.screens.auth.RegisterScreen
import com.mervyn.ggcouriergo.ui.screens.auth.SplashScreen
import com.mervyn.ggcouriergo.ui.screens.dispatcher.CreateParcelScreen
import com.mervyn.ggcouriergo.ui.screens.dispatcher.DispatcherDashboardScreen
import com.mervyn.ggcouriergo.ui.screens.delivery.DeliveryDetailsScreen
import com.mervyn.ggcouriergo.ui.screens.driver.DriverDashboardScreen
import com.mervyn.ggcouriergo.ui.screens.driver.DriverParcelDetailsScreen
import com.mervyn.ggcouriergo.ui.screens.dispatcher.ParcelDetailsScreen


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ROUT_SPlASH_SCREEN, // Splash handles role & onboarding
        modifier = modifier
    ) {

        // Splash / Role-based start
        composable("splash") {
            SplashScreen(navController) }

        // Auth Screens
        composable(ROUT_LOGIN) { LoginScreen(navController) }
        composable(ROUT_REGISTER) { RegisterScreen(navController) }

        // Dashboards
        composable(ROUT_DRIVER_DASHBOARD) { DriverDashboardScreen() }
        composable(ROUT_DISPATCHER_DASHBOARD) { DispatcherDashboardScreen() }

        // Parcel / Delivery Screens
        composable(ROUT_CREATE_PARCEL) { CreateParcelScreen(navController) }

        composable(ROUT_PARCEL_DETAILS) { backStackEntry ->
            val parcelId = backStackEntry.arguments?.getString("parcelId") ?: "unknown"
            ParcelDetailsScreen(navController, parcelId)
        }

        composable(ROUT_DRIVER_PARCEL_DETAILS) { backStackEntry ->
            val parcelId = backStackEntry.arguments?.getString("parcelId") ?: "unknown"
            DriverParcelDetailsScreen(navController, parcelId)
        }

        composable(ROUT_DELIVERY_DETAILS) { backStackEntry ->
            val deliveryId = backStackEntry.arguments?.getString("deliveryId") ?: "unknown"
            DeliveryDetailsScreen(navController, deliveryId)
        }
    }
}
