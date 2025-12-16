package com.mervyn.ggcouriergo.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelManagementScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All") }
    val statuses = listOf("All", "Pending", "In Transit", "Delivered", "Cancelled")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- HEADER & SEARCH ---
        Surface(
            color = Color.White,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Inventory Tracking",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search Tracking ID or Receiver...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF1F3F4),
                        unfocusedContainerColor = Color(0xFFF1F3F4),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent,
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Status Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statuses.forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                            label = { Text(status, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }

        // --- PARCEL LIST ---
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Mock data for the admin view
            val mockParcels = listOf(
                ParcelInfo("TRK-8821", "In Transit", "John Doe"),
                ParcelInfo("TRK-1029", "Pending", "Sarah Smith"),
                ParcelInfo("TRK-4452", "Delivered", "Mike Ross"),
                ParcelInfo("TRK-9901", "Cancelled", "Emma Stone")
            )

            val filtered = mockParcels.filter {
                (selectedStatus == "All" || it.status == selectedStatus) &&
                        (it.id.contains(searchQuery, true) || it.receiver.contains(searchQuery, true))
            }

            items(filtered) { parcel ->
                AdminParcelCard(parcel)
            }
        }
    }
}

data class ParcelInfo(val id: String, val status: String, val receiver: String)

@Composable
fun AdminParcelCard(parcel: ParcelInfo) {
    val statusColor = when (parcel.status) {
        "Delivered" -> Color(0xFF2E7D32)
        "In Transit" -> Color(0xFF1976D2)
        "Pending" -> Color(0xFFE65100)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (parcel.status == "In Transit") Icons.Default.LocalShipping else Icons.Default.Inventory2,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(parcel.id, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("To: ${parcel.receiver}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = parcel.status.uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = statusColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewParcelManagementScreen() {
    GGCourierGoTheme {
        ParcelManagementScreen()
    }
}