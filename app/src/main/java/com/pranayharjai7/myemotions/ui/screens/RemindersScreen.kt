package com.pranayharjai7.myemotions.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onNavigateBack: () -> Unit,
    viewModel: RemindersViewModel = hiltViewModel()
) {
    var remindersEnabled by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                remindersEnabled = false
            }
        }
    )

    LaunchedEffect(remindersEnabled) {
        if (remindersEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            // Schedule dummy reminders for Phase 2 demonstration
            viewModel.scheduleReminder("Log your mood tonight", "It's time for your evening reflection.", 1)
        } else {
            viewModel.cancelAllReminders()
        }
    }

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
                    supportingContent = { Text("In 1 minute (Demo)") }
                )
                ListItem(
                    headlineContent = { Text("How are you feeling right now?") },
                    supportingContent = { Text("Not scheduled") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.scheduleReminder("Check-in", "How are you feeling right now?", 1) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Trigger Demo Reminder (1 min delay)")
                }
            }
        }
    }
}
