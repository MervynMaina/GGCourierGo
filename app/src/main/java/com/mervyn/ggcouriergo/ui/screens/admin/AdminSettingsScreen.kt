package com.mervyn.ggcouriergo.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen() {
    Scaffold(
        topBar = { TopAppBar(
            title = { Text("Admin Settings") },
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
                text = "Admin Settings Interface (Feature Under Development)",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdminSettingsScreen() {
    GGCourierGoTheme {
        AdminSettingsScreen()
    }
}