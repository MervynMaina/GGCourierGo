package com.mervyn.ggcouriergo.ui.screens.delivery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.DeliveryDetailViewModel
import com.mervyn.ggcouriergo.data.DeliveryDetailViewModelFactory
import com.mervyn.ggcouriergo.models.DeliveryDetailUIState
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDetailsScreen(
    navController: NavController? = null,
    deliveryId: String,
    viewModel: DeliveryDetailViewModel = viewModel(
        factory = DeliveryDetailViewModelFactory(deliveryId)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shipment Tracking", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA) // Modern light grey background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            when (val state = uiState) {
                is DeliveryDetailUIState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DeliveryDetailUIState.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(state.message, modifier = Modifier.padding(16.dp), color = Color.Red)
                    }
                }
                is DeliveryDetailUIState.Success -> {
                    val data = state.detail

                    // --- TRACKING ID & STATUS ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TRACKING NUMBER", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("#${data.id.uppercase()}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        }
                        StatusChip(status = data.status)
                    }

                    Spacer(Modifier.height(24.dp))

                    // --- LOGISTICS CARD ---
                    InfoCard(title = "Route Details", icon = Icons.Default.Route) {
                        AddressRow(label = "Pickup", address = data.pickupAddress, icon = Icons.Default.LocationOn, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        AddressRow(label = "Dropoff", address = data.dropoffAddress, icon = Icons.Default.Flag, color = Color(0xFFE91E63))
                    }

                    Spacer(Modifier.height(16.dp))

                    // --- PEOPLE CARD ---
                    InfoCard(title = "Contact Information", icon = Icons.Default.Contacts) {
                        DetailItem(label = "Sender", value = data.senderName)
                        DetailItem(label = "Receiver", value = "${data.receiverName} (${data.receiverPhone})")
                        DetailItem(label = "Assigned Driver", value = data.assignedDriver ?: "Searching for driver...", isItalic = data.assignedDriver == null)
                    }

                    Spacer(Modifier.height(16.dp))

                    // --- PACKAGE CARD ---
                    InfoCard(title = "Package Content", icon = Icons.Default.Inventory) {
                        Text(data.packageDetails, style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun AddressRow(label: String, address: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp).padding(top = 2.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(address, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, isItalic: Boolean = false) {
    Row(Modifier.padding(vertical = 4.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontStyle = if (isItalic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
        )
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when(status.lowercase()) {
        "delivered" -> Color(0xFF2E7D32)
        "pending" -> Color(0xFFFFA000)
        else -> MaterialTheme.colorScheme.primary
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = color,
            fontWeight = FontWeight.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDeliveryDetailsScreen() {
    GGCourierGoTheme {
        DeliveryDetailsScreen(deliveryId = "DEL123")
    }
}