package com.pranayharjai7.myemotions.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.ui.components.AnimatedGradientBackground
import com.pranayharjai7.myemotions.ui.components.EmotionCard
import com.pranayharjai7.myemotions.ui.components.ProfileAvatar
import com.pranayharjai7.myemotions.ui.components.ProfileBottomSheet
import com.pranayharjai7.myemotions.ui.components.emotionToEmoji
import com.pranayharjai7.myemotions.ui.theme.AzureGradient
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onNavigateToLogMood: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val todayEmotions by viewModel.todayEmotionList.collectAsStateWithLifecycle()
    val latestEmotion by viewModel.todayLatestEmotion.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var showProfileMenu by androidx.compose.runtime.remember { 
        androidx.compose.runtime.mutableStateOf(false) 
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Emotions",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    ProfileAvatar(
                        userInfo = currentUser,
                        onClick = { showProfileMenu = true },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            GreetingHero(userName = "Pranay")

            Spacer(modifier = Modifier.height(40.dp))

            HeroMoodDisplay(latestEmotion = latestEmotion)

            Spacer(modifier = Modifier.height(48.dp))

            // Unified Action Pill
            LogMoodAction(onClick = onNavigateToLogMood)

            Spacer(modifier = Modifier.height(48.dp))

            TimelinePreviewSection(
                todayEmotions = todayEmotions,
                onViewFullTimeline = onNavigateToHistory
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showProfileMenu) {
            ProfileBottomSheet(
                userInfo = currentUser,
                onDismiss = { showProfileMenu = false },
                onLogout = onLogout,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToInsights = { /* TODO */ },
                onNavigateToSettings = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun GreetingHero(userName: String) {
    val greeting = when (LocalTime.now().hour) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    Column {
        Text(
            text = greeting,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = "$userName.",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@Composable
private fun HeroMoodDisplay(latestEmotion: EmotionRecord?) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (latestEmotion != null) {
                Text(
                    text = emotionToEmoji(latestEmotion.emotion),
                    fontSize = 100.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Right now, you're feeling",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = latestEmotion.emotion,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            } else {
                Text(
                    text = "😶",
                    fontSize = 100.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Start your day by logging a mood",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun LogMoodAction(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(32.dp),
        color = Color.White.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Log how you're feeling",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun TimelinePreviewSection(
    todayEmotions: List<EmotionRecord>,
    onViewFullTimeline: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Today's Journey",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            TextButton(onClick = onViewFullTimeline) {
                Text("Full Timeline →", color = Color.White.copy(alpha = 0.6f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (todayEmotions.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No emotions recorded yet today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            todayEmotions.forEach { record ->
                GlassTimelineItem(record)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun GlassTimelineItem(record: EmotionRecord) {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(record.timestamp))

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateString,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.width(65.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = emotionToEmoji(record.emotion), fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = record.emotion,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${(record.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
