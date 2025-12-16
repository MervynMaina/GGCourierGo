package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
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
fun DispatcherHistoryTab(
    onNavigateToDetails: (String) -> Unit,
    viewModel: AssignedParcelsViewModel = viewModel(
        factory = AssignedParcelsViewModelFactory(ParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        when (val state = uiState) {
            is AssignedParcelsUIState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is AssignedParcelsUIState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is AssignedParcelsUIState.Success -> {
                // Filter for ONLY delivered parcels
                val deliveredParcels = state.parcels.filter { it.status.lowercase() == "delivered" }

                if (deliveredParcels.isEmpty()) {
                    EmptyHistoryView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // --- ARCHIVE HEADER CARD ---
                        item {
                            HistorySummaryHeader(count = deliveredParcels.size)
                        }

                        items(deliveredParcels, key = { it.id }) { parcel ->
                            HistoryCard(parcel = parcel) {
                                onNavigateToDetails(parcel.id)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorySummaryHeader(count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Fleet Archive", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("$count Deliveries", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
            }
            Icon(
                Icons.Default.Inventory2,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun HistoryCard(parcel: Parcel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Circle
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFFE8F5E9)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = parcel.receiverName,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "Driver: ${parcel.assignedDriver ?: "Unknown"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "#${parcel.id.takeLast(5)}",
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "DELIVERED",
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Text("No archived shipments", fontWeight = FontWeight.Bold, color = Color.Gray)
        Text("Completed jobs will appear here.", color = Color.LightGray)
    }
}