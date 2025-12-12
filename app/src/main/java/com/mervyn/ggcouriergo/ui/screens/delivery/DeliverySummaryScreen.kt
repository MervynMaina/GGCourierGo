package com.mervyn.ggcouriergo.ui.screens.delivery

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.R
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliverySummaryScreen(
    navController: NavController? = null,
    parcelId: String
) {
    val db = FirebaseFirestore.getInstance()
    var parcel by remember { mutableStateOf<Parcel?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Load parcel data once
    LaunchedEffect(parcelId) {
        db.collection("parcels").document(parcelId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    parcel = Parcel(
                        id = doc.id,
                        senderName = doc.getString("senderName") ?: "",
                        receiverName = doc.getString("receiverName") ?: "",
                        pickupAddress = doc.getString("pickupAddress") ?: "",
                        dropoffAddress = doc.getString("dropoffAddress") ?: "",
                        packageDetails = doc.getString("packageDetails") ?: "",
                        status = doc.getString("status") ?: "",
                        deliveredAt = doc.getLong("deliveredAt"),
                        deliveryPhotoUrl = doc.getString("deliveryPhotoUrl")
                    )
                }
                isLoading = false
            }
            .addOnFailureListener { isLoading = false }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Delivery Summary") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            val data = parcel ?: run {
                Text("Delivery info not available.")
                return@Column
            }

            Text("Parcel ID: ${data.id}", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Pickup: ${data.pickupAddress}")
            Text("Dropoff: ${data.dropoffAddress}")
            Text("Receiver: ${data.receiverName}")
            Text("Package: ${data.packageDetails}")
            Spacer(Modifier.height(8.dp))
            Text("Status: ${data.status}", style = MaterialTheme.typography.titleMedium)

            data.deliveredAt?.let {
                val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                Text("Delivered At: ${sdf.format(Date(it))}")
            }

            Spacer(Modifier.height(16.dp))

            // Delivery Photo
            data.deliveryPhotoUrl?.let {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = it,
                        placeholder = painterResource(R.drawable.ic_launcher_foreground),
                        error = painterResource(R.drawable.ic_launcher_foreground)
                    ),
                    contentDescription = "Delivery Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    navController?.navigate("driver_dashboard") {
                        popUpTo("driver_dashboard") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Dashboard")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDeliverySummaryScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        DeliverySummaryScreen(navController, parcelId = "XYZ123")
    }
}
