package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.CreateParcelViewModel
import com.mervyn.ggcouriergo.data.CreateParcelViewModelFactory
import com.mervyn.ggcouriergo.models.CreateParcelUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateParcelScreen(
    navController: NavController,
    viewModel: CreateParcelViewModel = viewModel(
        factory = CreateParcelViewModelFactory(ParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var senderName by remember { mutableStateOf("") }
    var receiverName by remember { mutableStateOf("") }
    var receiverPhone by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf("") }
    var dropoffAddress by remember { mutableStateOf("") }
    var packageDetails by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is CreateParcelUIState.Success) {
            delay(1500)
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Shipment", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA) // Consistent grey background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // --- SECTION 1: CONTACT INFO ---
            Text(
                "Client Information",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ParcelInput(value = senderName, onValueChange = { senderName = it }, label = "Sender Name", icon = Icons.Default.Person)
                    Spacer(Modifier.height(12.dp))
                    ParcelInput(value = receiverName, onValueChange = { receiverName = it }, label = "Receiver Name", icon = Icons.Default.Badge)
                    Spacer(Modifier.height(12.dp))
                    ParcelInput(value = receiverPhone, onValueChange = { receiverPhone = it }, label = "Receiver Phone", icon = Icons.Default.Phone, keyboardType = KeyboardType.Phone)
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- SECTION 2: LOGISTICS ---
            Text(
                "Route & Package Details",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ParcelInput(value = pickupAddress, onValueChange = { pickupAddress = it }, label = "Pickup Address", icon = Icons.Default.LocationOn)
                    Spacer(Modifier.height(12.dp))
                    ParcelInput(value = dropoffAddress, onValueChange = { dropoffAddress = it }, label = "Dropoff Address", icon = Icons.Default.LocalShipping)
                    Spacer(Modifier.height(12.dp))
                    ParcelInput(
                        value = packageDetails,
                        onValueChange = { packageDetails = it },
                        label = "Weight, items, etc.",
                        icon = Icons.Default.Inventory,
                        singleLine = false,
                        modifier = Modifier.heightIn(min = 100.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- STATUS FEEDBACK ---
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                when (val state = uiState) {
                    is CreateParcelUIState.Loading -> CircularProgressIndicator()
                    is CreateParcelUIState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                    is CreateParcelUIState.Success -> Text("✓ Shipment Logged", color = Color(0xFF1B8F3A), fontWeight = FontWeight.Bold)
                    else -> {}
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- SUBMIT BUTTON ---
            Button(
                onClick = {
                    viewModel.createParcel(
                        Parcel(
                            senderName = senderName,
                            receiverName = receiverName,
                            receiverPhone = receiverPhone,
                            pickupAddress = pickupAddress,
                            dropoffAddress = dropoffAddress,
                            packageDetails = packageDetails,
                            status = "pending",
                            assignedDriver = "UNASSIGNED"
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = uiState !is CreateParcelUIState.Loading && senderName.isNotBlank() && receiverPhone.isNotBlank()
            ) {
                Text("CREATE SHIPMENT", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ParcelInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            // Border Colors
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color(0xFFE0E0E0),

            // Label Colors
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = Color.Gray,

            // Background (Container) Colors - Updated for M3 Compatibility
            focusedContainerColor = Color(0xFFFAFAFA),
            unfocusedContainerColor = Color(0xFFFAFAFA),

            // Text Colors
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateParcelScreen() {
    val navController = rememberNavController()
    GGCourierGoTheme {
        CreateParcelScreen(navController)
    }
}