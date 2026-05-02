package com.pranayharjai7.myemotions.ui.screens.manual

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pranayharjai7.myemotions.ui.screens.history.emotionToEmoji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEmotionScreen(
    onNavigateBack: () -> Unit,
    viewModel: ManualEmotionViewModel = hiltViewModel()
) {
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val emotions = listOf(
        "Happiness", "Sadness", "Anger", "Fear", 
        "Surprise", "Disgust", "Contempt", "Neutral"
    )

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            snackbarHostState.showSnackbar("Emotion logged successfully")
            viewModel.resetSuccess()
            onNavigateBack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Manual Logging", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "How are you feeling right now?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (isSaving) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(emotions) { emotion ->
                        EmotionOptionCard(
                            emoji = emotionToEmoji(emotion),
                            label = emotion,
                            onClick = { viewModel.logEmotion(emotion) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmotionOptionCard(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .height(140.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 40.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
