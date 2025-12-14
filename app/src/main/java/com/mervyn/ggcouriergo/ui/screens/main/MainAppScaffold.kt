package com.mervyn.ggcouriergo.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mervyn.ggcouriergo.navigation.BottomNavItem
import com.mervyn.ggcouriergo.ui.screens.admin.AdminHomeScreen
import com.mervyn.ggcouriergo.ui.screens.dispatcher.DispatcherDashboardScreen
import com.mervyn.ggcouriergo.ui.screens.driver.DriverDashboardScreen
import com.mervyn.ggcouriergo.ui.screens.driver.UserDashboardScreen
import com.mervyn.ggcouriergo.data.ProfileViewModel // Use to get current user/role
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState


/**
 * Master Scaffold that handles role-based navigation and UI structure.
 * This screen is the true landing page after authentication.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val role = (uiState as? com.mervyn.ggcouriergo.models.ProfileUIState.Success)?.profile?.role ?: "loading"

    val navItems = BottomNavItem.getItemsForRole(role)

    Scaffold(
        bottomBar = {
            if (navItems.isNotEmpty()) {
                BottomNavigationBar(navController, navItems)
            }
        }
    ) { paddingValues ->
        // Render the main dashboard content based on the user's role
        when (role.lowercase()) {
            "driver" -> DriverDashboardScreen(navController = navController, Modifier.padding(paddingValues))
            "dispatcher" -> DispatcherDashboardScreen(navController, Modifier.padding(paddingValues))
            "admin" -> AdminHomeScreen(navController, Modifier.padding(paddingValues))
            "customer" -> UserDashboardScreen(navController, Modifier.padding(paddingValues))
            "loading" -> Text("Loading profile and routing...", modifier = Modifier.padding(paddingValues))
            else -> Text("Access Denied or Unknown Role.", modifier = Modifier.padding(paddingValues))
        }
    }
}

/**
 * Reusable Bottom Navigation Bar for all roles.
 */
@Composable
fun BottomNavigationBar(navController: NavController, items: List<BottomNavItem>) {
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Avoid building up a large stack of destinations on the back stack as users select items
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}