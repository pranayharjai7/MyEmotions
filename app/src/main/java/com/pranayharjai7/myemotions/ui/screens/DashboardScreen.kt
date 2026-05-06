package com.pranayharjai7.myemotions.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.blur
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.ui.components.ProfileAvatar
import com.pranayharjai7.myemotions.ui.components.ProfileMenu
import com.pranayharjai7.myemotions.ui.components.EmotionIcon
import com.pranayharjai7.myemotions.ui.theme.MoodNeutral
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
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
    onNavigateToRecommendations: (String) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToReminders: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {},
    onNavigateToRecommendationsHistory: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val todayEmotions by viewModel.todayEmotionList.collectAsStateWithLifecycle()
    val latestEmotion by viewModel.todayLatestEmotion.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val loggingStreak by viewModel.loggingStreak.collectAsStateWithLifecycle()
    val todayMoodSnapshot by viewModel.todayMoodSnapshot.collectAsStateWithLifecycle()
    val dominantEmotionToday by viewModel.dominantEmotionToday.collectAsStateWithLifecycle()
    val smartInsight by viewModel.smartInsight.collectAsStateWithLifecycle()

    val userName = remember(currentUser) {
        currentUser?.userMetadata?.get("full_name")?.jsonPrimitive?.contentOrNull
            ?: currentUser?.email?.split("@")?.firstOrNull()
            ?: "User"
    }

    var showProfileMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .then(if (showProfileMenu) Modifier.blur(16.dp) else Modifier)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            WelcomeHeader(
                userName = userName,
                userInfo = currentUser,
                latestEmotion = latestEmotion,
                loggingStreak = loggingStreak,
                showProfileMenu = showProfileMenu,
                onShowProfileMenuChange = { showProfileMenu = it },
                onLogout = onLogout,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToAnalytics = onNavigateToAnalytics,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToRecommendationsHistory = onNavigateToRecommendationsHistory,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToReminders = onNavigateToReminders,
                onNavigateToHelp = onNavigateToHelp
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryEmotionCard(
                latestEmotion = latestEmotion,
                onTap = { latestEmotion?.let { onNavigateToRecommendations(it.emotion) } },
                onLogMood = onNavigateToLogMood
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                QuickActionCard(
                    title = "Log Mood",
                    icon = Icons.Default.Add,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLogMood
                )
                Spacer(modifier = Modifier.width(16.dp))
                QuickActionCard(
                    title = "Scan Face",
                    icon = Icons.Default.CameraAlt,
                    modifier = Modifier.weight(1f),
                    // For now routing to LogMood if Capture not explicitly passed, 
                    // Wait, onNavigateToLogMood can be used, or a specific scan route if available.
                    // The NavGraph has "Capture" which isn't explicitly passed here. I'll use onNavigateToLogMood for now.
                    onClick = onNavigateToLogMood
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                QuickActionCard(
                    title = "Journal",
                    icon = Icons.Default.EditNote,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLogMood
                )
                Spacer(modifier = Modifier.width(16.dp))
                QuickActionCard(
                    title = "Get Advice",
                    icon = Icons.Default.Lightbulb,
                    modifier = Modifier.weight(1f),
                    onClick = { latestEmotion?.let { onNavigateToRecommendations(it.emotion) } ?: onNavigateToLogMood() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Today's Mood",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TodaySnapshotCard(
                snapshot = todayMoodSnapshot,
                dominant = dominantEmotionToday,
                onClick = onNavigateToAnalytics // EmotionInsights
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Smart Insight",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SmartInsightCard(insight = smartInsight)
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun WelcomeHeader(
    userName: String,
    userInfo: UserInfo?,
    latestEmotion: EmotionRecord?,
    loggingStreak: Int,
    showProfileMenu: Boolean,
    onShowProfileMenuChange: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToRecommendationsHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    val greeting = when (LocalTime.now().hour) {
        in 0..11 -> "Good morning,"
        in 12..16 -> "Good afternoon,"
        else -> "Good evening,"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            )
            Text(
                text = "$userName.",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "How are you feeling today?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            if (loggingStreak > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$loggingStreak Day Streak",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Box {
            ProfileAvatar(
                userInfo = userInfo,
                onClick = { onShowProfileMenuChange(true) },
                modifier = Modifier.padding(top = 4.dp),
                borderColor = if (latestEmotion != null) MaterialTheme.colorScheme.primary else null
            )
            
            if (showProfileMenu) {
                ProfileMenu(
                    userInfo = userInfo,
                    onDismiss = { onShowProfileMenuChange(false) },
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToAnalytics = onNavigateToAnalytics,
                    onNavigateToTimeline = onNavigateToHistory,
                    onNavigateToRecommendationsHistory = onNavigateToRecommendationsHistory,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToReminders = onNavigateToReminders,
                    onNavigateToHelp = onNavigateToHelp,
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
private fun PrimaryEmotionCard(
    latestEmotion: EmotionRecord?,
    onTap: () -> Unit,
    onLogMood: () -> Unit
) {
    val cardColor = MaterialTheme.colorScheme.surface
    val emotionColor = if (latestEmotion != null) MaterialTheme.colorScheme.primary else MoodNeutral

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp, shape = RoundedCornerShape(32.dp), ambientColor = emotionColor, spotColor = emotionColor),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        onClick = onTap
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = latestEmotion,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "EmotionIcon"
                ) { emotion ->
                    EmotionIcon(
                        emotion = emotion?.emotion ?: "Neutral",
                        modifier = Modifier.size(80.dp).padding(bottom = 16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = latestEmotion?.emotion ?: "Neutral",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                latestEmotion?.let {
                    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val timeString = dateFormat.format(Date(it.timestamp))
                    Text(
                        text = "Detected $timeString",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Tap to see mood recommendations",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Log Mood Floating Button inside card
            SmallFloatingActionButton(
                onClick = onLogMood,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Mood")
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        modifier = modifier.shadow(4.dp, shape = RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TodaySnapshotCard(
    snapshot: Map<String, Int>,
    dominant: String?,
    onClick: () -> Unit
) {
    val dominantTheme = com.pranayharjai7.myemotions.ui.theme.getThemeForEmotion(dominant)
    
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            if (snapshot.isEmpty()) {
                Text(
                    text = "No emotions logged today yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    snapshot.entries.sortedByDescending { it.value }.forEach { (emotion, count) ->
                        val emoji = com.pranayharjai7.myemotions.ui.utils.getEmojiForEmotion(emotion)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "$emotion $emoji ($count)",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Dominant Mood: ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = dominant ?: "",
                        style = MaterialTheme.typography.labelLarge,
                        color = dominantTheme.primaryColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SmartInsightCard(insight: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = "Insight",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = insight,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
