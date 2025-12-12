package com.mervyn.ggcouriergo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.ui.screens.admin.AdminDashboardScreen
import com.mervyn.ggcouriergo.ui.screens.auth.DispatcherDashboardScreen
import com.mervyn.ggcouriergo.ui.screens.auth.LoginScreen
import com.mervyn.ggcouriergo.ui.screens.auth.RegisterScreen
import com.mervyn.ggcouriergo.ui.screens.auth.SplashScreen
import com.mervyn.ggcouriergo.ui.screens.auth.OnboardingScreen
import com.mervyn.ggcouriergo.ui.screens.dispatcher.CreateParcelScreen
import com.mervyn.ggcouriergo.ui.screens.dispatcher.ParcelDetailsScreen
import com.mervyn.ggcouriergo.ui.screens.driver.*
import com.mervyn.ggcouriergo.ui.screens.delivery.DeliveryDetailsScreen
import com.mervyn.ggcouriergo.ui.screens.profile.ProfileScreen
import com.mervyn.ggcouriergo.ui.screens.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ROUT_SPLASH_SCREEN,
        modifier = modifier
    ) {

        // ---------------------------------------------------
        // Splash / Onboarding / Auth
        // ---------------------------------------------------
        composable(ROUT_SPLASH_SCREEN) { SplashScreen(navController) }
        composable(ROUT_ONBOARDING) { OnboardingScreen(navController) }
        composable(ROUT_LOGIN) { LoginScreen(navController) }
        composable(ROUT_REGISTER) { RegisterScreen(navController) }

        // ---------------------------------------------------
        // Dashboards
        // ---------------------------------------------------
        composable(ROUT_DRIVER_DASHBOARD) { DriverDashboardScreen(navController) }
        composable(ROUT_DISPATCHER_DASHBOARD) { DispatcherDashboardScreen(navController) }
        composable(ROUT_ADMIN_DASHBOARD) { AdminDashboardScreen(navController) }

        // ---------------------------------------------------
        // Parcel / Delivery Screens
        // ---------------------------------------------------
        composable(ROUT_CREATE_PARCEL) { CreateParcelScreen(navController) }

        // Parcel details (dispatcher or admin)
        composable("$ROUT_PARCEL_DETAILS/{parcelId}") { backStackEntry ->
            val parcelId = backStackEntry.arguments?.getString("parcelId") ?: "unknown"
            ParcelDetailsScreen(navController, parcelId)
        }

        // Driver parcel details
        composable("$ROUT_DRIVER_PARCEL_DETAILS/{parcelId}") { backStackEntry ->
            val parcelId = backStackEntry.arguments?.getString("parcelId") ?: "unknown"
            DriverParcelDetailsScreen(navController, parcelId)
        }

        // Delivery details (correct parameter = deliveryId)
        composable("$ROUT_DELIVERY_DETAILS/{deliveryId}") { backStackEntry ->
            val deliveryId = backStackEntry.arguments?.getString("deliveryId") ?: "unknown"
            DeliveryDetailsScreen(navController, deliveryId)
        }

        composable("$ROUT_DELIVERY_SUMMARY/{parcelId}") { backStackEntry ->
            val parcelId = backStackEntry.arguments?.getString("parcelId") ?: "unknown"
            DeliverySummaryScreen(navController, parcelId)
        }

        // ---------------------------------------------------
        // Driver shift
        // ---------------------------------------------------
        composable(ROUT_DRIVER_SHIFT) { DriverShiftScreen(navController) }

        // ---------------------------------------------------
        // Profile & Settings
        // ---------------------------------------------------
        composable(ROUT_PROFILE) { ProfileScreen(navController) }
        composable(ROUT_SETTINGS) { SettingsScreen(navController) }

        // ---------------------------------------------------
        // Tracking
        // ---------------------------------------------------
        composable("$ROUT_TRACKING/{parcelId}") { backStackEntry ->
            val parcelId = backStackEntry.arguments?.getString("parcelId") ?: "unknown"
            TrackingScreen(navController, parcelId)
        }
    }
}
