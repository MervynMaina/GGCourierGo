package com.mervyn.ggcouriergo.ui.screens.driver

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.navigation.routeDeliverySummary
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverParcelDetailsScreen(
    navController: NavController? = null,
    parcelId: String
) {
    val db = FirebaseFirestore.getInstance()
    var parcel by remember { mutableStateOf<Parcel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var updating by remember { mutableStateOf(false) }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Photo picker
    val context = LocalContext.current
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> selectedPhotoUri = uri }
    )

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
                    parcel = Parcel(
                        id = doc.id,
                        senderName = doc.getString("senderName") ?: "",
                        receiverName = doc.getString("receiverName") ?: "",
                        pickupAddress = doc.getString("pickupAddress") ?: "",
                        dropoffAddress = doc.getString("dropoffAddress") ?: "",
                        packageDetails = doc.getString("packageDetails") ?: "",
                        status = doc.getString("status") ?: "",
                        assignedDriver = doc.getString("assignedDriver"),
                        createdAt = doc.getLong("createdAt"),
                        deliveredAt = doc.getLong("deliveredAt"),
                        deliveryPhotoUrl = doc.getString("deliveryPhotoUrl")
                    )
                }
                isLoading = false
            }

        onDispose { listener.remove() }
    }

    fun updateStatusDelivered(photoUrl: String) {
        updating = true
        db.collection("parcels")
            .document(parcelId)
            .update(
                mapOf(
                    "status" to "delivered",
                    "deliveredAt" to System.currentTimeMillis(),
                    "deliveryPhotoUrl" to photoUrl
                )
            )
            .addOnSuccessListener {
                updating = false
                navController?.navigate(routeDeliverySummary(parcelId))
            }
            .addOnFailureListener { updating = false }
    }

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

            val data = parcel ?: run {
                Text("Parcel not found.")
                return@Column
            }

            // --- PARCEL DETAILS ---
            Text("Parcel ID: ${data.id}", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            Text("Pickup: ${data.pickupAddress}")
            Text("Dropoff: ${data.dropoffAddress}")
            Text("Receiver: ${data.receiverName}")
            Text("Package: ${data.packageDetails}")
            Spacer(Modifier.height(16.dp))
            Text("Current Status: ${data.status}", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))

            // --- ACTION BUTTONS ---
            when (data.status.lowercase()) {
                "pending" -> Button(
                    onClick = {
                        db.collection("parcels").document(parcelId)
                            .update("status", "picked_up")
                    },
                    enabled = !updating,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Mark as Picked Up") }

                "picked_up" -> Button(
                    onClick = {
                        db.collection("parcels").document(parcelId)
                            .update("status", "in_transit")
                    },
                    enabled = !updating,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Start Navigation / In Transit") }

                "in_transit" -> Column {
                    Button(
                        onClick = { photoLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Upload Delivery Photo") }

                    selectedPhotoUri?.let { uri ->
                        Spacer(Modifier.height(8.dp))
                        // Show a preview
                        Image(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "Delivery photo preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                // TODO: Upload to Cloudinary, get URL, then call updateStatusDelivered(url)
                                // For now, simulate:
                                val dummyCloudinaryUrl = "https://res.cloudinary.com/demo/image/upload/sample.jpg"
                                updateStatusDelivered(dummyCloudinaryUrl)
                            },
                            enabled = !updating,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Mark as Delivered") }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDriverParcelDetailsScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        DriverParcelDetailsScreen(navController, parcelId = "123")
    }
}
