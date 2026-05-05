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
                text = "Your Insights",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                SecondaryActionCard(
                    title = "Timeline",
                    subtitle = "Mood history",
                    icon = Icons.Default.History,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToHistory
                )
                Spacer(modifier = Modifier.width(16.dp))
                SecondaryActionCard(
                    title = "Analytics",
                    subtitle = "Mood trends",
                    icon = Icons.Default.BarChart,
                    modifier = Modifier.weight(1f),
                    onClick = { /* Navigate to Analytics */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            WeeklyMoodPreview()
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun WelcomeHeader(
    userName: String,
    userInfo: UserInfo?,
    latestEmotion: EmotionRecord?,
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
private fun SecondaryActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.shadow(8.dp, shape = RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun WeeklyMoodPreview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Weekly Mood", style = MaterialTheme.typography.titleMedium)
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Simple visual representation of a graph
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) { index ->
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height((30..60).random().dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (index == 5) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                    )
                }
            }
        }
    }
}
