package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
// We now need to import the required components for the New Parcels Tab
import com.mervyn.ggcouriergo.data.NewParcelsViewModel
import com.mervyn.ggcouriergo.data.NewParcelsViewModelFactory
// We no longer need DispatcherDashboardViewModel/Factory unless it manages other data
// import com.mervyn.ggcouriergo.data.DispatcherDashboardViewModel
// import com.mervyn.ggcouriergo.data.DispatcherDashboardViewModelFactory

// The necessary imports for models/repo/navigation are assumed to be correct

import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import com.mervyn.ggcouriergo.navigation.routeParcelDetails
import com.mervyn.ggcouriergo.navigation.ROUT_CREATE_PARCEL
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
// If you defined the UI state needed for the old approach, it's now removed.
// import com.mervyn.ggcouriergo.models.DispatcherDashboardUIState

// Define internal screens/tabs
sealed class DispatcherNavTab(val title: String) {
    object NewParcels : DispatcherNavTab("New Parcels")
    object AssignedParcels : DispatcherNavTab("Assigned")
    object MapView : DispatcherNavTab("Map View")
}
val dispatcherTabs = listOf(DispatcherNavTab.NewParcels, DispatcherNavTab.AssignedParcels, DispatcherNavTab.MapView)

// --------------------------------------------------
// Dispatcher Dashboard Screen (Top Level)
// --------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatcherDashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    // FIX 1: REMOVE the DispatcherDashboardViewModel dependency.
    // Data loading is now handled by the specific tabs (NewParcelsTab, AssignedParcelsTab).
    // viewModel: DispatcherDashboardViewModel = viewModel(...) // <-- REMOVED
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dispatcher Console") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ROUT_CREATE_PARCEL) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create Parcel")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier // Use the external modifier passed in
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Tab Row for internal navigation
            TabRow(selectedTabIndex = selectedTabIndex) {
                dispatcherTabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(tab.title) }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTabIndex) {
                // FIX 2: Call the correct, new, standalone NewParcelsTab Composable.
                // We pass the navigation function it needs for assignment.
                0 -> NewParcelsTab(onNavigateToAssignment = { parcelId ->
                    // Navigate to the details/assignment screen
                    navController.navigate(routeParcelDetails(parcelId))
                })

                // FIX 3: AssignedParcelsTab call (Assuming this is a standalone component too)
                1 -> AssignedParcelsTab(onNavigateToDetails = { parcelId ->
                    navController.navigate(routeParcelDetails(parcelId))
                })

                2 -> DispatcherMapViewTab()
            }
        }
    }
}

// --------------------------------------------------
// OLD TAB 1 LOGIC REMOVED/REPLACED
// --------------------------------------------------
/*
// The following block is the old, conflicting NewParcelsTab logic and is REMOVED
@Composable
fun NewParcelsTab(navController: NavController, viewModel: DispatcherDashboardViewModel) {
    // ... entire previous implementation of NewParcelsTab is deleted from this file
}
*/

// --------------------------------------------------
// TAB 3: Map View (Placeholder remains)
// --------------------------------------------------
@Composable
fun DispatcherMapViewTab() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text("Live Driver/Parcel Map (Integration with Maps API required)", style = MaterialTheme.typography.titleMedium)
    }
}

// --------------------------------------------------
// Delivery Card (Moved to a separate file, or kept if generic)
// Since this card was tied to the old NewParcelsTab, it should be removed or moved.
// For now, removing it here to clean up the file.
// --------------------------------------------------
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignableDeliveryCard(parcel: Parcel, onAssignClick: () -> Unit) {
    // ... REMOVED
}
*/


@Preview(showBackground = true)
@Composable
fun PreviewDispatcherDashboardScreen() {
    val navController = rememberNavController()
    GGCourierGoTheme {
        // We can't use the real data/viewmodels in the preview easily, but the UI structure is fine.
        DispatcherDashboardScreen(navController)
    }
}