package com.mervyn.ggcouriergo.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.mervyn.ggcouriergo.models.*
import com.mervyn.ggcouriergo.repository.AdminRepository
import com.mervyn.ggcouriergo.navigation.*
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

// --- 1. LOCAL UI COMPONENTS (Fixed to prevent import mismatches) ---

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, fontWeight = FontWeight.Black, fontSize = 22.sp, color = color)
        }
    }
}

@Composable
fun AdminParcelListItem(parcel: Parcel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Inventory2, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("ID: ${parcel.id.takeLast(8)}", fontWeight = FontWeight.Bold)
                Text("Receiver: ${parcel.receiverName}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Spacer(Modifier.weight(1f))
            Surface(color = Color(0xFFF1F3F4), shape = RoundedCornerShape(8.dp)) {
                Text(parcel.status.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun SimpleBarChart(data: List<Int>, modifier: Modifier = Modifier) {
    val maxValue = (data.maxOrNull() ?: 1).toFloat()
    Row(
        modifier = modifier.fillMaxWidth().height(120.dp).padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { value ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight((value / maxValue).coerceAtLeast(0.1f))
                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                    .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)))
            )
        }
    }
}

// --- 2. NAVIGATION ---

sealed class AdminNavScreen(val route: String, val title: String, val icon: ImageVector) {
    object Overview : AdminNavScreen("admin_overview", "Overview", Icons.Filled.Analytics)
    object Users : AdminNavScreen("admin_users", "Users", Icons.Filled.People)
    object Parcels : AdminNavScreen("admin_parcels", "Inventory", Icons.Filled.Inventory2)
    object Settings : AdminNavScreen("admin_settings", "Settings", Icons.Filled.Settings)
}

val adminScreens = listOf(AdminNavScreen.Overview, AdminNavScreen.Users, AdminNavScreen.Parcels, AdminNavScreen.Settings)

// --- 3. MAIN SCREENS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    val internalNavController = rememberNavController()
    Scaffold(
        modifier = modifier,
        bottomBar = { AdminBottomNavigationBar(internalNavController) }
    ) { paddingValues ->
        NavHost(internalNavController, AdminNavScreen.Overview.route, Modifier.padding(paddingValues)) {
            composable(AdminNavScreen.Overview.route) {
                OverviewScreen(
                    viewModel = viewModel(factory = AdminHomeViewModelFactory(AdminRepository())),
                    onLogout = { navController.navigate(ROUT_LOGIN) { popUpTo(0) { inclusive = true } } }
                )
            }
            composable(AdminNavScreen.Users.route) { UserManagementScreen() }
            composable(AdminNavScreen.Parcels.route) { ParcelManagementScreen() }
            composable(AdminNavScreen.Settings.route) { AdminSettingsScreen() }
        }
    }
}

@Composable
fun OverviewScreen(viewModel: AdminHomeViewModel, onLogout: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        Surface(color = Color.White, shadowElevation = 1.dp) {
            Row(Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Admin Console", fontWeight = FontWeight.Black, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                    Text("System Monitoring", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                IconButton(onClick = onLogout, modifier = Modifier.background(Color(0xFFFFEBEE), CircleShape)) {
                    Icon(Icons.Filled.Logout, contentDescription = null, tint = Color.Red)
                }
            }
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            when (val state = uiState) {
                is AdminHomeUIState.Loading -> { item { CircularProgressIndicator() } }
                is AdminHomeUIState.Success -> {
                    item {
                        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Weekly Volume", fontWeight = FontWeight.Bold, color = Color.Gray)
                                SimpleBarChart(listOf(40, 60, 45, 90, 70, 85, 100))
                            }
                        }
                    }
                    item {
                        Row(Modifier.fillMaxWidth()) {
                            AdminStatCard("Total", state.data.totalParcels.toString(), MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                            AdminStatCard("Active", state.data.pendingParcels.toString(), Color(0xFFE65100), Modifier.weight(1f))
                            AdminStatCard("Done", state.data.deliveredParcels.toString(), Color(0xFF2E7D32), Modifier.weight(1f))
                        }
                    }
                    items(state.data.parcels.take(5)) { AdminParcelListItem(it) }
                }
                is AdminHomeUIState.Error -> { item { Text("Error: ${state.message}", color = Color.Red) } }
                else -> {}
            }
        }
    }
}

@Composable
fun AdminBottomNavigationBar(navController: NavHostController) {
    NavigationBar(containerColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        adminScreens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, null) },
                label = { Text(screen.title, fontSize = 10.sp) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdminDashboardScreen() {
    GGCourierGoTheme { AdminHomeScreen(rememberNavController(), Modifier) }
}