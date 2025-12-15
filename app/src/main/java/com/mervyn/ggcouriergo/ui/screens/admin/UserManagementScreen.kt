package com.mervyn.ggcouriergo.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    // Note: The AdminNavHost doesn't pass a NavController directly,
    // but we include it here for general structure/preview purposes.
    // In a real app, this screen would likely use the outer NavController
    // for deep-linking into user details.
) {
    Scaffold(
        topBar = { TopAppBar(
            title = { Text("User Management") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "User Management Interface (Feature Under Development)",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserManagementScreen() {
    GGCourierGoTheme {
        UserManagementScreen()
    }
}