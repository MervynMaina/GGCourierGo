package com.mervyn.ggcouriergo.ui.screens.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.DeliveryDetailViewModel
import com.mervyn.ggcouriergo.data.DeliveryDetailViewModelFactory
import com.mervyn.ggcouriergo.models.DeliveryDetailUIState
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDetailsScreen(
    navController: NavController? = null,
    deliveryId: String,    // must match navigation argument
    viewModel: DeliveryDetailViewModel = viewModel(
        factory = DeliveryDetailViewModelFactory(deliveryId)
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Delivery Details") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            when (uiState) {

                is DeliveryDetailUIState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is DeliveryDetailUIState.Error -> {
                    Text(
                        (uiState as DeliveryDetailUIState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is DeliveryDetailUIState.Success -> {
                    val data = (uiState as DeliveryDetailUIState.Success).detail

                    Text(
                        "Delivery ID: ${data.id}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(16.dp))

                    Text("Pickup: ${data.pickupAddress}")
                    Text("Dropoff: ${data.dropoffAddress}")
                    Text("Sender: ${data.senderName}")
                    Text("Receiver: ${data.receiverName} (${data.receiverPhone})")
                    Text("Package Details: ${data.packageDetails}")
                    Text("Status: ${data.status}")
                    Text("Assigned Driver: ${data.assignedDriver ?: "Not assigned"}")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDeliveryDetailsScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        DeliveryDetailsScreen(
            navController = navController,
            deliveryId = "DEL123"
        )
    }
}
