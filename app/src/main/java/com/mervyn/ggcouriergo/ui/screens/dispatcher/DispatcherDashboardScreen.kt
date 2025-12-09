package com.mervyn.ggcouriergo.ui.screens.dispatcher

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mervyn.ggcouriergo.navigation.CreateParcelRoute
import com.mervyn.ggcouriergo.navigation.parcelDetailsRoute
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme

// --------------------------------------------------
// DATA MODEL
// --------------------------------------------------
data class Parcel(
    val id: String = "",
    val senderName: String = "",
    val receiverName: String = "",
    val pickupAddress: String = "",
    val dropoffAddress: String = "",
    val status: String = "",
    val assignedDriver: String? = null
)

// --------------------------------------------------
// MAIN SCREEN
// --------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatcherDashboardScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    var parcels by remember { mutableStateOf(listOf<Parcel>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Listen to unassigned parcels in real time
    DisposableEffect(Unit) {
        val listener = db.collection("parcels")
            .whereEqualTo("assignedDriver", null)
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
                        senderName = doc.getString("senderName") ?: "",
                        receiverName = doc.getString("receiverName") ?: "",
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
    // UI Layout
    // --------------------------------------------------
    Scaffold(
        topBar = { TopAppBar(title = { Text("Dispatcher Dashboard") }) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text("Welcome, Dispatcher!", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))

            OverviewStatsCards()
            Spacer(Modifier.height(24.dp))

            Text("Unassigned Deliveries", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

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
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No unassigned deliveries available.")
                    }
                }

                else -> {
                    LazyColumn {
                        items(parcels) { parcel ->
                            AssignableDeliveryCard(
                                parcel = parcel,
                                onAssignClick = {
                                    navController.navigate(parcelDetailsRoute(parcel.id))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(CreateParcelRoute) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create New Parcel")
            }
        }
    }
}

// --------------------------------------------------
// STATS CARDS
// --------------------------------------------------
@Composable
fun OverviewStatsCards() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatCard(title = "Active Drivers", value = "8")
        StatCard(title = "Deliveries Today", value = "42")
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

// --------------------------------------------------
// DELIVERY CARD
// --------------------------------------------------
@Composable
fun AssignableDeliveryCard(parcel: Parcel, onAssignClick: () -> Unit) {
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
            Text("Sender: ${parcel.senderName}")
            Text("Receiver: ${parcel.receiverName}")

            Spacer(Modifier.height(8.dp))

            Button(onClick = onAssignClick, modifier = Modifier.fillMaxWidth()) {
                Text("View / Assign")
            }
        }
    }
}

// --------------------------------------------------
// PREVIEW
// --------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewDispatcherDashboard() {
    val navController = rememberNavController()
    CourierGoTheme {
        DispatcherDashboardScreen(navController)
    }
}
