package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.DriverShiftViewModel
import com.mervyn.ggcouriergo.data.DriverShiftViewModelFactory
import com.mervyn.ggcouriergo.models.DriverShiftUIState
import com.mervyn.ggcouriergo.repository.DriverShiftRepository
import java.util.concurrent.TimeUnit
import com.mervyn.ggcouriergo.navigation.ROUT_DRIVER_DASHBOARD // For navigation

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
    viewModel: DriverShiftViewModel = viewModel(
        factory = DriverShiftViewModelFactory(DriverShiftRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val now = remember { System.currentTimeMillis() } // Time reference for running clock

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
            when (val state = uiState) {
                is DriverShiftUIState.Loading -> {
                    CircularProgressIndicator()
                }
                is DriverShiftUIState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                is DriverShiftUIState.Success -> {
                    val shift = state.shift
                    val runningDuration = if (shift.isActive && shift.shiftStartTime != null) {
                        // Calculate live running time using current system time
                        System.currentTimeMillis() - shift.shiftStartTime
                    } else {
                        0L
                    }
                    val currentShiftTime = shift.accumulatedTime + runningDuration

                    Spacer(Modifier.height(32.dp))

                    Text(
                        if (shift.isActive) "SHIFT CLOCKED IN" else "SHIFT OFF DUTY",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (shift.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    // Clock Display Card
                    Card(modifier = Modifier.fillMaxWidth().height(150.dp)) {
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
                                    color = if (shift.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // Toggle Button
                    Button(
                        onClick = viewModel::toggleShift,
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (shift.isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            if (shift.isActive) "CLOCK OUT" else "CLOCK IN",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}