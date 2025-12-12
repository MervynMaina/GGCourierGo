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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverShiftScreen(navController: NavController? = null) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var isOnShift by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    val uid = auth.currentUser?.uid

    // Load shift status from Firestore
    LaunchedEffect(uid) {
        if (uid != null) {
            val docRef = db.collection("drivers").document(uid)
            docRef.get()
                .addOnSuccessListener { doc ->
                    isOnShift = doc?.getBoolean("onShift") ?: false
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    // Update shift status
    fun updateShiftStatus(startShift: Boolean) {
        if (uid != null) {
            db.collection("drivers").document(uid)
                .update("onShift", startShift)
                .addOnSuccessListener { isOnShift = startShift }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Driver Shift") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (isLoading) {
                CircularProgressIndicator()
                return@Column
            }

            Text(
                "Current Shift Status:",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(16.dp))

            Text(
                if (isOnShift) "ON DUTY" else "OFF DUTY",
                style = MaterialTheme.typography.titleLarge,
                color = if (isOnShift) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { updateShiftStatus(!isOnShift) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isOnShift) "End Shift" else "Start Shift")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDriverShiftScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        DriverShiftScreen(navController)
    }
}
