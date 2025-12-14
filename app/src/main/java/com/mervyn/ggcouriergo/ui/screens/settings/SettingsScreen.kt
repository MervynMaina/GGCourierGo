package com.mervyn.ggcouriergo.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.SettingsViewModel
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme // Assuming the theme name is GGCourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    // Collect the theme state from the ViewModel
    val darkThemeEnabled by viewModel.isDarkTheme.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            // --- THEME TOGGLE ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dark Theme", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = darkThemeEnabled,
                    onCheckedChange = viewModel::toggleDarkTheme // Update state via ViewModel
                )
            }
            Divider(Modifier.padding(vertical = 12.dp))

            // --- NAVIGATE TO PROFILE ---
            Button(
                onClick = { navController.navigate("profile") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Edit Profile", color = MaterialTheme.colorScheme.onTertiary)
            }
            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(16.dp))

            // --- LOG OUT BUTTON ---
            Button(
                onClick = { viewModel.logout(navController) }, // Handle logout via ViewModel
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Log Out", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    val navController = rememberNavController()
    // FIX: Using the assumed correct theme name GGCourierGoTheme
    GGCourierGoTheme {
        SettingsScreen(navController)
    }
}