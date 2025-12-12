package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    val pages = listOf(
        OnboardingPage("Welcome to CourierGo", "Manage deliveries easily and reliably"),
        OnboardingPage("Track Deliveries", "Realtime tracking for all your parcels"),
        OnboardingPage("Manage Orders & Drivers", "Dispatch and monitor drivers efficiently"),
        OnboardingPage("Fast & Smooth Workflow", "Keep deliveries on time and organized")
    )

    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    Scaffold { paddingValues ->
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
                        HorizontalPager(
                            count = pages.size,
                            state = pagerState,
                            modifier = Modifier.weight(1f)
                        ) { page ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(pages[page].title, style = MaterialTheme.typography.headlineMedium)
                                Spacer(Modifier.height(16.dp))
                                Text(pages[page].description, style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.height(32.dp))
                                Box(
                                    modifier = Modifier
                                        .size(150.dp)
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Image\nPlaceholder",
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        HorizontalPagerIndicator(
                            pagerState = pagerState,
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = {
                                viewModel.completeOnboarding("dummyUserId")
                            }) {
                                Text("Skip")
                            }

                            Button(onClick = {
                                scope.launch {
                                    if (pagerState.currentPage < pages.size - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    } else {
                                        viewModel.completeOnboarding("dummyUserId")
                                    }
                                }
                            }) {
                                Text(if (pagerState.currentPage == pages.size - 1) "Finish" else "Next")
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }

                is OnboardingUIState.Finished -> {
                    LaunchedEffect(uiState) {
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOnboardingScreen() {
    val navController = rememberNavController()
    OnboardingScreen(navController = navController)
}
