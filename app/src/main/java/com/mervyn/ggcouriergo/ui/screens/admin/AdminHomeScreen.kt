package com.mervyn.ggcouriergo.ui.screens.admin

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
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme

// --------------------------------------------------
// DATA CLASSES
// --------------------------------------------------
data class User(
    val id: String = "",
    val email: String = "",
    val role: String = ""
)

data class Parcel(
    val id: String = "",
    val senderName: String = "",
    val receiverName: String = "",
    val status: String = ""
)

// --------------------------------------------------
// ADMIN DASHBOARD SCREEN
// --------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()

    var users by remember { mutableStateOf(listOf<User>()) }
    var parcels by remember { mutableStateOf(listOf<Parcel>()) }
    var isLoadingUsers by remember { mutableStateOf(true) }
    var isLoadingParcels by remember { mutableStateOf(true) }

    // Fetch users
    LaunchedEffect(Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                users = snapshot.documents.map { doc ->
                    User(
                        id = doc.id,
                        email = doc.getString("email") ?: "",
                        role = doc.getString("role") ?: "driver"
                    )
                }
                isLoadingUsers = false
            }
            .addOnFailureListener { isLoadingUsers = false }
    }

    // Fetch parcels
    LaunchedEffect(Unit) {
        db.collection("parcels")
            .get()
            .addOnSuccessListener { snapshot ->
                parcels = snapshot.documents.map { doc ->
                    Parcel(
                        id = doc.id,
                        senderName = doc.getString("senderName") ?: "",
                        receiverName = doc.getString("receiverName") ?: "",
                        status = doc.getString("status") ?: ""
                    )
                }
                isLoadingParcels = false
            }
            .addOnFailureListener { isLoadingParcels = false }
    }

    // Analytics
    val totalParcels = parcels.size
    val deliveredParcels = parcels.count { it.status.lowercase() == "delivered" }
    val pendingParcels = parcels.count { it.status.lowercase() != "delivered" }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin Dashboard") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // --- Analytics Cards ---
            Text("Analytics", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard("Total Parcels", totalParcels.toString())
                StatCard("Delivered", deliveredParcels.toString())
                StatCard("Pending", pendingParcels.toString())
            }

            Spacer(Modifier.height(24.dp))

            // --- User List ---
            Text("Registered Users", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            when {
                isLoadingUsers -> CircularProgressIndicator()
                users.isEmpty() -> Text("No users found.")
                else -> LazyColumn {
                    items(users) { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Email: ${user.email}")
                                Text("Role: ${user.role}")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- Parcels List ---
            Text("Parcels Overview", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            when {
                isLoadingParcels -> CircularProgressIndicator()
                parcels.isEmpty() -> Text("No parcels found.")
                else -> LazyColumn {
                    items(parcels) { parcel ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Parcel ID: ${parcel.id}")
                                Text("Sender: ${parcel.senderName}")
                                Text("Receiver: ${parcel.receiverName}")
                                Text("Status: ${parcel.status}")
                            }
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// STAT CARD COMPOSABLE
// --------------------------------------------------
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
// PREVIEW
// --------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewAdminDashboardScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        AdminDashboardScreen(navController)
    }
}
