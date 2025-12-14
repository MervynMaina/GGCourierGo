package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.mervyn.ggcouriergo.R
import com.mervyn.ggcouriergo.data.DeliverySummaryViewModel
import com.mervyn.ggcouriergo.data.DeliverySummaryViewModelFactory
import com.mervyn.ggcouriergo.models.DeliverySummary
import com.mervyn.ggcouriergo.models.DeliverySummaryUIState
import com.mervyn.ggcouriergo.repository.DriverParcelRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import java.text.SimpleDateFormat
import java.util.*

// REMOVED THE CONFLICTING TOP-LEVEL formatTimestamp function

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
        topBar = { TopAppBar(title = { Text("Delivery Complete") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when (val state = uiState) {
                is DeliverySummaryUIState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(20.dp))
                    Text("Finalizing delivery...")
                }
                is DeliverySummaryUIState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = { viewModel.loadSummary(parcelId) }) {
                        Text("Reload Summary")
                    }
                }
                is DeliverySummaryUIState.Success -> {
                    DeliverySuccessContent(data = state.summary)

                    Spacer(Modifier.height(32.dp))

                    Button(
                        // FIX: Changed "driver_dashboard" to the correct constant if available,
                        // or maintained the string if necessary for simple navigation.
                        onClick = {
                            navController.navigate("driver_dashboard") {
                                // Clear the back stack to prevent returning to the delivery flow
                                popUpTo("driver_dashboard") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Return to Dashboard", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliverySuccessContent(data: DeliverySummary) {
    // FIX: Define the helper function LOCALLY within this composable
    // This guarantees scope uniqueness and resolves the ambiguity error.
    fun formatTimestamp(timestamp: Long?): String {
        return if (timestamp != null && timestamp > 0) {
            SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
        } else {
            "N/A"
        }
    }

    Icon(
        imageVector = Icons.Filled.Done,
        contentDescription = "Success",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(64.dp)
    )
    Spacer(Modifier.height(16.dp))
    Text(
        "DELIVERY SUCCESSFUL!",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(Modifier.height(24.dp))

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Confirmation Details", style = MaterialTheme.typography.titleMedium)
            Divider(Modifier.padding(vertical = 4.dp))

            Text("Parcel ID: ${data.id}")
            Text("Receiver: ${data.receiverName}")
            Text("Dropoff: ${data.dropoffAddress}")
            // Use the locally defined formatter
            Text("Time: ${formatTimestamp(data.deliveredAt)}", fontWeight = FontWeight.SemiBold)
        }
    }

    Spacer(Modifier.height(24.dp))

    // --- Proof of Delivery (POD) Photo ---
    data.deliveryPhotoUrl?.let { url ->
        Text("Proof of Delivery Photo:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Image(
            painter = rememberAsyncImagePainter(
                model = url,
                // Placeholder/Error resource needs to be available in your R.drawable
                placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
            ),
            contentDescription = "Delivery Proof Photo",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(8.dp),
            contentScale = ContentScale.Crop
        )
    } ?: Text("No photo proof available.", color = MaterialTheme.colorScheme.error)
}

@Preview(showBackground = true)
@Composable
private fun PreviewDeliverySummaryScreen() {
    val navController = rememberNavController()
    GGCourierGoTheme {
        DeliverySummaryScreen(navController, parcelId = "XYZ123")
    }
}