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
import com.mervyn.ggcouriergo.data.DriverDashboardViewModel
import com.mervyn.ggcouriergo.data.DriverDashboardViewModelFactory
import com.mervyn.ggcouriergo.models.DriverDashboardUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme
import com.mervyn.ggcouriergo.navigation.driverParcelDetailsRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    navController: NavController,
    driverId: String,
    viewModel: DriverDashboardViewModel = viewModel(factory = DriverDashboardViewModelFactory(
        ParcelRepository()
    )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadAssignedParcels(driverId) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Driver Dashboard") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text("Welcome, Driver!", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))

            when (uiState) {
                is DriverDashboardUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DriverDashboardUIState.Error -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text((uiState as DriverDashboardUIState.Error).message)
                    }
                }
                is DriverDashboardUIState.Success -> {
                    val parcels = (uiState as DriverDashboardUIState.Success).parcels
                    if (parcels.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No deliveries assigned yet.")
                        }
                    } else {
                        LazyColumn {
                            items(parcels) { parcel ->
                                DriverParcelCard(parcel = parcel) {
                                    navController.navigate(driverParcelDetailsRoute(parcel.id))
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Parcel: ${parcel.id}", style = MaterialTheme.typography.titleMedium)
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
    CourierGoTheme {
        DriverDashboardScreen(navController, driverId = "123")
    }
}
