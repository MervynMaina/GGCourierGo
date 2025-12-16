package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mervyn.ggcouriergo.navigation.routeParcelDetails
import com.mervyn.ggcouriergo.navigation.ROUT_CREATE_PARCEL
import com.mervyn.ggcouriergo.navigation.ROUT_LOGIN
import com.mervyn.ggcouriergo.navigation.ROUT_PROFILE
import com.mervyn.ggcouriergo.navigation.ROUT_SETTINGS
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

sealed class DispatcherNavTab(val title: String) {
    object NewParcels : DispatcherNavTab("New Parcels")
    object AssignedParcels : DispatcherNavTab("Assigned")
    object MapView : DispatcherNavTab("Fleet Intel")
    object History : DispatcherNavTab("History") // New Tab added
}

val dispatcherTabs = listOf(
    DispatcherNavTab.NewParcels,
    DispatcherNavTab.AssignedParcels,
    DispatcherNavTab.MapView,
    DispatcherNavTab.History // Added to the list
)

@Composable
fun DispatcherDashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ROUT_CREATE_PARCEL) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create New Parcel")
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            Surface(
                color = Color.White,
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
                                modifier = Modifier
                                    .background(Color.White)
                                    .width(180.dp)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text("My Profile", color = Color.Black, fontWeight = FontWeight.Medium)
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    onClick = {
                                        showMenu = false
                                        navController.navigate(ROUT_PROFILE)
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Text("Settings", color = Color.Black, fontWeight = FontWeight.Medium)
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    onClick = {
                                        showMenu = false
                                        navController.navigate(ROUT_SETTINGS)
                                    }
                                )

                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                DropdownMenuItem(
                                    text = {
                                        Text("Sign Out", color = Color.Red, fontWeight = FontWeight.Bold)
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
                                    },
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

                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.White,
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
                                        fontSize = 11.sp // Reduced size slightly for 4 tabs
                                    )
                                }
                            )
                        }
                    }
                }
            }

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