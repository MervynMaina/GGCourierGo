package com.mervyn.ggcouriergo.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Text("Theme Mode")
        Spacer(Modifier.height(8.dp))
        Text("Light / Dark / System toggle placeholder")
    }
}

@Preview (showBackground = true)
@Composable
fun PreviewSettings() {
    CourierGoTheme { SettingsScreen() }
}
