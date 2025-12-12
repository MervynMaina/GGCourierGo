package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.CreateParcelViewModel
import com.mervyn.ggcouriergo.data.CreateParcelViewModelFactory
import com.mervyn.ggcouriergo.models.CreateParcelUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateParcelScreen(
    navController: NavController,
    viewModel: CreateParcelViewModel = viewModel(
        factory = CreateParcelViewModelFactory(ParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    var senderName by remember { mutableStateOf("") }
    var receiverName by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf("") }
    var dropoffAddress by remember { mutableStateOf("") }

    // Navigate back on successful creation
    LaunchedEffect(uiState) {
        if (uiState is CreateParcelUIState.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Create Parcel") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = senderName,
                onValueChange = { senderName = it },
                label = { Text("Sender Name") },
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

            Spacer(Modifier.height(16.dp))

            when (uiState) {
                is CreateParcelUIState.Loading -> CircularProgressIndicator()
                is CreateParcelUIState.Error -> Text(
                    (uiState as CreateParcelUIState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                else -> {}
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.createParcel(
                        Parcel(
                            senderName = senderName,
                            receiverName = receiverName,
                            pickupAddress = pickupAddress,
                            dropoffAddress = dropoffAddress,
                            status = "pending"
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState != CreateParcelUIState.Loading
            ) {
                Text("Create Parcel")
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
