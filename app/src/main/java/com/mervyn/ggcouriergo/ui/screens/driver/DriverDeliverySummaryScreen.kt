//package com.mervyn.ggcouriergo.ui.screens.driver
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//import com.google.firebase.firestore.FirebaseFirestore
//import com.mervyn.ggcouriergo.models.DeliverySummary
//import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme
//import kotlinx.coroutines.tasks.await
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DeliverySummaryScreen(
//    navController: NavController,
//    parcelId: String
//) {
//    val db = FirebaseFirestore.getInstance()
//
//    var summary by remember { mutableStateOf<DeliverySummary?>(null) }
//    var isLoading by remember { mutableStateOf(true) }
//
//    // Load parcel data once using coroutine
//    LaunchedEffect(parcelId) {
//        isLoading = true
//        try {
//            val doc = db.collection("parcels").document(parcelId).get().await()
//            if (doc.exists()) {
//                summary = DeliverySummary(
//                    id = doc.id,
//                    pickupAddress = doc.getString("pickupAddress") ?: "",
//                    dropoffAddress = doc.getString("dropoffAddress") ?: "",
//                    receiverName = doc.getString("receiverName") ?: "",
//                    receiverPhone = doc.getString("receiverPhone") ?: "",
//                    packageDetails = doc.getString("packageDetails") ?: "",
//                    deliveredAt = doc.getLong("deliveredAt") // optional timestamp
//                )
//            }
//        } catch (e: Exception) {
//            summary = null
//        } finally {
//            isLoading = false
//        }
//    }
//
//    Scaffold(
//        topBar = { TopAppBar(title = { Text("Delivery Summary") }) }
//    ) { paddingValues ->
//
//        Column(
//            modifier = Modifier
//                .padding(paddingValues)
//                .padding(16.dp)
//                .fillMaxSize()
//        ) {
//
//            if (isLoading) {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator()
//                }
//                return@Column
//            }
//
//            if (summary == null) {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Text("Delivery information not available.")
//                }
//                return@Column
//            }
//
//            val data = summary!!
//
//            // -------------------------
//            // SUMMARY CONTENT
//            // -------------------------
//            Text("Delivery Complete", style = MaterialTheme.typography.headlineMedium)
//            Spacer(Modifier.height(20.dp))
//
//            Text("Parcel ID: ${data.id}")
//            Spacer(Modifier.height(12.dp))
//
//            Text("Pickup Location: ${data.pickupAddress}")
//            Text("Dropoff Location: ${data.dropoffAddress}")
//            Text("Receiver: ${data.receiverName} (${data.receiverPhone})")
//            Spacer(Modifier.height(12.dp))
//
//            Text("Package: ${data.packageDetails}")
//            Spacer(Modifier.height(12.dp))
//
//            Text("Delivered Successfully!", style = MaterialTheme.typography.titleLarge)
//            Spacer(Modifier.height(24.dp))
//
//            Button(
//                onClick = {
//                    navController.navigate("driver_dashboard") {
//                        popUpTo("driver_dashboard") { inclusive = true }
//                    }
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Back to Dashboard")
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewDeliverySummaryScreen() {
//    val navController = rememberNavController()
//    CourierGoTheme {
//        DeliverySummaryScreen(
//            navController = navController,
//            parcelId = "XYZ123"
//        )
//    }
//}