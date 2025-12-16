package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.*
import com.mervyn.ggcouriergo.data.OnboardingViewModel
import com.mervyn.ggcouriergo.data.OnboardingViewModelFactory
import com.mervyn.ggcouriergo.models.OnboardingPage
import com.mervyn.ggcouriergo.models.OnboardingUIState
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    // UPDATED: Factory now receives Context to initialize the persistent Repository
    viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsState(initial = OnboardingUIState.Loading)

    val pages = remember {
        listOf(
            OnboardingPage("Welcome to Green Giant", "The most reliable way to manage and track your parcels across the city with eco-friendly logistics."),
            OnboardingPage("Real-time Tracking", "Provide customers with up-to-the-minute status updates for full transparency and trust."),
            OnboardingPage("Driver Dispatching", "Assign, manage, and monitor your delivery fleet efficiently from one unified dashboard."),
            OnboardingPage("Fast & Organized", "A streamlined workflow designed to keep every package on time and every client happy.")
        )
    }

    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White // Set to clean white for the premium brand feel
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is OnboardingUIState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top Pager Content (Illustrations and Text)
                        HorizontalPager(
                            count = pages.size,
                            state = pagerState,
                            modifier = Modifier.weight(1f)
                        ) { page ->
                            OnboardingPageContent(page = pages[page], index = page)
                        }

                        // Bottom Control Panel
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Indicator dots
                            HorizontalPagerIndicator(
                                pagerState = pagerState,
                                activeColor = MaterialTheme.colorScheme.primary,
                                inactiveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                indicatorWidth = 12.dp,
                                spacing = 8.dp,
                                modifier = Modifier.padding(bottom = 32.dp)
                            )

                            // Controls Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // SKIP BUTTON - Marks onboarding as done and exits
                                TextButton(
                                    onClick = { viewModel.completeOnboarding() },
                                ) {
                                    Text(
                                        "Skip",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }

                                // NEXT / GET STARTED BUTTON
                                Button(
                                    onClick = {
                                        scope.launch {
                                            if (pagerState.currentPage < pages.size - 1) {
                                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                            } else {
                                                // Marks onboarding as done permanently
                                                viewModel.completeOnboarding()
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.height(56.dp).padding(horizontal = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                        if (pagerState.currentPage < pages.size - 1) {
                                            Spacer(Modifier.width(8.dp))
                                            Icon(
                                                Icons.Default.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is OnboardingUIState.Finished -> {
                    // Triggers once the repository flag is saved successfully
                    LaunchedEffect(Unit) {
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage, index: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Dynamic Branded Illustration with soft green background circle
        Box(
            modifier = Modifier
                .size(240.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            OnboardingIllustration(index = index, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(56.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = (-0.5).sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp,
                color = Color.Gray
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OnboardingIllustration(index: Int, color: Color) {
    Canvas(modifier = Modifier.size(120.dp)) {
        val stroke = Stroke(width = 8f, cap = StrokeCap.Round)
        when (index) {
            0 -> { // Eco-Friendly/Globe
                drawCircle(color = color, style = stroke, radius = size.width / 2.5f)
                drawArc(color = color, startAngle = -45f, sweepAngle = 90f, useCenter = false, style = stroke,
                    topLeft = Offset(size.width * 0.15f, size.height * 0.15f), size = size * 0.7f)
            }
            1 -> { // Tracking/Radar
                drawCircle(color = color, style = stroke, radius = size.width / 4f)
                drawCircle(color = color.copy(alpha = 0.3f), style = stroke, radius = size.width / 2f)
                drawLine(color = color, start = center, end = Offset(size.width * 0.9f, size.height * 0.1f), strokeWidth = 8f)
            }
            2 -> { // Dispatching/Box
                drawRect(color = color, style = stroke, size = Size(size.width * 0.6f, size.height * 0.5f),
                    topLeft = Offset(size.width * 0.2f, size.height * 0.25f))
                drawLine(color = color, start = Offset(size.width * 0.2f, size.height * 0.45f), end = Offset(size.width * 0.8f, size.height * 0.45f), strokeWidth = 4f)
            }
            3 -> { // Fast/Checkmark
                drawLine(color = color, start = Offset(size.width * 0.2f, size.height * 0.55f),
                    end = Offset(size.width * 0.45f, size.height * 0.75f), strokeWidth = 10f, cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(size.width * 0.45f, size.height * 0.75f),
                    end = Offset(size.width * 0.85f, size.height * 0.3f), strokeWidth = 10f, cap = StrokeCap.Round)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOnboardingScreen() {
    GGCourierGoTheme {
        OnboardingScreen(navController = rememberNavController())
    }
}