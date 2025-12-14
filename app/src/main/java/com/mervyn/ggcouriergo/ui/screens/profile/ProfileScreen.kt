package com.mervyn.ggcouriergo.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mervyn.ggcouriergo.data.ProfileViewModel
import com.mervyn.ggcouriergo.models.ProfileUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is ProfileUIState.Loading -> {
                    Spacer(Modifier.height(100.dp))
                    CircularProgressIndicator()
                }
                is ProfileUIState.Error -> {
                    Spacer(Modifier.height(100.dp))
                    Text("Error loading profile: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadProfile() }) { Text("Retry") }
                }
                is ProfileUIState.Success -> {
                    ProfileContent(user = state.profile)
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- LOG OUT BUTTON ---
            Button(
                onClick = { viewModel.logout(navController) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Log Out", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}

@Composable
fun ProfileContent(user: com.mervyn.ggcouriergo.models.UserProfile) {
    Icon(
        Icons.Filled.Person,
        contentDescription = "Profile Icon",
        modifier = Modifier.size(96.dp).padding(top = 16.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(24.dp))

    ProfileDetailRow(label = "Name", value = user.name)
    ProfileDetailRow(label = "Email", value = user.email)
    ProfileDetailRow(label = "Role", value = user.role)
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}