package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.ParcelDetailsViewModel
import com.mervyn.ggcouriergo.data.ParcelDetailsViewModelFactory
import com.mervyn.ggcouriergo.models.Driver
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.models.ParcelDetailsUIState
import com.mervyn.ggcouriergo.repository.DriverRepository
import com.mervyn.ggcouriergo.repository.ParcelRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelDetailsScreen(
    navController: NavController,
    parcelId: String,
    viewModel: ParcelDetailsViewModel = viewModel(
        factory = ParcelDetailsViewModelFactory(ParcelRepository(), DriverRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(parcelId) {
        viewModel.loadParcelDetails(parcelId)
    }

    var selectedDriver by remember { mutableStateOf<Driver?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shipment Management", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            when (val state = uiState) {
                is ParcelDetailsUIState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ParcelDetailsUIState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadParcelDetails(parcelId) }) { Text("Retry") }
                }
                is ParcelDetailsUIState.AssignmentSuccess -> {
                    LaunchedEffect(Unit) { viewModel.loadParcelDetails(parcelId) }
                }
                is ParcelDetailsUIState.Success -> {
                    // --- PARCEL INFO CARD ---
                    InfoSectionHeader(title = "Logistics Details", icon = Icons.Default.Info)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            DetailRow(label = "Tracking ID", value = state.data.parcel.id)
                            DetailRow(label = "Current Status", value = state.data.parcel.status.uppercase(), isHighlight = true)
                            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                            DetailRow(label = "Sender", value = state.data.parcel.senderName)
                            DetailRow(label = "Receiver", value = state.data.parcel.receiverName)
                            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                            DetailRow(label = "Pickup", value = state.data.parcel.pickupAddress)
                            DetailRow(label = "Dropoff", value = state.data.parcel.dropoffAddress)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // --- ASSIGNMENT SECTION ---
                    InfoSectionHeader(title = "Driver Assignment", icon = Icons.Default.LocalShipping)
                    DriverAssignmentSection(
                        parcel = state.data.parcel,
                        availableDrivers = state.data.availableDrivers,
                        selectedDriver = selectedDriver,
                        onDriverSelected = { selectedDriver = it },
                        onAssignClicked = { driver ->
                            viewModel.assignDriverToParcel(state.data.parcel.id, driver.id)
                        }
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun InfoSectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DetailRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else Color.Black
        )
    }
}

@Composable
fun DriverAssignmentSection(
    parcel: Parcel,
    availableDrivers: List<Driver>,
    selectedDriver: Driver?,
    onDriverSelected: (Driver?) -> Unit,
    onAssignClicked: (Driver) -> Unit
) {
    val isAssigned = parcel.assignedDriver != null && parcel.assignedDriver != "UNASSIGNED"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (isAssigned) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF1B8F3A))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Shipment Assigned", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(parcel.assignedDriver ?: "", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                if (availableDrivers.isEmpty()) {
                    Text("No drivers available", color = MaterialTheme.colorScheme.error)
                } else {
                    DriverDropdown(
                        drivers = availableDrivers,
                        selectedDriver = selectedDriver,
                        onDriverSelected = onDriverSelected
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { selectedDriver?.let(onAssignClicked) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = selectedDriver != null
                    ) {
                        Text("CONFIRM ASSIGNMENT", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDropdown(
    drivers: List<Driver>,
    selectedDriver: Driver?,
    onDriverSelected: (Driver?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            readOnly = true,
            value = selectedDriver?.name ?: "Tap to select driver",
            onValueChange = {},
            label = { Text("Available Personnel") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color(0xFFE0E0E0)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            drivers.forEach { driver ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(driver.name, fontWeight = FontWeight.Bold)
                            Text("Status: ${driver.status.name}", fontSize = 11.sp, color = Color.Gray)
                        }
                    },
                    onClick = {
                        onDriverSelected(driver)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewParcelDetailsScreen() {
    val navController = rememberNavController()
    // CORRECTED THEME USAGE
    GGCourierGoTheme {
        ParcelDetailsScreen(navController, parcelId = "GG-45678")
    }
}