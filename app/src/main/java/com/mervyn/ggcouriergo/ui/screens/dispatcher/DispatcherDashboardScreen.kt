package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.DispatcherDashboardViewModel
import com.mervyn.ggcouriergo.data.DispatcherDashboardViewModelFactory
import com.mervyn.ggcouriergo.models.DispatcherDashboardUIState
import com.mervyn.ggcouriergo.models.Parcel // <--- IMPORT ADDED
import com.mervyn.ggcouriergo.repository.ParcelRepository
import com.mervyn.ggcouriergo.navigation.routeParcelDetails
import com.mervyn.ggcouriergo.navigation.ROUT_CREATE_PARCEL
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme // Corrected Theme Import

// Define internal screens/tabs
sealed class DispatcherNavTab(val title: String) {
    object NewParcels : DispatcherNavTab("New Parcels")
    object AssignedParcels : DispatcherNavTab("Assigned")
    object MapView : DispatcherNavTab("Map View")
}
val dispatcherTabs = listOf(DispatcherNavTab.NewParcels, DispatcherNavTab.AssignedParcels, DispatcherNavTab.MapView)

// --------------------------------------------------
// Dispatcher Dashboard Screen (Top Level)
// --------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatcherDashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: DispatcherDashboardViewModel = viewModel(
        factory = DispatcherDashboardViewModelFactory(ParcelRepository())
    )
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dispatcher Console") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ROUT_CREATE_PARCEL) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create Parcel")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Tab Row for internal navigation
            TabRow(selectedTabIndex = selectedTabIndex) {
                dispatcherTabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(tab.title) }
                    )
                }
            }

            // Content based on selected tab
            // FIX 1: Removed 'index =' assignment
            when (selectedTabIndex) {
                0 -> NewParcelsTab(navController, viewModel)
                1 -> AssignedParcelsTab(onNavigateToDetails = { parcelId ->
                    // Navigate to the details screen for monitoring
                    navController.navigate(routeParcelDetails(parcelId))
                })
                2 -> DispatcherMapViewTab()
            }
        }
    }
}

// --------------------------------------------------
// TAB 1: New Parcels (Shows unassigned deliveries)
// --------------------------------------------------
@Composable
fun NewParcelsTab(navController: NavController, viewModel: DispatcherDashboardViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadParcels() // Loads unassigned parcels
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text(
            "Parcels Ready for Assignment",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(10.dp))

        when (val state = uiState) {
            is DispatcherDashboardUIState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DispatcherDashboardUIState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is DispatcherDashboardUIState.Success -> {
                if (state.parcels.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No new parcels awaiting assignment.", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn {
                        items(state.parcels, key = { it.id }) { parcel ->
                            AssignableDeliveryCard(parcel = parcel) {
                                // Navigate to the details screen for assignment
                                navController.navigate(routeParcelDetails(parcel.id))
                            }
                        }
                    }
                }
            }
            // Add Idle state handling for robustness
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("System is initializing...")
                }
            }
        }
    }
}

// --------------------------------------------------
// TAB 3: Map View (Placeholder remains)
// --------------------------------------------------
@Composable
fun DispatcherMapViewTab() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        //
        Text("Live Driver/Parcel Map (Integration with Maps API required)", style = MaterialTheme.typography.titleMedium)
    }
}

// --------------------------------------------------
// Delivery Card
// --------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignableDeliveryCard(parcel: Parcel, onAssignClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onAssignClick, // Make the entire card clickable
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Parcel ID: ${parcel.id}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            // FIX 2: Parcel properties are now recognized due to the import
            Text("Pickup: ${parcel.pickupAddress}")
            Text("Dropoff: ${parcel.dropoffAddress}")
            Spacer(Modifier.height(8.dp))
            Button(onClick = onAssignClick, modifier = Modifier.fillMaxWidth()) {
                Text("View Details / Assign")
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