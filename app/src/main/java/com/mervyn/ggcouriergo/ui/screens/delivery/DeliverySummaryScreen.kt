package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.mervyn.ggcouriergo.data.DeliverySummaryViewModel
import com.mervyn.ggcouriergo.data.DeliverySummaryViewModelFactory
import com.mervyn.ggcouriergo.models.DeliverySummary
import com.mervyn.ggcouriergo.models.DeliverySummaryUIState
import com.mervyn.ggcouriergo.repository.DriverParcelRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliverySummaryScreen(
    navController: NavController,
    parcelId: String,
    viewModel: DeliverySummaryViewModel = viewModel(
        factory = DeliverySummaryViewModelFactory(DriverParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(parcelId) {
        viewModel.loadSummary(parcelId)
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is DeliverySummaryUIState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DeliverySummaryUIState.Error -> {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadSummary(parcelId) }) { Text("Retry") }
                    }
                }
                is DeliverySummaryUIState.Success -> {
                    // --- SUCCESS ANIMATION HEADER ---
                    Spacer(Modifier.height(40.dp))
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = Color(0xFFE8F5E9)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Job Well Done!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        "Delivery successfully logged",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(32.dp))

                    // --- RECEIPT CONTENT ---
                    DeliverySuccessContent(data = state.summary)

                    Spacer(Modifier.height(32.dp))

                    // --- NAVIGATION ---
                    Button(
                        onClick = {
                            navController.navigate("driver_dashboard") {
                                popUpTo("driver_dashboard") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("BACK TO DASHBOARD", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun DeliverySuccessContent(data: DeliverySummary) {
    fun formatTimestamp(timestamp: Long?): String {
        return if (timestamp != null && timestamp > 0) {
            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))
        } else {
            "N/A"
        }
    }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        // --- SUMMARY CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Confirmation Details", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.height(16.dp))

                SummaryRow(label = "Parcel ID", value = "#${data.id.takeLast(8)}")
                SummaryRow(label = "Receiver", value = data.receiverName)
                SummaryRow(label = "Dropoff", value = data.dropoffAddress)
                SummaryRow(label = "Time Completed", value = formatTimestamp(data.deliveredAt), isHighlight = true)
            }
        }

        Spacer(Modifier.height(24.dp))

        // --- PHOTO PROOF ---
        Text(
            "Visual Proof of Delivery",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            data.deliveryPhotoUrl?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(model = url),
                    contentDescription = "POD",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                Modifier.fillMaxWidth().height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No photo proof available", color = Color.LightGray)
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun PreviewDeliverySummaryScreen() {
    val navController = rememberNavController()
    GGCourierGoTheme {
        DeliverySummaryScreen(navController, parcelId = "XYZ123")
    }
}