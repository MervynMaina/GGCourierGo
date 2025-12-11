package com.mervyn.ggcouriergo.data

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
import com.mervyn.ggcouriergo.data.model.Parcel
import com.mervyn.ggcouriergo.data.repository.ParcelRepository
import com.mervyn.ggcouriergo.ui.screens.dispatcher.DispatcherDashboardUIState
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme
import com.mervyn.ggcouriergo.navigation.parcelDetailsRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatcherDashboardScreen(
    navController: NavController,
    viewModel: DispatcherDashboardViewModel = viewModel(factory = DispatcherDashboardViewModelFactory(ParcelRepository()))
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadParcels()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dispatcher Dashboard") }) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text("Welcome, Dispatcher!", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))

            when (uiState) {
                is DispatcherDashboardUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is DispatcherDashboardUIState.Error -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text((uiState as DispatcherDashboardUIState.Error).message)
                    }
                }

                is DispatcherDashboardUIState.Success -> {
                    val parcels = (uiState as DispatcherDashboardUIState.Success).parcels
                    if (parcels.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No unassigned deliveries available.")
                        }
                    } else {
                        LazyColumn {
                            items(parcels) { parcel ->
                                AssignableDeliveryCard(parcel = parcel) {
                                    navController.navigate(parcelDetailsRoute(parcel.id))
                                }
                            }
                        }
                    }
                }

                is DispatcherDashboardUIState.Idle -> {
                    // Optional: show placeholder or nothing
                }
            }
        }
    }
}

// --------------------------------------------------
// DELIVERY CARD
// --------------------------------------------------
@Composable
fun AssignableDeliveryCard(parcel: Parcel, onAssignClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Parcel: ${parcel.id}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("Pickup: ${parcel.pickupAddress}")
            Text("Dropoff: ${parcel.dropoffAddress}")
            Text("Sender: ${parcel.senderName}")
            Text("Receiver: ${parcel.receiverName}")
            Spacer(Modifier.height(8.dp))
            Button(onClick = onAssignClick, modifier = Modifier.fillMaxWidth()) {
                Text("View / Assign")
            }
        }
    }
}

// --------------------------------------------------
// PREVIEW
// --------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewDispatcherDashboardScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        DispatcherDashboardScreen(navController)
    }
}
