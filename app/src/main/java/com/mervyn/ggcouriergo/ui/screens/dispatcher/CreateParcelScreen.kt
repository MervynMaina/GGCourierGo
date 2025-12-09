package com.mervyn.ggcouriergo.ui.screens.dispatcher

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
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateParcelScreen(navController: NavController? = null) {
    var senderName by remember { mutableStateOf("") }
    var senderPhone by remember { mutableStateOf("") }
    var receiverName by remember { mutableStateOf("") }
    var receiverPhone by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf("") }
    var dropoffAddress by remember { mutableStateOf("") }
    var packageDetails by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Create Parcel") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Parcel Information", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))

            // Input fields
            OutlinedTextField(
                value = senderName,
                onValueChange = { senderName = it },
                label = { Text("Sender Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = senderPhone,
                onValueChange = { senderPhone = it },
                label = { Text("Sender Phone") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = receiverName,
                onValueChange = { receiverName = it },
                label = { Text("Receiver Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = receiverPhone,
                onValueChange = { receiverPhone = it },
                label = { Text("Receiver Phone") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = pickupAddress,
                onValueChange = { pickupAddress = it },
                label = { Text("Pickup Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = dropoffAddress,
                onValueChange = { dropoffAddress = it },
                label = { Text("Dropoff Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = packageDetails,
                onValueChange = { packageDetails = it },
                label = { Text("Package Details") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (senderName.isBlank() || receiverName.isBlank() || pickupAddress.isBlank() || dropoffAddress.isBlank()) {
                        errorMessage = "Please fill all required fields"
                        return@Button
                    }

                    isLoading = true
                    val parcelId = UUID.randomUUID().toString()
                    val parcelData = mapOf(
                        "id" to parcelId,
                        "senderName" to senderName,
                        "senderPhone" to senderPhone,
                        "receiverName" to receiverName,
                        "receiverPhone" to receiverPhone,
                        "pickupAddress" to pickupAddress,
                        "dropoffAddress" to dropoffAddress,
                        "packageDetails" to packageDetails,
                        "assignedDriver" to null,
                        "status" to "pending",
                        "createdAt" to System.currentTimeMillis()
                    )

                    db.collection("parcels").document(parcelId)
                        .set(parcelData)
                        .addOnSuccessListener {
                            isLoading = false
                            navController?.popBackStack() // return to dashboard
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            errorMessage = e.message ?: "Failed to create parcel"
                        }

                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create Parcel")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateParcelScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        CreateParcelScreen(navController)
    }
}
