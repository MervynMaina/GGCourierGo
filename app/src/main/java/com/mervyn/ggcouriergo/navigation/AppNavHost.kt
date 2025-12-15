package com.mervyn.ggcouriergo.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.ui.screens.admin.AdminHomeScreen
import com.mervyn.ggcouriergo.ui.screens.dispatcher.DispatcherDashboardScreen
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
// IMPORT THE FINAL USER SCREEN
import com.mervyn.ggcouriergo.ui.screens.driver.UserDashboardScreen
import com.mervyn.ggcouriergo.ui.screens.main.MainAppScaffold

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
        // Global App Scaffold (The true landing page after auth)
        // ---------------------------------------------------
        composable(ROUT_MAIN_APP) { MainAppScaffold(navController) }

        // ---------------------------------------------------
        // Dashboards (Role-Based Entry Points)
        // ---------------------------------------------------
        composable(ROUT_DRIVER_DASHBOARD) { DriverDashboardScreen(navController) }
        composable(ROUT_DISPATCHER_DASHBOARD) { DispatcherDashboardScreen(navController) }
        composable(ROUT_ADMIN_DASHBOARD) { AdminHomeScreen(
            navController,
            Modifier.fillMaxSize()
        ) }
        // NEW: Add the main customer entry point (User Dashboard)
        composable(ROUT_USER_DASHBOARD) { UserDashboardScreen(navController) }


        // ---------------------------------------------------
        // Parcel / Delivery Screens
        // ---------------------------------------------------
        composable(ROUT_CREATE_PARCEL) { CreateParcelScreen(navController) }

        // Parcel details (dispatcher or admin)
        composable("$ROUT_PARCEL_DETAILS/{parcelId}") { backStackEntry ->
            val parcelId = backStackEntry.arguments?.getString("parcelId") ?: "unknown"
            ParcelDetailsScreen(navController, parcelId)
        }

        // Driver parcel details (P3.2)
        composable("$ROUT_DRIVER_PARCEL_DETAILS/{parcelId}") { backStackEntry ->
            val parcelId = backStackEntry.arguments?.getString("parcelId") ?: "unknown"
            DriverParcelDetailsScreen(navController, parcelId)
        }

        // CRITICAL ADDITION: The new List Route (used for the bottom tab click)
        composable(ROUT_PARCEL_LIST) {
            // Since the 'Parcels' tab is used by Dispatcher,
            // it should route to the screen that contains the Dispatcher's parcel views (New/Assigned).

            // This assumes DispatcherDashboardScreen is the entry point for the Dispatcher's content,
            // which then handles the internal tabs (New, Assigned, Map).
            DispatcherDashboardScreen(navController = navController)
        }

        // Delivery summary (P3.3)
        composable("$ROUT_DELIVERY_SUMMARY/{parcelId}") { backStackEntry ->
            val parcelId = backStackEntry.arguments?.getString("parcelId") ?: "unknown"
            DeliverySummaryScreen(navController, parcelId)
        }

        // Generic Delivery details (retaining for other uses)
        composable("$ROUT_DELIVERY_DETAILS/{deliveryId}") { backStackEntry ->
            val deliveryId = backStackEntry.arguments?.getString("deliveryId") ?: "unknown"
            DeliveryDetailsScreen(navController, deliveryId)
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
        // Tracking (P4.1)
        // ---------------------------------------------------
        // FIX: The UserDashboardScreen handles the tracking input, so we use the non-parameterized route.
        // If the route must be parameterized, we link to the UserDashboard.
        composable(ROUT_TRACKING) { UserDashboardScreen(navController) }
        // Note: The previous parameterized tracking route is removed as the UserDashboard handles the input.
        // If you need a deep link, the Tracking route should be used without parameters in the Composable call.
    }
}