package com.pranayharjai7.myemotions.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    var isDynamicTheme by remember { mutableStateOf(true) }
    var areNotificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Theme Behavior", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            ListItem(
                headlineContent = { Text("Emotion-based Dynamic Theme") },
                supportingContent = { Text("Changes app colors based on your mood") },
                trailingContent = { Switch(checked = isDynamicTheme, onCheckedChange = { isDynamicTheme = it }) }
            )
            ListItem(
                headlineContent = { Text("Static Theme") },
                supportingContent = { Text("Always use Azure Glass theme") },
                trailingContent = { Switch(checked = !isDynamicTheme, onCheckedChange = { isDynamicTheme = !it }) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Notification Preferences", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            ListItem(
                headlineContent = { Text("Enable Notifications") },
                supportingContent = { Text("Receive mood log reminders") },
                trailingContent = { Switch(checked = areNotificationsEnabled, onCheckedChange = { areNotificationsEnabled = it }) }
            )
        }
    }
}
