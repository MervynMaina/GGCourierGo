package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mervyn.ggcouriergo.models.Parcel

@Composable
fun DriverHistoryTab(
    parcels: List<Parcel>,
    onNavigateToDetails: (String) -> Unit
) {
    // Filter for only delivered parcels
    val history = parcels.filter { it.status.lowercase() == "delivered" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        if (history.isEmpty()) {
            EmptyDriverHistory()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Personal Stats Header
                item {
                    DriverStatsHeader(history.size)
                }

                items(history) { parcel ->
                    DriverHistoryCard(parcel = parcel) {
                        onNavigateToDetails(parcel.id)
                    }
                }
            }
        }
    }
}

@Composable
fun DriverStatsHeader(count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Your Progress", color = Color.White.copy(0.7f), fontSize = 12.sp)
                Text("$count Deliveries", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }
            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun DriverHistoryCard(parcel: Parcel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(parcel.receiverName, fontWeight = FontWeight.Bold)
                Text(parcel.dropoffAddress, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
            }

            Text(
                text = "DONE",
                fontWeight = FontWeight.Black,
                fontSize = 10.sp,
                color = Color(0xFF2E7D32),
                modifier = Modifier.background(Color(0xFFE8F5E9), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun EmptyDriverHistory() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Text("No completed trips yet.", color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}