package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mervyn.ggcouriergo.data.DriverShiftViewModel
import com.mervyn.ggcouriergo.data.DriverShiftViewModelFactory
// 💥 NEW IMPORT REQUIRED: DriverRepository is needed for instantiation
import com.mervyn.ggcouriergo.repository.DriverRepository
import com.mervyn.ggcouriergo.repository.DriverShiftRepository
import com.mervyn.ggcouriergo.models.DriverShiftUIState
import java.util.concurrent.TimeUnit

// Helper to format milliseconds to H:MM:SS
private fun formatTime(ms: Long): String {
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
    val hours = TimeUnit.MILLISECONDS.toHours(ms)
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverShiftScreen(
    navController: NavController,
    // 💥 FIX START: Remove default viewModel construction here
    // We will construct it inside the function body using `remember`
    // viewModel: DriverShiftViewModel = viewModel(factory = DriverShiftViewModelFactory(DriverShiftRepository()))
) {
    // 1. 💥 CRITICAL FIX: Instantiate DriverRepository (base dependency)
    val driverRepository = remember { DriverRepository() }

    // 2. 💥 CRITICAL FIX: Instantiate DriverShiftRepository, passing the base dependency
    val driverShiftRepository = remember { DriverShiftRepository(driverRepository) }

    // 3. 💥 CRITICAL FIX: Instantiate the ViewModel using the configured repository chain
    val viewModel: DriverShiftViewModel = viewModel(
        factory = remember { DriverShiftViewModelFactory(driverShiftRepository) }
    )

    val uiState by viewModel.uiState.collectAsState()

    // We don't strictly need 'now' here as the ViewModel/State should drive the UI updates,
    // but keeping it doesn't hurt.

    Scaffold(
        topBar = { TopAppBar(title = { Text("Shift Manager") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is DriverShiftUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DriverShiftUIState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is DriverShiftUIState.Success -> {
                    val shift = state.shift

                    // Calculate live duration if active
                    val runningDuration = if (shift.isActive && shift.shiftStartTime != null) {
                        System.currentTimeMillis() - shift.shiftStartTime
                    } else {
                        0L
                    }
                    val currentShiftTime = shift.accumulatedTime + runningDuration

                    Spacer(Modifier.height(32.dp))

                    // Status Indicator
                    Text(
                        if (shift.isActive) "ON DUTY" else "OFF DUTY",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (shift.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    // Clock Display Card
                    Card(
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Total Time Today", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                formatTime(currentShiftTime),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // 1. Clock In/Out Button
                    Button(
                        onClick = viewModel::toggleShift,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (shift.isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            if (shift.isActive) "CLOCK OUT" else "CLOCK IN",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // 2. NEW: Reset Button
                    OutlinedButton(
                        onClick = { viewModel.resetShift() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !shift.isActive // Prevent reset while clock is running
                    ) {
                        Text("Reset for New Day")
                    }

                    if (shift.isActive) {
                        Text(
                            "You must clock out to reset the timer.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}