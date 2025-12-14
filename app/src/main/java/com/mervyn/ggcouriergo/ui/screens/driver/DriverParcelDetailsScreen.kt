package com.mervyn.ggcouriergo.ui.screens.driver

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.DriverParcelDetailsViewModel
import com.mervyn.ggcouriergo.data.DriverParcelDetailsViewModelFactory
import com.mervyn.ggcouriergo.models.DriverParcelDetails
import com.mervyn.ggcouriergo.models.DriverParcelDetailsUIState
import com.mervyn.ggcouriergo.navigation.routeDeliverySummary
import com.mervyn.ggcouriergo.repository.DriverParcelRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverParcelDetailsScreen(
    navController: NavController,
    parcelId: String,
    viewModel: DriverParcelDetailsViewModel = viewModel(
        factory = DriverParcelDetailsViewModelFactory(DriverParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(parcelId) {
        viewModel.loadParcel(parcelId)
    }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> selectedPhotoUri = uri }
    )

    fun handleFinalDelivery(data: DriverParcelDetails) {
        // --- SIMULATION: In a real app, the photo would be uploaded here ---
        val dummyCloudinaryUrl = "https://example.com/delivery_proof/${data.id}.jpg"

        // NOTE: A real update function should include the photo URL and deliveredAt timestamp.
        // Since the current repository updateStatus is simplified, we rely on the next screen (Summary)
        // to handle the final state. For the navigation, we proceed.

        viewModel.updateStatus(data.id, "delivered") {
            navController.navigate(routeDeliverySummary(data.id))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delivery: $parcelId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            when (val state = uiState) {
                is DriverParcelDetailsUIState.Loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(20.dp))
                    Text("Loading delivery details...")
                }
                is DriverParcelDetailsUIState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = { viewModel.loadParcel(parcelId) }) {
                        Text("Reload")
                    }
                }
                is DriverParcelDetailsUIState.Success -> {
                    val data = state.parcel

                    DeliveryDetailsCard(data)

                    Spacer(Modifier.height(24.dp))

                    DeliveryActionButton(
                        parcel = data,
                        onStatusUpdate = { newStatus -> viewModel.updateStatus(data.id, newStatus) },
                        onPhotoSelect = { photoLauncher.launch("image/*") },
                        onFinalizeDelivery = { handleFinalDelivery(data) },
                        selectedPhotoUri = selectedPhotoUri,
                        isLoading = uiState is DriverParcelDetailsUIState.Loading
                    )
                }
            }
        }
    }
}

@Composable
fun DeliveryDetailsCard(data: DriverParcelDetails) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Delivery Information", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Divider()

            DeliveryDetailRow(label = "Status", value = data.status.uppercase(), isStatus = true)
            DeliveryDetailRow(label = "Pickup", value = data.pickupAddress)
            DeliveryDetailRow(label = "Dropoff", value = data.dropoffAddress)

            Spacer(Modifier.height(16.dp))
            Text("Receiver Details", style = MaterialTheme.typography.titleMedium)
            DeliveryDetailRow(label = "Name", value = data.receiverName)
            DeliveryDetailRow(label = "Phone", value = data.receiverPhone)

            Spacer(Modifier.height(16.dp))
            Text("Package Details", style = MaterialTheme.typography.titleMedium)
            Text(data.packageDetails, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun DeliveryDetailRow(label: String, value: String, isStatus: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isStatus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

// Helper to format timestamp
fun formatTimestamp(timestamp: Long?): String {
    return if (timestamp != null && timestamp > 0) {
        SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
    } else {
        "N/A"
    }
}

@Composable
fun DeliveryActionButton(
    parcel: DriverParcelDetails,
    onStatusUpdate: (String) -> Unit,
    onPhotoSelect: () -> Unit,
    onFinalizeDelivery: () -> Unit,
    selectedPhotoUri: Uri?,
    isLoading: Boolean
) {
    val buttonEnabled = !isLoading

    when (parcel.status.lowercase()) {
        "assigned" -> Button(
            onClick = { onStatusUpdate("picked_up") },
            enabled = buttonEnabled,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Confirm Parcel Picked Up") }

        "picked_up" -> Button(
            onClick = { onStatusUpdate("in_transit") },
            enabled = buttonEnabled,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Start Dropoff / In Transit") }

        "in_transit" -> Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Final Step: Confirm Dropoff Location", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onPhotoSelect,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) { Text(if (selectedPhotoUri == null) "Take/Select Delivery Photo" else "Change Photo") }

            selectedPhotoUri?.let {
                // FIX: Using a standard Android system icon/resource
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = "Delivery photo preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Crop
                )

                Button(
                    onClick = onFinalizeDelivery,
                    enabled = buttonEnabled,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Mark as Delivered") }
            } ?: Text("Photo proof is required to finalize delivery.")
        }
        "delivered" -> Text(
            // FIX: Use the nullable property with the helper function
            "Delivery Completed on ${formatTimestamp(parcel.deliveredAt)}",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall
        )
        else -> Text("Status: ${parcel.status}. No immediate actions available.")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDriverParcelDetailsScreen() {
    val navController = rememberNavController()
    GGCourierGoTheme {
        val mockData = DriverParcelDetails(
            id = "GG-1001",
            pickupAddress = "123 Sender St, City, Country",
            dropoffAddress = "456 Receiver Ave, City, Country",
            receiverName = "John Doe",
            receiverPhone = "+1-555-1234",
            packageDetails = "Small Box, fragile contents.",
            status = "in_transit",
            deliveredAt = null, // Mocking a pending delivery
            deliveryPhotoUrl = null
        )
        Column {
            DeliveryDetailsCard(mockData)
            Spacer(Modifier.height(20.dp))
            DeliveryActionButton(
                parcel = mockData,
                onStatusUpdate = {},
                onPhotoSelect = {},
                onFinalizeDelivery = {},
                selectedPhotoUri = null,
                isLoading = false
            )
        }
    }
}