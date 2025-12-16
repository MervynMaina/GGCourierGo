package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mervyn.ggcouriergo.data.AssignedParcelsViewModel
import com.mervyn.ggcouriergo.data.AssignedParcelsViewModelFactory
import com.mervyn.ggcouriergo.data.NewParcelsViewModel
import com.mervyn.ggcouriergo.data.NewParcelsViewModelFactory
import com.mervyn.ggcouriergo.models.AssignedParcelsUIState
import com.mervyn.ggcouriergo.models.NewParcelsUIState
import com.mervyn.ggcouriergo.repository.ParcelRepository

@Composable
fun DispatcherMapViewTab(
    newViewModel: NewParcelsViewModel = viewModel(factory = NewParcelsViewModelFactory(ParcelRepository())),
    assignedViewModel: AssignedParcelsViewModel = viewModel(factory = AssignedParcelsViewModelFactory(ParcelRepository()))
) {
    val newParcelsState by newViewModel.uiState.collectAsState()
    val assignedParcelsState by assignedViewModel.uiState.collectAsState()

    // Extract raw numbers from the Success states
    val pendingCount = (newParcelsState as? NewParcelsUIState.Success)?.parcels?.size ?: 0
    val activeCount = (assignedParcelsState as? AssignedParcelsUIState.Success)?.parcels?.size ?: 0
    val totalLoad = pendingCount + activeCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            "Fleet Intelligence",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1B5E20) // Deep Green
        )
        Text(
            "Real-time operational overview",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(Modifier.height(24.dp))

        // --- STAT CARDS ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                label = "Pending",
                value = pendingCount.toString(),
                containerColor = Color(0xFFFFEBEE),
                contentColor = Color(0xFFC62828),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "In-Transit",
                value = activeCount.toString(),
                containerColor = Color(0xFFE8F5E9),
                contentColor = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        // --- LOAD PROGRESS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Delivery Progress", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(Modifier.height(16.dp))

                val progress = if (totalLoad > 0) activeCount.toFloat() / totalLoad.toFloat() else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFE0E0E0),
                    strokeCap = StrokeCap.Round
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = if (totalLoad > 0)
                        "${(progress * 100).toInt()}% of today's load is currently dispatched"
                    else "No active shipments to track",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // --- SYSTEM INFRASTRUCTURE ---
        Text("Infrastructure Status", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(Modifier.height(12.dp))

        StatusRow("Cloud Database", "CONNECTED", Icons.Default.CloudDone, Color(0xFF4CAF50))
        StatusRow("GPS Satellite", "STANDBY", Icons.Default.GpsFixed, Color(0xFFFFA000))

        Spacer(Modifier.height(40.dp))

        // --- FOOTER BRANDING ---
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Surface(
                color = Color.LightGray.copy(alpha = 0.2f),
                shape = CircleShape
            ) {
                Text(
                    "GG COURIER GO • SYSTEM SECURE",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun StatCard(label: String, value: String, containerColor: Color, contentColor: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = contentColor.copy(alpha = 0.7f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            Text(value, color = contentColor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun StatusRow(service: String, status: String, icon: ImageVector, statusColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(service, modifier = Modifier.weight(1f), color = Color.DarkGray)
        Text(status, color = statusColor, fontWeight = FontWeight.Black, fontSize = 12.sp)
    }
}