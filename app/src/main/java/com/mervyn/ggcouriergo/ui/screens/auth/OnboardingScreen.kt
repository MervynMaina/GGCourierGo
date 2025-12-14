package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.*
import com.mervyn.ggcouriergo.data.OnboardingViewModel
import com.mervyn.ggcouriergo.data.OnboardingViewModelFactory
import com.mervyn.ggcouriergo.models.OnboardingPage
import com.mervyn.ggcouriergo.models.OnboardingUIState
import com.mervyn.ggcouriergo.repository.OnboardingRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(OnboardingRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState(initial = OnboardingUIState.Loading)

    // Using a list of OnboardingPage objects directly within the Composable for simplicity
    val pages = remember {
        listOf(
            OnboardingPage("Welcome to CourierGo", "The most reliable way to manage and track your parcels across the city."),
            OnboardingPage("Realtime Tracking", "Provide customers with up-to-the-minute status updates for full transparency."),
            OnboardingPage("Driver Dispatching", "Assign, manage, and monitor your delivery fleet efficiently from one dashboard."),
            OnboardingPage("Fast & Organized", "A streamlined workflow designed to keep every package on time and every client happy.")
        )
    }

    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background // Uses GrayBackground
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
                        // Top Pager Content (Takes up most space)
                        HorizontalPager(
                            count = pages.size,
                            state = pagerState,
                            modifier = Modifier.weight(1f) // Ensures content fills available vertical space
                        ) { page ->
                            OnboardingPageContent(page = pages[page])
                        }

                        // Bottom Control Panel
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HorizontalPagerIndicator(
                                pagerState = pagerState,
                                activeColor = MaterialTheme.colorScheme.primary, // Green Primary
                                inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // SKIP Button
                                TextButton(
                                    onClick = { viewModel.completeOnboarding() }, // Removed userId
                                ) {
                                    Text("Skip", color = MaterialTheme.colorScheme.onBackground)
                                }

                                // NEXT / FINISH Button
                                Button(
                                    onClick = {
                                        scope.launch {
                                            if (pagerState.currentPage < pages.size - 1) {
                                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                            } else {
                                                viewModel.completeOnboarding() // Removed userId
                                            }
                                        }
                                    },
                                    // Use GreenPrimary for the main action button
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Text(
                                        if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                is OnboardingUIState.Finished -> {
                    LaunchedEffect(Unit) { // Keyed on Unit for one-time execution
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
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 64.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image Placeholder - Branded Icon Look
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface, // Use GraySurface for the container
                    shape = MaterialTheme.shapes.extraLarge // Use rounded corners for modern look
                ),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for an Icon or Lottie animation related to the page content
            Text(
                "Icon Placeholder",
                color = MaterialTheme.colorScheme.primary, // Green text inside the box
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            // Replace with: Image(painter = painterResource(id = R.drawable.ic_onboard_track), contentDescription = null, ...)
        }
        Spacer(Modifier.height(48.dp))

        // Title - Use Primary Color for emphasis
        Text(
            page.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary // Green Title
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        // Description
        Text(
            page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOnboardingScreen() {
    GGCourierGoTheme {
        OnboardingScreen(navController = rememberNavController())
    }
}