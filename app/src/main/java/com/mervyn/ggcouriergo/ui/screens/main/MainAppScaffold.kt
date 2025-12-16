package com.mervyn.ggcouriergo.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock // standard material icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mervyn.ggcouriergo.data.ProfileViewModel
import com.mervyn.ggcouriergo.models.ProfileUIState
import com.mervyn.ggcouriergo.navigation.BottomNavItem
import com.mervyn.ggcouriergo.ui.screens.admin.AdminHomeScreen
import com.mervyn.ggcouriergo.ui.screens.dispatcher.DispatcherDashboardScreen
import com.mervyn.ggcouriergo.ui.screens.driver.DriverDashboardScreen
import com.mervyn.ggcouriergo.ui.screens.driver.UserDashboardScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val role = (uiState as? ProfileUIState.Success)?.profile?.role ?: "loading"
    val navItems = BottomNavItem.getItemsForRole(role)

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        bottomBar = {
            if (role != "loading" && navItems.isNotEmpty()) {
                BottomNavigationBar(navController, navItems)
            }
        }
    ) { paddingValues ->
        // contentModifier must be applied to the dashboard screens
        val contentModifier = Modifier.padding(paddingValues)

        Box(modifier = Modifier.fillMaxSize()) {
            when (role.lowercase()) {
                "driver" -> DriverDashboardScreen(navController, contentModifier)
                "dispatcher" -> DispatcherDashboardScreen(navController, contentModifier)
                "admin" -> AdminHomeScreen(navController, contentModifier)
                "customer" -> UserDashboardScreen(navController, contentModifier)

                "loading" -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Red)
                            Text("Unauthorized: $role", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, items: List<BottomNavItem>) {
    NavigationBar(containerColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // CORRECTED: Explicitly find the start destination ID
                            val startDestId = navController.graph.findStartDestination().id
                            popUpTo(startDestId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, fontSize = 10.sp) }
            )
        }
    }
}