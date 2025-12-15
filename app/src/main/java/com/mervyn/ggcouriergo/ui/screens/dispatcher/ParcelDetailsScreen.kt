package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

    // 1. Load data on first launch
    LaunchedEffect(parcelId) {
        viewModel.loadParcelDetails(parcelId)
    }

    // 2. Local state for driver selection
    var expanded by remember { mutableStateOf(false) }
    var selectedDriver by remember { mutableStateOf<Driver?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parcel: $parcelId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // --- UI STATE HANDLING ---
            when (val state = uiState) {
                is ParcelDetailsUIState.Loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(20.dp))
                    Text("Loading parcel and available drivers...")
                }
                is ParcelDetailsUIState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = { viewModel.loadParcelDetails(parcelId) }) {
                        Text("Retry Load")
                    }
                }
                is ParcelDetailsUIState.AssignmentSuccess -> {
                    Text("Assignment Successful!", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(10.dp))
                    // Automatically reload the details to show the assigned status
                    LaunchedEffect(Unit) {
                        viewModel.loadParcelDetails(parcelId)
                    }
                }
                is ParcelDetailsUIState.Success -> {
                    DisplayParcelDetails(parcel = state.data.parcel)
                    Spacer(Modifier.height(32.dp))
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
                else -> {
                    Text("Ready to load data.")
                }
            }
        }
    }
}

// Helper Composable to display core parcel data
@Composable
fun DisplayParcelDetails(parcel: Parcel) {
    Text("Parcel Details", style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(10.dp))
    DetailRow(label = "ID", value = parcel.id)
    DetailRow(label = "Status", value = parcel.status.uppercase())
    Divider(Modifier.padding(vertical = 8.dp))
    DetailRow(label = "Sender", value = parcel.senderName)
    DetailRow(label = "Receiver", value = parcel.receiverName)
    Divider(Modifier.padding(vertical = 8.dp))
    DetailRow(label = "Pickup", value = parcel.pickupAddress)
    DetailRow(label = "Dropoff", value = parcel.dropoffAddress)
    DetailRow(label = "Package Info", value = parcel.packageDetails)
    DetailRow(label = "Assigned Driver", value = parcel.assignedDriver ?: "UNASSIGNED", isAssigned = parcel.assignedDriver != null)
}

// Helper Composable for assignment logic
// Helper Composable for assignment logic
@Composable
fun DriverAssignmentSection(
    parcel: Parcel,
    availableDrivers: List<Driver>,
    selectedDriver: Driver?,
    onDriverSelected: (Driver?) -> Unit,
    onAssignClicked: (Driver) -> Unit
) {
    // Corrected logic: Assigned only if not null AND not the placeholder.
    val isAssigned = parcel.assignedDriver != null && parcel.assignedDriver != "UNASSIGNED"

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isAssigned) "Driver: ${parcel.assignedDriver}" else "Assign a Driver",
                style = MaterialTheme.typography.titleLarge,
                color = if (isAssigned) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )

            if (!isAssigned) {
                Spacer(Modifier.height(16.dp))
                if (availableDrivers.isEmpty()) {
                    Text("No drivers currently available for assignment.", color = MaterialTheme.colorScheme.error)
                } else {
                    // 💥 FIX: CALL THE DROPDOWN HERE
                    DriverDropdown(
                        drivers = availableDrivers,
                        selectedDriver = selectedDriver,
                        onDriverSelected = onDriverSelected
                    )

                    Spacer(Modifier.height(16.dp))

                    // 💥 FIX: CALL THE BUTTON HERE
                    Button(
                        onClick = { selectedDriver?.let(onAssignClicked) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedDriver != null
                    ) {
                        Text("Assign Driver")
                    }
                }
            } else {
                Spacer(Modifier.height(16.dp))
                Text("This parcel is currently in transit/assigned.")
            }
        }
    }
}

// Helper for displaying a single detail row
@Composable
fun DetailRow(label: String, value: String, isAssigned: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isAssigned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}


// Helper for the driver selection dropdown (REVISED FOR ANCHORING)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Helper for the driver selection dropdown (REVISED FOR RELIABILITY)
fun DriverDropdown(
    drivers: List<Driver>,
    selectedDriver: Driver?,
    onDriverSelected: (Driver?) -> Unit
) {
    // 1. Local state to control the menu visibility
    var expanded by remember { mutableStateOf(false) }

    // 2. The core component: ExposedDropdownMenuBox
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        // 3. The input field that anchors the menu (clickable and displays current selection)
        OutlinedTextField(
            // Use menuAnchor() modifier provided by the ExposedDropdownMenuBox scope
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            readOnly = true, // The user cannot type here, only select from menu
            value = selectedDriver?.name ?: "Select Driver",
            onValueChange = { /* readOnly is true, so this won't be used */ },
            label = { Text("Available Drivers") },
            trailingIcon = {
                // Trailing icon clearly indicates a dropdown
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        // 4. The dropdown menu itself
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            drivers.forEach { driver ->
                DropdownMenuItem(
                    text = { Text("${driver.name} (Status: ${driver.status.name})") },
                    onClick = {
                        onDriverSelected(driver)
                        expanded = false // Close the menu on selection
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
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