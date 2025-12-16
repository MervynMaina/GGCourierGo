package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.mervyn.ggcouriergo.data.ParcelTrackingViewModel
import com.mervyn.ggcouriergo.data.ParcelTrackingViewModelFactory
import com.mervyn.ggcouriergo.data.ThemeSettings
import com.mervyn.ggcouriergo.models.ParcelTracking
import com.mervyn.ggcouriergo.models.ParcelTrackingUIState
import com.mervyn.ggcouriergo.repository.ParcelTrackingRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ParcelTrackingViewModel = viewModel(
        factory = ParcelTrackingViewModelFactory(ParcelTrackingRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val parcelIdInput by viewModel.parcelIdInput.collectAsState()
    val recentSearches by viewModel.searchHistory.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themeSettings = remember { ThemeSettings(context) }
    val isDarkMode by themeSettings.darkModeFlow.collectAsState(initial = false)

    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- 1. HEADER SECTION ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // ACTION ROW
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 40.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { themeSettings.toggleTheme() } }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = Color.White
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.MoreVert, "Menu", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Profile") },
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                onClick = { showMenu = false }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = { Icon(Icons.Default.ExitToApp, null, tint = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 32.dp)
                ) {
                    Text("Track Your Shipment", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = Color.White)
                    Text("Enter your tracking ID for real-time updates", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))

                    Spacer(Modifier.height(24.dp))

                    TextField(
                        value = parcelIdInput,
                        onValueChange = viewModel::updateParcelIdInput,
                        placeholder = { Text("Example: GG-12345") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Search, null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.trackParcel(parcelIdInput) },
                        enabled = parcelIdInput.isNotBlank() && uiState !is ParcelTrackingUIState.Loading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        if (uiState is ParcelTrackingUIState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        } else {
                            Text("Locate Parcel", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- 2. SCROLLABLE CONTENT ---
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { TrackingContent(uiState = uiState) }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Recent Searches", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    if (recentSearches.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearHistory() }) {
                            Text("Clear", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            items(recentSearches) { id ->
                HistoryItem(id = id, onClick = {
                    viewModel.updateParcelIdInput(id)
                    viewModel.trackParcel(id)
                })
            }
        }
    }
}

@Composable
fun TrackingContent(uiState: ParcelTrackingUIState) {
    AnimatedContent(targetState = uiState, label = "tracking_animation") { state ->
        when (state) {
            is ParcelTrackingUIState.Idle -> {
                Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("Ready to track your parcel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            is ParcelTrackingUIState.Loading -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
            }
            is ParcelTrackingUIState.Error -> {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(state.message, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
            is ParcelTrackingUIState.Success -> {
                ParcelTrackingDetails(state.parcel)
            }
        }
    }
}

@Composable
fun ParcelTrackingDetails(parcel: ParcelTracking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(parcel.status.uppercase(), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
            TrackingDetailItem(Icons.Default.MyLocation, "Location", parcel.currentLocation)
            Spacer(Modifier.height(12.dp))
            TrackingDetailItem(Icons.Default.Person, "Driver", parcel.assignedDriver ?: "Unassigned")
        }
    }
}

@Composable
fun HistoryItem(id: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(16.dp))
            Text(id, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ArrowForwardIos, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun TrackingDetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewUserDashboardScreen() {
    GGCourierGoTheme {
        UserDashboardScreen(rememberNavController())
    }
}