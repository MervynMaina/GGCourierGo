package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.ParcelTrackingViewModel
import com.mervyn.ggcouriergo.data.ParcelTrackingViewModelFactory
import com.mervyn.ggcouriergo.models.ParcelTracking
import com.mervyn.ggcouriergo.models.ParcelTrackingUIState
import com.mervyn.ggcouriergo.repository.ParcelTrackingRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme // Using the standard theme

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

    Scaffold(
        topBar = { TopAppBar(title = { Text("Parcel Tracker") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. Parcel ID Input ---
            OutlinedTextField(
                value = parcelIdInput,
                onValueChange = viewModel::updateParcelIdInput,
                label = { Text("Enter Parcel ID") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.trackParcel(parcelIdInput) },
                enabled = parcelIdInput.isNotBlank() && uiState !is ParcelTrackingUIState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState is ParcelTrackingUIState.Loading) "Tracking..." else "Track Parcel")
            }

            Spacer(Modifier.height(32.dp))

            // --- 2. Tracking Results ---
            TrackingContent(uiState = uiState)
        }
    }
}

@Composable
fun TrackingContent(uiState: ParcelTrackingUIState) {
    Card(modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (uiState) {
                is ParcelTrackingUIState.Idle -> {
                    Text("Enter a Parcel ID to see the current status.")
                }
                is ParcelTrackingUIState.Loading -> {
                    CircularProgressIndicator()
                }
                is ParcelTrackingUIState.Error -> {
                    Text(
                        "Tracking Failed: ${uiState.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is ParcelTrackingUIState.Success -> {
                    ParcelTrackingDetails(uiState.parcel)
                }
            }
        }
    }
}

@Composable
fun ParcelTrackingDetails(parcel: ParcelTracking) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Tracking Details", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Divider(Modifier.padding(vertical = 4.dp))

        TrackingDetailRow(label = "Parcel ID", value = parcel.id)
        TrackingDetailRow(label = "Status", value = parcel.status.uppercase())
        TrackingDetailRow(label = "Location", value = parcel.currentLocation)
        TrackingDetailRow(label = "Driver", value = parcel.assignedDriver ?: "N/A")
    }
}

@Composable
fun TrackingDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserDashboardScreen() {
    val navController = rememberNavController()
    GGCourierGoTheme {
        // We cannot easily preview the ViewModel, so we just show the structure
        UserDashboardScreen(navController)
    }
}