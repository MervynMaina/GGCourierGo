package com.mervyn.ggcouriergo.ui.screens.auth

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
import com.mervyn.ggcouriergo.data.DispatcherDashboardViewModel
import com.mervyn.ggcouriergo.data.DispatcherDashboardViewModelFactory
import com.mervyn.ggcouriergo.models.DispatcherDashboardUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme
import com.mervyn.ggcouriergo.navigation.routeParcelDetails

// --------------------------------------------------
// Dispatcher Dashboard Screen
// --------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatcherDashboardScreen(
    navController: NavController,
    viewModel: DispatcherDashboardViewModel = viewModel(
        factory = DispatcherDashboardViewModelFactory(ParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState(initial = DispatcherDashboardUIState.Loading)

    // Load parcels on first composition
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
                                    navController.navigate(routeParcelDetails(parcel.id))
                                }
                            }
                        }
                    }
                }
                is DispatcherDashboardUIState.Idle -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Idle state")
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// Delivery Card
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
// Preview - Multiple States
// --------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewDispatcherDashboardScreen() {
    val navController = rememberNavController()

    val dummyParcels = listOf(
        Parcel(
            id = "P001", senderName = "Alice", receiverName = "Bob",
            pickupAddress = "123 Main St", dropoffAddress = "456 Elm St", packageDetails = "Box"
        ),
        Parcel(
            id = "P002", senderName = "Charlie", receiverName = "Dave",
            pickupAddress = "789 Oak St", dropoffAddress = "321 Pine St", packageDetails = "Envelope"
        )
    )

    CourierGoTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Loading State Preview")
            DispatcherDashboardScreen(
                navController = navController,
                viewModel = object : DispatcherDashboardViewModel(ParcelRepository()) {
                    init { _uiState.value = DispatcherDashboardUIState.Loading }
                    override fun loadParcels() {}
                }
            )

            Spacer(Modifier.height(16.dp))
            Text("Success State Preview")
            DispatcherDashboardScreen(
                navController = navController,
                viewModel = object : DispatcherDashboardViewModel(ParcelRepository()) {
                    init { _uiState.value = DispatcherDashboardUIState.Success(dummyParcels) }
                    override fun loadParcels() {}
                }
            )

            Spacer(Modifier.height(16.dp))
            Text("Empty State Preview")
            DispatcherDashboardScreen(
                navController = navController,
                viewModel = object : DispatcherDashboardViewModel(ParcelRepository()) {
                    init { _uiState.value = DispatcherDashboardUIState.Success(emptyList()) }
                    override fun loadParcels() {}
                }
            )

            Spacer(Modifier.height(16.dp))
            Text("Error State Preview")
            DispatcherDashboardScreen(
                navController = navController,
                viewModel = object : DispatcherDashboardViewModel(ParcelRepository()) {
                    init { _uiState.value = DispatcherDashboardUIState.Error("Failed to load parcels") }
                    override fun loadParcels() {}
                }
            )
        }
    }
}

// --------------------------------------------------
// Preview - Single Card
// --------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewAssignableDeliveryCard() {
    val dummyParcel = Parcel(
        id = "P100",
        senderName = "John Doe",
        receiverName = "Jane Smith",
        pickupAddress = "100 Maple St",
        dropoffAddress = "200 Oak St",
        packageDetails = "Medium Box"
    )

    CourierGoTheme {
        AssignableDeliveryCard(parcel = dummyParcel, onAssignClick = {})
    }
}
