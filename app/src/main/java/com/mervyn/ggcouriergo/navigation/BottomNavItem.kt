package com.mervyn.ggcouriergo.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Defines the navigation items used in the Bottom Bar or Drawer.
 */
sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    // Shared Items
    object Profile : BottomNavItem("Profile", Icons.Filled.Person, ROUT_PROFILE)
    object Settings : BottomNavItem("Settings", Icons.Filled.Settings, ROUT_SETTINGS)

    // Role-Specific Dashboards (Used for main entry point)
    object DriverDashboard : BottomNavItem("Deliveries", Icons.Filled.DeliveryDining, ROUT_DRIVER_DASHBOARD)
    object DispatcherDashboard : BottomNavItem("Overview", Icons.Filled.Dashboard, ROUT_DISPATCHER_DASHBOARD)
    object UserTrackingDashboard : BottomNavItem("Track Parcel", Icons.Filled.TrackChanges, ROUT_TRACKING)

    // Role-Specific Features
    object DriverShift : BottomNavItem("Shift Manager", Icons.Filled.Schedule, ROUT_DRIVER_SHIFT)
    object CreateParcel : BottomNavItem("New Parcel", Icons.Filled.AddBox, ROUT_CREATE_PARCEL)
    object ParcelList : BottomNavItem("Parcels", Icons.AutoMirrored.Filled.List, ROUT_PARCEL_DETAILS)

    companion object {
        fun getItemsForRole(role: String): List<BottomNavItem> {
            return when (role.lowercase()) {
                "driver" -> listOf(
                    DriverDashboard,
                    DriverShift,
                    Profile,
                    Settings
                )
                "dispatcher" -> listOf(
                    DispatcherDashboard,
                    CreateParcel,
                    ParcelList,
                    Profile
                )
                "admin" -> listOf(
                    DispatcherDashboard,
                    ParcelList,
                    Settings
                    // Admin would likely navigate to AdminDashboard, but uses Dispatcher view for parcel handling
                )
                else -> listOf(
                    UserTrackingDashboard
                    // Anonymous users only see the tracking search field
                )
            }
        }
    }
}