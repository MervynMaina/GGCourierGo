package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mervyn.ggcouriergo.data.AssignedParcelsViewModel
import com.mervyn.ggcouriergo.data.AssignedParcelsViewModelFactory
import com.mervyn.ggcouriergo.models.AssignedParcelsUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.navigation.routeParcelDetails
import com.mervyn.ggcouriergo.repository.ParcelRepository

@Composable
fun AssignedParcelsTab(
    onNavigateToDetails: (String) -> Unit,
    viewModel: AssignedParcelsViewModel = viewModel(
        factory = AssignedParcelsViewModelFactory(ParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {

        Spacer(Modifier.height(16.dp))
        Text(
            "Active Assignments",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        when (val state = uiState) {
            is AssignedParcelsUIState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is AssignedParcelsUIState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("Error loading assignments: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is AssignedParcelsUIState.Success -> {
                val assignedParcels = state.parcels
                if (assignedParcels.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No parcels are currently assigned to drivers.", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(assignedParcels, key = { it.id }) { parcel ->
                            AssignedParcelCard(parcel = parcel) {
                                onNavigateToDetails(parcel.id)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignedParcelCard(parcel: Parcel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Parcel ID: ${parcel.id}", style = MaterialTheme.typography.titleMedium)
            Text("Driver: ${parcel.assignedDriver ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
            Text("Status: ${parcel.status.uppercase()}", color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Divider()
            Spacer(Modifier.height(4.dp))
            Text("Pickup: ${parcel.pickupAddress}")
            Text("Dropoff: ${parcel.dropoffAddress}")
        }
    }
}

// NOTE: Remember to update DispatcherDashboardScreen.kt to include this tab!
// The structure should be:
/*
    // In DispatcherDashboardScreen.kt:
    when (selectedTabIndex) {
        0 -> NewParcelsTab(navController::navigate)
        1 -> AssignedParcelsTab(navController::navigate) // <-- NEW TAB
        2 -> MapTab()
    }
*/