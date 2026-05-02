package com.pranayharjai7.myemotions.ui.screens.emotion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranayharjai7.myemotions.ui.components.emotionToEmoji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodRecommendationsScreen(
    emotion: String,
    onNavigateBack: () -> Unit
) {
    val recommendations = getRecommendationsForEmotion(emotion)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recommendations") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = emotionToEmoji(emotion), fontSize = 48.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "You're feeling $emotion",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Here are some ways to support yourself",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(recommendations) { recommendation ->
                    RecommendationCard(recommendation)
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(recommendation: RecommendationItem) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    recommendation.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = recommendation.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

data class RecommendationItem(
    val title: String,
    val description: String,
    val icon: ImageVector
)

fun getRecommendationsForEmotion(emotion: String): List<RecommendationItem> {
    return when (emotion) {
        "Happiness" -> listOf(
            RecommendationItem("Share the Joy", "Call a friend and share what made you happy.", Icons.Default.Share),
            RecommendationItem("Express Gratitude", "Write down 3 things you're grateful for.", Icons.Default.Favorite),
            RecommendationItem("Keep Moving", "Go for a light jog or a dance session.", Icons.Default.DirectionsRun)
        )
        "Sadness" -> listOf(
            RecommendationItem("Listen to Music", "Put on a calming or uplifting playlist.", Icons.Default.MusicNote),
            RecommendationItem("Talk to Someone", "Reach out to a trusted friend or family member.", Icons.Default.Chat),
            RecommendationItem("Take a Walk", "A short walk in nature can help clear your mind.", Icons.Default.Park)
        )
        "Anger" -> listOf(
            RecommendationItem("Deep Breathing", "Try the 4-7-8 breathing technique.", Icons.Default.Air),
            RecommendationItem("Physical Activity", "Channel your energy into a workout.", Icons.Default.FitnessCenter),
            RecommendationItem("Write it Out", "Journal your thoughts to understand the trigger.", Icons.Default.EditNote)
        )
        "Fear" -> listOf(
            RecommendationItem("Grounding Exercise", "Focus on 5 things you can see, 4 you can touch.", Icons.Default.SelfImprovement),
            RecommendationItem("Safe Space", "Visualize or go to a place where you feel safe.", Icons.Default.Home),
            RecommendationItem("Limit Stimuli", "Reduce noise and bright lights for a while.", Icons.Default.DoNotDisturbOn)
        )
        "Surprise" -> listOf(
            RecommendationItem("Pause and Reflect", "Take a moment to process the sudden change.", Icons.Default.Pause),
            RecommendationItem("Document it", "Write down what surprised you for future memory.", Icons.Default.Note),
            RecommendationItem("Stay Open", "Embrace the novelty of the situation.", Icons.Default.WbSunny)
        )
        "Neutral" -> listOf(
            RecommendationItem("Set a Goal", "What's one small thing you want to achieve today?", Icons.Default.Flag),
            RecommendationItem("Mindfulness", "Spend 5 minutes in meditation.", Icons.Default.SelfImprovement),
            RecommendationItem("Learn Something", "Read a few pages of a book or an article.", Icons.Default.Book)
        )
        else -> listOf(
            RecommendationItem("Self Care", "Take a moment to breathe and check in with yourself.", Icons.Default.SelfImprovement),
            RecommendationItem("Stay Hydrated", "Drink a glass of water.", Icons.Default.WaterDrop),
            RecommendationItem("Stretch", "Do some light stretching to release tension.", Icons.Default.Accessibility)
        )
    }
}
