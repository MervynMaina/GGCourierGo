package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme

// --------------------------------------------------
// DATA CLASS
// --------------------------------------------------
data class DriverParcelDetails(
    val id: String = "",
    val pickupAddress: String = "",
    val dropoffAddress: String = "",
    val receiverName: String = "",
    val receiverPhone: String = "",
    val packageDetails: String = "",
    val status: String = ""
)

// --------------------------------------------------
// SCREEN
// --------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverParcelDetailsScreen(
    navController: NavController? = null,
    parcelId: String
) {
    val db = FirebaseFirestore.getInstance()

    var parcel by remember { mutableStateOf<DriverParcelDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var updating by remember { mutableStateOf(false) }

    // Real-time parcel listener
    DisposableEffect(parcelId) {
        val listener = db.collection("parcels")
            .document(parcelId)
            .addSnapshotListener { doc, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }

                if (doc != null && doc.exists()) {
                    parcel = DriverParcelDetails(
                        id = doc.id,
                        pickupAddress = doc.getString("pickupAddress") ?: "",
                        dropoffAddress = doc.getString("dropoffAddress") ?: "",
                        receiverName = doc.getString("receiverName") ?: "",
                        receiverPhone = doc.getString("receiverPhone") ?: "",
                        packageDetails = doc.getString("packageDetails") ?: "",
                        status = doc.getString("status") ?: ""
                    )
                }
                isLoading = false
            }

        onDispose { listener.remove() }
    }

    // Firestore status update
    fun updateStatus(newStatus: String, navigateAfter: Boolean = false) {
        updating = true
        db.collection("parcels").document(parcelId)
            .update("status", newStatus)
            .addOnSuccessListener {
                updating = false
                if (navigateAfter) {
                    navController?.navigate("driver_delivery_summary/$parcelId")
                }
            }
            .addOnFailureListener {
                updating = false
            }
    }

    // --------------------------------------------------
    // UI LAYOUT
    // --------------------------------------------------
    Scaffold(
        topBar = { TopAppBar(title = { Text("Delivery Task") }) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            if (isLoading) {
                CircularProgressIndicator()
                return@Column
            }

            if (parcel == null) {
                Text("Parcel not found.")
                return@Column
            }

            val data = parcel!!

            // --- PARCEL DETAILS ---
            Text("Parcel ID: ${data.id}", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))

            Text("Pickup Location: ${data.pickupAddress}")
            Text("Dropoff Location: ${data.dropoffAddress}")
            Text("Receiver: ${data.receiverName} (${data.receiverPhone})")
            Text("Package: ${data.packageDetails}")
            Spacer(Modifier.height(16.dp))

            Text("Current Status: ${data.status}", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))

            // --- ACTION BUTTONS ---
            when (data.status.lowercase()) {
                "pending" -> Button(
                    onClick = { updateStatus("picked_up") },
                    enabled = !updating,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Mark as Picked Up") }

                "picked_up" -> Button(
                    onClick = { updateStatus("in_transit") },
                    enabled = !updating,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Start Navigation / In Transit") }

                "in_transit" -> Button(
                    onClick = { updateStatus("delivered", navigateAfter = true) },
                    enabled = !updating,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Mark as Delivered") }
            }
        }
    }
}

// --------------------------------------------------
// PREVIEW
// --------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewDriverParcelDetailsScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        DriverParcelDetailsScreen(navController, parcelId = "123")
    }
}
