package com.pranayharjai7.myemotions.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.ui.components.EmotionCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * Main dashboard screen displayed after successful authentication.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit, 
    onNavigateToEmotionCapture: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val history by viewModel.history.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Emotions",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            DashboardHeader(userName = "Pranay")

            Spacer(modifier = Modifier.height(32.dp))

            DashboardContent(history = history, modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(32.dp))

            com.pranayharjai7.myemotions.ui.components.EmotionButton(
                text = "Scan Emotion",
                onClick = onNavigateToEmotionCapture
            )
        }
    }
}

@Composable
private fun DashboardHeader(userName: String) {
    Text(
        text = "Welcome back,",
        style = MaterialTheme.typography.titleMedium
    )
    Text(
        text = userName,
        style = MaterialTheme.typography.displaySmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun DashboardContent(history: List<EmotionRecord>, modifier: Modifier = Modifier) {
    EmotionCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                text = "Recent Emotions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No emotions recorded yet.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(history) { record ->
                        EmotionHistoryItem(record)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmotionHistoryItem(record: EmotionRecord) {
    val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(record.timestamp))

    val emoji = when(record.emotion) {
        "Happiness" -> "🙂"
        "Sadness" -> "😢"
        "Anger" -> "😠"
        "Fear" -> "😨"
        "Surprise" -> "😲"
        "Disgust" -> "🤢"
        "Contempt" -> "😒"
        "Neutral" -> "😐"
        else -> "🤔"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = record.emotion, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = dateString, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        Text(
            text = "${(record.confidence * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}
