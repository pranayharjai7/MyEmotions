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
fun RemindersScreen(onNavigateBack: () -> Unit) {
    var remindersEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminders", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            ListItem(
                headlineContent = { Text("Enable Reminders") },
                trailingContent = { Switch(checked = remindersEnabled, onCheckedChange = { remindersEnabled = it }) }
            )

            if (remindersEnabled) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text("Scheduled Reminders", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                
                ListItem(
                    headlineContent = { Text("Log your mood tonight") },
                    supportingContent = { Text("8:00 PM") }
                )
                ListItem(
                    headlineContent = { Text("How are you feeling right now?") },
                    supportingContent = { Text("1:00 PM") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* TODO Phase 2: Add reminder time picker */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Add New Reminder")
                }
            }
        }
    }
}
