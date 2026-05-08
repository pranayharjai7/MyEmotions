package com.pranayharjai7.myemotions.ui.screens.space

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import coil.compose.AsyncImage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pranayharjai7.myemotions.domain.model.Profile
import com.pranayharjai7.myemotions.ui.screens.space.FeedItem
import com.pranayharjai7.myemotions.ui.components.EmotionIcon
import com.pranayharjai7.myemotions.ui.theme.getThemeForEmotion
import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.border
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SpaceScreen(
    onNavigateToProfile: (String) -> Unit,
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    val feedItems by feedViewModel.feedItems.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // --- SECTION 1: EMOTION FEED ---
            item {
                Text(
                    text = "Friends' Emotions",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp)
                )
            }

            if (feedItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 32.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No recent emotions from friends",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(feedItems) { item ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        FeedItemCard(
                            item = item,
                            onReact = { type -> feedViewModel.sendReaction(item.emotion_id, type) },
                            onRemoveReaction = { feedViewModel.removeReaction(item.emotion_id) }
                        )
                    }
                }
            }
        }
    }
}

// --- REUSED COMPONENTS FROM PREVIOUS SCREENS ---

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedItemCard(item: FeedItem, onReact: (String) -> Unit, onRemoveReaction: () -> Unit) {
    val theme = getThemeForEmotion(item.emotion)
    var showReactionPicker by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = theme.primaryColor.copy(alpha = 0.05f)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, theme.primaryColor.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!item.avatar_url.isNullOrBlank()) {
                    AsyncImage(
                        model = item.avatar_url,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(theme.primaryColor.copy(alpha = 0.2f))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(theme.primaryColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (item.display_name ?: item.username ?: "?").firstOrNull()?.toString() ?: "?",
                            style = MaterialTheme.typography.titleMedium,
                            color = theme.primaryColor
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.display_name ?: item.username ?: "Unknown", style = MaterialTheme.typography.titleMedium)
                    val timeAgo = DateUtils.getRelativeTimeSpanString(item.unix_timestamp, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
                    Text(timeAgo.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body: Emotion and Note
            Row(verticalAlignment = Alignment.CenterVertically) {
                EmotionIcon(emotion = item.emotion, modifier = Modifier.size(32.dp), tint = theme.primaryColor)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Feeling ${item.emotion}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = theme.primaryColor)
            }

            if (!item.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${item.note}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer: Reactions List
            if (item.reactions.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val reactionCounts = item.reactions.groupingBy { it.reaction_type }.eachCount()
                    reactionCounts.forEach { (type, count) ->
                        val emoji = when (type) {
                            "Support" -> "❤️"
                            "Hug" -> "🫂"
                            "Relate" -> "🙌"
                            "Proud" -> "🌟"
                            "Care" -> "💛"
                            else -> "❤️"
                        }
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("$emoji $count", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Action Button
            Box(contentAlignment = Alignment.Center) {
                if (showReactionPicker) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(32.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            "Support" to "❤️",
                            "Hug" to "🫂",
                            "Relate" to "🙌",
                            "Proud" to "🌟",
                            "Care" to "💛"
                        ).forEach { (type, emoji) ->
                            TextButton(
                                onClick = {
                                    onReact(type)
                                    showReactionPicker = false
                                }
                            ) {
                                Text(emoji, fontSize = 24.sp)
                            }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { onReact("Support") },
                                onLongClick = { showReactionPicker = true }
                            ),
                        shape = RoundedCornerShape(32.dp),
                        border = BorderStroke(1.dp, theme.primaryColor.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = theme.primaryColor
                        )
                    ) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("React")
                    }
                }
            }
        }
    }
}

