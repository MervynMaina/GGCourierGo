package com.mervyn.ggcouriergo.ui.screens.dispatcher

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
import com.mervyn.ggcouriergo.data.NewParcelsViewModel
import com.mervyn.ggcouriergo.data.NewParcelsViewModelFactory
import com.mervyn.ggcouriergo.models.NewParcelsUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

@Composable
fun NewParcelsTab(
    // Function passed from DispatcherDashboardScreen to handle navigation to assignment/details screen
    onNavigateToAssignment: (String) -> Unit,
    viewModel: NewParcelsViewModel = viewModel(
        factory = NewParcelsViewModelFactory(ParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {

        Spacer(Modifier.height(16.dp))
        Text(
            "Parcels Pending Assignment",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        when (val state = uiState) {
            is NewParcelsUIState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is NewParcelsUIState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("Error loading new parcels: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is NewParcelsUIState.Success -> {
                val newParcels = state.parcels
                if (newParcels.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No new parcels are currently waiting for assignment.", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(newParcels, key = { it.id }) { parcel ->
                            NewParcelCard(parcel = parcel) {
                                // Triggers navigation to the screen where the dispatcher selects a driver
                                onNavigateToAssignment(parcel.id)
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
fun NewParcelCard(parcel: Parcel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("ID: ${parcel.id}", style = MaterialTheme.typography.titleMedium)
                Text("Status: ${parcel.status.uppercase()}", color = MaterialTheme.colorScheme.tertiary)
            }
            Button(onClick = onClick) {
                Text("Assign Driver")
            }
        }
    }
}

// Preview Note: Requires a mock UI State and Parcel object for a functional preview.
@Preview(showBackground = true)
@Composable
fun PreviewNewParcelsTab() {
    GGCourierGoTheme {
        // You would typically pass a function that does nothing for the preview
        NewParcelsTab(onNavigateToAssignment = {})
    }
}