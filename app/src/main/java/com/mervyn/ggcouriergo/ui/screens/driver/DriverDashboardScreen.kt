package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mervyn.ggcouriergo.data.DriverDashboardViewModel
import com.mervyn.ggcouriergo.data.DriverDashboardViewModelFactory
import com.mervyn.ggcouriergo.models.DriverDashboardUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import com.mervyn.ggcouriergo.navigation.routeDriverParcelDetails
// --- CORRECTED THEME IMPORT ---
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: DriverDashboardViewModel = viewModel(
        factory = DriverDashboardViewModelFactory(ParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    // Logged in driver
    val driverId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(driverId) {
        driverId?.let { viewModel.loadAssignedParcels(it) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Driver Console") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text("Welcome, Driver!", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))

            when (val state = uiState) {

                is DriverDashboardUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is DriverDashboardUIState.Error -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error) // Use theme error color
                    }
                }

                is DriverDashboardUIState.Success -> {
                    val parcels = state.parcels

                    if (parcels.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp), contentAlignment = Alignment.Center) {
                            Text("No deliveries assigned yet.")
                        }
                    } else {
                        LazyColumn {
                            items(parcels) { parcel ->
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
}

@Composable
fun DriverParcelCard(parcel: Parcel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick, // Make the entire card clickable
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Parcel ID: ${parcel.id}", style = MaterialTheme.typography.titleMedium)
            Text("Pickup: ${parcel.pickupAddress}")
            Text("Dropoff: ${parcel.dropoffAddress}")
            Text("Receiver: ${parcel.receiverName}")
            Spacer(Modifier.height(8.dp))
            Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Text("View / Start Delivery")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDriverDashboardScreen() {
    val navController = rememberNavController()
    // --- CORRECTED THEME USAGE ---
    GGCourierGoTheme {
        DriverDashboardScreen(navController)
    }
}