package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mervyn.ggcouriergo.data.AssignedParcelsViewModel
import com.mervyn.ggcouriergo.data.AssignedParcelsViewModelFactory
import com.mervyn.ggcouriergo.models.AssignedParcelsUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository

@Composable
fun AssignedParcelsTab(
    onNavigateToDetails: (String) -> Unit,
    viewModel: AssignedParcelsViewModel = viewModel(
        factory = AssignedParcelsViewModelFactory(ParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is AssignedParcelsUIState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        is AssignedParcelsUIState.Error -> {
            Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is AssignedParcelsUIState.Success -> {
            if (state.parcels.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text("No active assignments", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Assigned parcels will appear here.", color = Color.LightGray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.parcels, key = { it.id }) { parcel ->
                        AssignedParcelCard(parcel = parcel) {
                            onNavigateToDetails(parcel.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssignedParcelCard(parcel: Parcel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "DRIVER: ${parcel.assignedDriver?.take(12) ?: "Unknown"}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Status Chip with dynamic colors
                val statusColor = when (parcel.status.lowercase()) {
                    "picked_up" -> Color(0xFF2196F3) // Blue
                    "in_transit" -> Color(0xFFFF9800) // Orange
                    else -> Color(0xFF4CAF50) // Green for Assigned
                }

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = parcel.status.replace("_", " ").uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.4f))
            Spacer(Modifier.height(12.dp))

            // Routing Information
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("FROM", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(parcel.pickupAddress, maxLines = 1, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 8.dp).size(14.dp),
                    tint = Color.LightGray
                )
                Column(Modifier.weight(1f)) {
                    Text("TO", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(parcel.dropoffAddress, maxLines = 1, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "ID: ${parcel.id}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray,
                fontSize = 10.sp
            )
        }
    }
}