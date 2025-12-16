package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mervyn.ggcouriergo.data.ThemeSettings
import com.mervyn.ggcouriergo.navigation.routeParcelDetails
import com.mervyn.ggcouriergo.navigation.ROUT_CREATE_PARCEL
import com.mervyn.ggcouriergo.navigation.ROUT_LOGIN
import com.mervyn.ggcouriergo.navigation.ROUT_PROFILE
import com.mervyn.ggcouriergo.navigation.ROUT_SETTINGS
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import kotlinx.coroutines.launch

sealed class DispatcherNavTab(val title: String) {
    object NewParcels : DispatcherNavTab("New Parcels")
    object AssignedParcels : DispatcherNavTab("Assigned")
    object MapView : DispatcherNavTab("Fleet Intel")
    object History : DispatcherNavTab("History")
}

val dispatcherTabs = listOf(
    DispatcherNavTab.NewParcels,
    DispatcherNavTab.AssignedParcels,
    DispatcherNavTab.MapView,
    DispatcherNavTab.History
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatcherDashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }

    // Theme Logic
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themeSettings = remember { ThemeSettings(context) }
    val isDarkMode by themeSettings.darkModeFlow.collectAsState(initial = false)

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ROUT_CREATE_PARCEL) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create New Parcel")
            }
        },
        containerColor = MaterialTheme.colorScheme.background // Fix: Uses theme background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // --- THEME-AWARE HEADER ---
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                shadowElevation = 2.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Dispatcher Console",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                        )

                        // --- ACTION ROW (Theme + Menu) ---
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Quick Theme Toggle
                            IconButton(onClick = { scope.launch { themeSettings.toggleTheme() } }) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Toggle Theme",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "User Menu",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false },
                                    modifier = Modifier.width(180.dp)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("My Profile") },
                                        leadingIcon = { Icon(Icons.Default.Person, null) },
                                        onClick = {
                                            showMenu = false
                                            navController.navigate(ROUT_PROFILE)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Settings") },
                                        leadingIcon = { Icon(Icons.Default.Settings, null) },
                                        onClick = {
                                            showMenu = false
                                            navController.navigate(ROUT_SETTINGS)
                                        }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    DropdownMenuItem(
                                        text = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
                                        leadingIcon = { Icon(Icons.Default.ExitToApp, null, tint = MaterialTheme.colorScheme.error) },
                                        onClick = {
                                            showMenu = false
                                            FirebaseAuth.getInstance().signOut()
                                            navController.navigate(ROUT_LOGIN) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // --- THEME-AWARE TAB ROW ---
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {},
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        dispatcherTabs.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = tab.title,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // --- TAB CONTENT AREA ---
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (selectedTabIndex) {
                    0 -> NewParcelsTab(onNavigateToAssignment = { navController.navigate(routeParcelDetails(it)) })
                    1 -> AssignedParcelsTab(onNavigateToDetails = { navController.navigate(routeParcelDetails(it)) })
                    2 -> DispatcherMapViewTab()
                    3 -> DispatcherHistoryTab(onNavigateToDetails = { navController.navigate(routeParcelDetails(it)) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDispatcherDashboardScreen() {
    val navController = rememberNavController()
    GGCourierGoTheme {
        DispatcherDashboardScreen(navController)
    }
}