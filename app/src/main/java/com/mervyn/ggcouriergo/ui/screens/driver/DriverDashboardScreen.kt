package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mervyn.ggcouriergo.navigation.driverParcelDetailsRoute
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme

// --------------------------------------------------
// DATA CLASS
// --------------------------------------------------
data class Parcel(
    val id: String = "",
    val pickupAddress: String = "",
    val dropoffAddress: String = "",
    val status: String = "",
    val assignedDriver: String? = null
)

// --------------------------------------------------
// DRIVER DASHBOARD SCREEN
// --------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var parcels by remember { mutableStateOf(listOf<Parcel>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Real-time listener for driver's assigned parcels
    DisposableEffect(auth.currentUser?.uid) {

        val uid = auth.currentUser?.uid
        if (uid == null) {
            parcels = emptyList()
            isLoading = false
            return@DisposableEffect onDispose { }
        }

        val listener = db.collection("parcels")
            .whereEqualTo("assignedDriver", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    parcels = emptyList()
                    isLoading = false
                    return@addSnapshotListener
                }

                parcels = snapshot?.documents?.map { doc ->
                    Parcel(
                        id = doc.id,
                        pickupAddress = doc.getString("pickupAddress") ?: "",
                        dropoffAddress = doc.getString("dropoffAddress") ?: "",
                        status = doc.getString("status") ?: "",
                        assignedDriver = doc.getString("assignedDriver")
                    )
                } ?: emptyList()

                isLoading = false
            }

        onDispose { listener.remove() }
    }

    // --------------------------------------------------
    // UI CONTENT
    // --------------------------------------------------
    Scaffold(
        topBar = { TopAppBar(title = { Text("Driver Dashboard") }) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text("Welcome, Driver!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))

            // --------------------------------------------------
            // SHIFT STATUS
            // --------------------------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Shift Status", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    Text("You are currently OFF duty.")
                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = { /* TODO: Implement Start Shift */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Shift")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --------------------------------------------------
            // ASSIGNED DELIVERIES
            // --------------------------------------------------
            Text("Assigned Deliveries", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                parcels.isEmpty() -> {
                    Text("No assigned deliveries.")
                }

                else -> {
                    LazyColumn {
                        items(parcels) { parcel ->
                            DeliveryItemCard(
                                parcel = parcel,
                                onStartClick = {
                                    navController.navigate(driverParcelDetailsRoute(parcel.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// DELIVERY CARD
// --------------------------------------------------
@Composable
fun DeliveryItemCard(parcel: Parcel, onStartClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text("Parcel: ${parcel.id}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))

            Text("Pickup: ${parcel.pickupAddress}")
            Text("Dropoff: ${parcel.dropoffAddress}")
            Text("Status: ${parcel.status}")

            Spacer(Modifier.height(8.dp))

            Button(onClick = onStartClick, modifier = Modifier.fillMaxWidth()) {
                Text("View / Start Delivery")
            }
        }
    }
}

// --------------------------------------------------
// PREVIEW
// --------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewDriverDashboard() {
    val navController = rememberNavController()
    CourierGoTheme {
        DriverDashboardScreen(navController)
    }
}
