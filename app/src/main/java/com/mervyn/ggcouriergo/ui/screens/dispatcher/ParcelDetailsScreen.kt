package com.mervyn.ggcouriergo.ui.screens.dispatcher

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
fun ParcelDetailsScreen(
    navController: NavController? = null,
    parcelId: String = "123"
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Parcel Details") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Parcel ID: $parcelId", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))

            Text("Sender: -")
            Text("Receiver: -")
            Text("Pickup Address: -")
            Text("Dropoff Address: -")
            Text("Package Details: -")
            Text("Assigned Driver: -")
            Text("Status: -")

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { /* TODO: Navigate to edit parcel or update status */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Parcel")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewParcelDetailsScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        ParcelDetailsScreen(navController)
    }
}
