package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mervyn.ggcouriergo.data.DriverDashboardViewModel
import com.mervyn.ggcouriergo.data.DriverDashboardViewModelFactory
import com.mervyn.ggcouriergo.data.ThemeSettings
import com.mervyn.ggcouriergo.models.DriverDashboardUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import com.mervyn.ggcouriergo.navigation.*
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import kotlinx.coroutines.launch

@Composable
fun DriverDashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: DriverDashboardViewModel = viewModel(
        factory = DriverDashboardViewModelFactory(ParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val driverId = FirebaseAuth.getInstance().currentUser?.uid

    // Theme Logic
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themeSettings = remember { ThemeSettings(context) }
    val isDarkMode by themeSettings.darkModeFlow.collectAsState(initial = false)

    // UI State for Tabs and Menu
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    val tabs = listOf("Active Tasks", "My History")

    LaunchedEffect(driverId) {
        driverId?.let { viewModel.setDriverId(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Fix: Background adapts
    ) {
        // --- PREMIUM ENHANCED HEADER ---
        Surface(
            color = MaterialTheme.colorScheme.surface, // Fix: Surface adapts
            tonalElevation = 2.dp,
            shadowElevation = 1.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Driver Console",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )

                        val activeCount = if (uiState is DriverDashboardUIState.Success) {
                            (uiState as DriverDashboardUIState.Success).parcels.count { it.status.lowercase() != "delivered" }
                        } else 0

                        Text(
                            text = "You have $activeCount active deliveries",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

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

                // --- TAB ROW ---
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        )
                    }
                }
            }
        }

        // --- DASHBOARD CONTENT ---
        when (val state = uiState) {
            is DriverDashboardUIState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is DriverDashboardUIState.Error -> {
                Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { driverId?.let { viewModel.setDriverId(it) } }) {
                            Text("Retry Connection")
                        }
                    }
                }
            }
            is DriverDashboardUIState.Success -> {
                val filteredParcels = if (selectedTabIndex == 0) {
                    state.parcels.filter { it.status.lowercase() != "delivered" }
                } else {
                    state.parcels.filter { it.status.lowercase() == "delivered" }
                }

                if (filteredParcels.isEmpty()) {
                    if (selectedTabIndex == 0) EmptyDeliveriesView() else EmptyHistoryView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredParcels, key = { it.id }) { parcel ->
                            DriverParcelCard(parcel = parcel) {
                                navController.navigate(routeDriverParcelDetails(parcel.id))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverParcelCard(parcel: Parcel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val statusColor = when(parcel.status.lowercase()) {
                    "assigned" -> MaterialTheme.colorScheme.primary
                    "picked_up" -> Color(0xFF1976D2)
                    "in_transit" -> Color(0xFFE65100)
                    "delivered" -> Color(0xFF1B8F3A)
                    else -> MaterialTheme.colorScheme.outline
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = parcel.status.replace("_", " ").uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    text = "ID: ${parcel.id.takeLast(5)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = parcel.receiverName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = parcel.dropoffAddress,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                IconButton(
                    onClick = onClick,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Navigation,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            if (parcel.status.lowercase() != "delivered") {
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when(parcel.status.lowercase()) {
                            "assigned" -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                ) {
                    Icon(Icons.Default.LocalShipping, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = when(parcel.status.lowercase()) {
                            "assigned" -> "START PICKUP"
                            "picked_up" -> "GO TO TRANSIT"
                            "in_transit" -> "COMPLETE DELIVERY"
                            else -> "VIEW DETAILS"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline)
                    )
                ) {
                    Text("VIEW COMPLETION DETAILS", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmptyDeliveriesView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.LocalShipping,
            null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(16.dp))
        Text("All clear!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("New assignments will appear here", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
    }
}

@Composable
fun EmptyHistoryView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Notifications,
            null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(16.dp))
        Text("No history yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Complete your first delivery to see it here!", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewDriverDashboardScreen() {
    val navController = rememberNavController()
    GGCourierGoTheme {
        DriverDashboardScreen(navController)
    }
}