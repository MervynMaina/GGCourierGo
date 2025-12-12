package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.models.ParcelTracking
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    navController: NavController? = null,
    parcelId: String
) {
    val db = FirebaseFirestore.getInstance()

    var parcel by remember { mutableStateOf<ParcelTracking?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Real-time listener for parcel updates
    DisposableEffect(parcelId) {
        val listener = db.collection("parcels")
            .document(parcelId)
            .addSnapshotListener { doc, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }

                if (doc != null && doc.exists()) {
                    parcel = ParcelTracking(
                        id = doc.id,
                        status = doc.getString("status") ?: "",
                        currentLocation = doc.getString("currentLocation") ?: "Unknown",
                        assignedDriver = doc.getString("assignedDriver")
                    )
                }
                isLoading = false
            }

        onDispose { listener.remove() }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Parcel Tracking") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            if (parcel == null) {
                Text("Parcel not found.")
                return@Column
            }

            val data = parcel!!

            Text("Parcel ID: ${data.id}", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            Text("Status: ${data.status}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            Text("Current Location: ${data.currentLocation}", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(12.dp))

            Text("Assigned Driver: ${data.assignedDriver ?: "Not assigned"}")
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { navController?.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTrackingScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        TrackingScreen(navController, parcelId = "TRACK123")
    }
}
