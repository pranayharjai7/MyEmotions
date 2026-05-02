package com.pranayharjai7.myemotions.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: EmotionHistoryViewModel = hiltViewModel()
) {
    val groupedHistory by viewModel.groupedHistory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mood History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (groupedHistory.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No emotions recorded yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Scan your first emotion to start tracking your mood.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    groupedHistory.forEach { (date, records) ->
                        item {
                            DateHeader(date = date)
                        }
                        items(records) { record ->
                            EmotionTimelineItem(record = record)
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateHeader(date: LocalDate) {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    val label = when (date) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }

    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun EmotionTimelineItem(record: EmotionRecord) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val time = Instant.ofEpochMilli(record.timestamp)
        .atZone(ZoneId.systemDefault())
        .format(timeFormatter)

    val emoji = emotionToEmoji(record.emotion)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.width(50.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(text = emoji, style = MaterialTheme.typography.headlineSmall)
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = record.emotion,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Confidence ${(record.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

fun emotionToEmoji(emotion: String): String {
    return when (emotion) {
        "Happiness" -> "😊"
        "Sadness" -> "😢"
        "Anger" -> "😠"
        "Fear" -> "😨"
        "Surprise" -> "😲"
        "Disgust" -> "🤢"
        "Contempt" -> "😒"
        "Neutral" -> "😐"
        else -> "🤔"
    }
}
