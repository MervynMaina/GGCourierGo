package com.mervyn.ggcouriergo.ui.screens.driver

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mervyn.ggcouriergo.data.DriverShiftViewModel
import com.mervyn.ggcouriergo.data.DriverShiftViewModelFactory
import com.mervyn.ggcouriergo.models.DriverShiftUIState
import com.mervyn.ggcouriergo.repository.DriverRepository
import com.mervyn.ggcouriergo.repository.DriverShiftRepository
import java.util.concurrent.TimeUnit

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
    modifier: Modifier = Modifier
) {
    val driverRepository = remember { DriverRepository() }
    val driverShiftRepository = remember { DriverShiftRepository(driverRepository) }
    val viewModel: DriverShiftViewModel = viewModel(
        factory = remember { DriverShiftViewModelFactory(driverShiftRepository) }
    )

    val uiState by viewModel.uiState.collectAsState()

    // Pulsating animation logic
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SHIFT MANAGER", fontWeight = FontWeight.Black, fontSize = 18.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is DriverShiftUIState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DriverShiftUIState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                is DriverShiftUIState.Success -> {
                    val shift = state.shift
                    val runningDuration = if (shift.isActive && shift.shiftStartTime != null) {
                        System.currentTimeMillis() - shift.shiftStartTime
                    } else 0L
                    val currentShiftTime = shift.accumulatedTime + runningDuration

                    Spacer(Modifier.height(20.dp))

                    // --- FIXED STATUS BADGE ---
                    // Using graphicsLayer for scale avoids the Dp * Float math error
                    Surface(
                        color = if (shift.isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .graphicsLayer {
                                if (shift.isActive) {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                }
                            }
                    ) {
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (shift.isActive) Color(0xFF2E7D32) else Color(0xFFC62828))
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (shift.isActive) "ACTIVE" else "OFF DUTY",
                                fontWeight = FontWeight.Bold,
                                color = if (shift.isActive) Color(0xFF2E7D32) else Color(0xFFC62828),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(40.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.White, Color(0xFFF1F3F4))
                                    )
                                )
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "TOTAL WORK TIME",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.Gray,
                                letterSpacing = 2.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                formatTime(currentShiftTime),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 54.sp
                                )
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = viewModel::toggleShift,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (shift.isActive) Color(0xFFC62828) else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(if (shift.isActive) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(if (shift.isActive) "END SHIFT" else "START SHIFT", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { viewModel.resetShift() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !shift.isActive
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("RESET DAY", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}