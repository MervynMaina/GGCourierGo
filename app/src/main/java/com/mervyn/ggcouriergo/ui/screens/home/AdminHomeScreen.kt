package com.mervyn.ggcouriergo.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mervyn.ggcouriergo.data.AdminHomeViewModel
import com.mervyn.ggcouriergo.data.AdminHomeViewModelFactory
import com.mervyn.ggcouriergo.repository.AdminRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: NavController,
    viewModel: AdminHomeViewModel = viewModel(factory = AdminHomeViewModelFactory(AdminRepository()))
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin Home") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { navController.navigate("dispatcher_dashboard") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Dispatchers")
            }

            Button(
                onClick = { navController.navigate("driver_dashboard") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Drivers")
            }

            Button(
                onClick = { /* TODO: Show statistics or reports */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Stats")
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewAdminHomeScreen() {
    val navController = androidx.navigation.compose.rememberNavController()
    AdminHomeScreen(navController)
}
