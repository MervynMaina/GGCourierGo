package com.mervyn.ggcouriergo.ui.screens.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDetailsScreen(navController: NavController? = null, deliveryId: String = "123") {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Delivery Details") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Delivery ID: $deliveryId", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))

            Text("Parcel ID: -")
            Text("Driver: -")
            Text("Status: -")
            Text("Pickup Time: -")
            Text("Delivery Time: -")
            Text("Current Location: -")

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { /* TODO: Open full parcel details */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Full Details")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDeliveryDetailsScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        DeliveryDetailsScreen(navController)
    }
}
