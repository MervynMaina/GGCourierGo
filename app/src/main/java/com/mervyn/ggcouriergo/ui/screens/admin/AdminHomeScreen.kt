package com.mervyn.ggcouriergo.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.AdminHomeViewModel
import com.mervyn.ggcouriergo.data.AdminHomeViewModelFactory
import com.mervyn.ggcouriergo.models.* // Imports User, Parcel, AdminDashboardData
import com.mervyn.ggcouriergo.repository.AdminRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import com.mervyn.ggcouriergo.ui.theme.GGColors // Assuming GGColors contains necessary custom colors

// --------------------------------------------------
// NAVIGATION SETUP
// --------------------------------------------------

sealed class AdminNavScreen(val route: String, val title: String, val icon: ImageVector) {
    object Overview : AdminNavScreen("admin_overview", "Overview", Icons.Filled.Dashboard)
    object Users : AdminNavScreen("admin_users", "Users", Icons.Filled.People)
    object Parcels : AdminNavScreen("admin_parcels", "Parcels", Icons.Filled.LocalShipping)
    object Settings : AdminNavScreen("admin_settings", "Settings", Icons.Filled.Settings)
}

val adminScreens = listOf(
    AdminNavScreen.Overview,
    AdminNavScreen.Users,
    AdminNavScreen.Parcels,
    AdminNavScreen.Settings
)

// --------------------------------------------------
// ADMIN DASHBOARD MAIN COMPOSABLE
// --------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: NavController,
    padding: Modifier, // Outer navigation (for logout, dispatcher/driver links)
) {
    val navHostController = rememberNavController() // Internal navigation for BottomBar

    Scaffold(
        topBar = { TopAppBar(
            title = { Text("Admin Console") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            actions = {
                // Example action: Logout button
                IconButton(onClick = { /* TODO: Implement Logout function */ navController.navigate("login") }) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        ) },
        bottomBar = { AdminBottomNavigationBar(navHostController) }
    ) { paddingValues ->
        AdminNavHost(navHostController, Modifier.padding(paddingValues))
    }
}

// --------------------------------------------------
// BOTTOM NAVIGATION COMPOSABLE
// --------------------------------------------------
@Composable
fun AdminBottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        adminScreens.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        // Avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

// --------------------------------------------------
// ADMIN NAV HOST (Handles internal screen switching)
// --------------------------------------------------
@Composable
fun AdminNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = AdminNavScreen.Overview.route,
        modifier = modifier
    ) {
        composable(AdminNavScreen.Overview.route) {
            // Use ViewModel injected via Factory
            OverviewScreen(viewModel = viewModel(factory = AdminHomeViewModelFactory(AdminRepository())))
        }
        // Placeholder screens for future expansion
        composable(AdminNavScreen.Users.route) { UserManagementScreen() }
        composable(AdminNavScreen.Parcels.route) { ParcelManagementScreen() }
        composable(AdminNavScreen.Settings.route) { AdminSettingsScreen() }
    }
}

// --------------------------------------------------
// 5.1 OVERVIEW/DASHBOARD SCREEN (View Model based)
// --------------------------------------------------
@Composable
fun OverviewScreen(viewModel: AdminHomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("Dashboard Overview", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
        }

        when (val state = uiState) {
            is AdminHomeUIState.Loading -> {
                item { CircularProgressIndicator(modifier = Modifier.padding(32.dp)) }
            }
            is AdminHomeUIState.Error -> {
                item { Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error) }
            }
            is AdminHomeUIState.Success -> {
                item {
                    // Analytics Cards
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // FIX: StatCard now accepts a modifier to apply weight externally
                        StatCard(
                            title = "Total Parcels",
                            value = state.data.totalParcels.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Delivered",
                            value = state.data.deliveredParcels.toString(),
                            color = GGColors.SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Pending",
                            value = state.data.pendingParcels.toString(),
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(24.dp))

                    // Quick Stats: Users
                    Text("User Summary", style = MaterialTheme.typography.titleLarge)
                    Divider(Modifier.padding(vertical = 8.dp))
                    state.data.users.groupBy { it.role }.forEach { (role, list) ->
                        Text("Total ${role.replaceFirstChar { it.uppercase() }}s: ${list.size}")
                    }
                    Spacer(Modifier.height(24.dp))
                }

                // Parcel List (For quick look)
                item {
                    Text("Recent Parcels", style = MaterialTheme.typography.titleLarge)
                    Divider(Modifier.padding(vertical = 8.dp))
                }
                items(state.data.parcels.take(5)) { parcel -> // Show only top 5 recent parcels
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ID: ${parcel.id}", style = MaterialTheme.typography.bodyMedium)
                            Text("Status: ${parcel.status.replaceFirstChar { it.uppercase() }}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

// --------------------------------------------------
// STAT CARD COMPOSABLE (FIXED)
// --------------------------------------------------
@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier // Accepts external modifier
) {
    Card(
        // The external modifier (including weight) is applied here
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = color)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = color)
        }
    }
}

// --------------------------------------------------
// PLACEHOLDER SCREENS (Used in AdminNavHost)
// --------------------------------------------------
//@Composable fun UserManagementScreen() { Box(Modifier.fillMaxSize().background(Color.LightGray), Alignment.Center) { Text("User Management (TO BE BUILT)") } }
//@Composable fun ParcelManagementScreen() { Box(Modifier.fillMaxSize().background(Color.LightGray), Alignment.Center) { Text("Parcel Management (TO BE BUILT)") } }
//@Composable fun AdminSettingsScreen() { Box(Modifier.fillMaxSize().background(Color.LightGray), Alignment.Center) { Text("Admin Settings (TO BE BUILT)") } }

// --------------------------------------------------
// PREVIEW
// --------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewAdminDashboardScreen() {
    GGCourierGoTheme {
        AdminHomeScreen(
            navController = rememberNavController(),
            padding = Modifier
        )
    }
}