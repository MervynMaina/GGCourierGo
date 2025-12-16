package com.mervyn.ggcouriergo.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

@Composable
fun AdminSettingsScreen() {
    var maintenanceMode by remember { mutableStateOf(false) }
    var pushNotifications by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- HEADER ---
        Surface(
            color = Color.White,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "System Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Configure global app behavior",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SECTION: SYSTEM CONTROL ---
            item { SettingHeader(title = "System Control") }
            item {
                SettingsSwitchTile(
                    title = "Maintenance Mode",
                    subtitle = "Disable app access for all users",
                    icon = Icons.Default.Build,
                    checked = maintenanceMode,
                    onCheckedChange = { maintenanceMode = it }
                )
            }

            // --- SECTION: NOTIFICATIONS ---
            item { SettingHeader(title = "Communications") }
            item {
                SettingsSwitchTile(
                    title = "Push Notifications",
                    subtitle = "Enable system-wide alerts",
                    icon = Icons.Default.Notifications,
                    checked = pushNotifications,
                    onCheckedChange = { pushNotifications = it }
                )
            }

            // --- SECTION: DATA MANAGEMENT ---
            item { SettingHeader(title = "Data Management") }
            item {
                SettingsActionTile(
                    title = "Backup Database",
                    subtitle = "Export system data to cloud storage",
                    icon = Icons.Default.CloudUpload,
                    onClick = { /* TODO */ }
                )
            }
            item {
                SettingsActionTile(
                    title = "Clear Cache",
                    subtitle = "Free up system temporary storage",
                    icon = Icons.Default.DeleteSweep,
                    onClick = { /* TODO */ },
                    isDestructive = true
                )
            }
        }
    }
}

@Composable
fun SettingHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsSwitchTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun SettingsActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isDestructive) Color.Red else MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = if (isDestructive) Color.Red else Color.Unspecified)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
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