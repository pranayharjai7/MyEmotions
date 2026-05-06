package com.pranayharjai7.myemotions.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import com.pranayharjai7.myemotions.ui.components.EmotionIcon
import com.pranayharjai7.myemotions.ui.screens.emotion.getRecommendationsForEmotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.pranayharjai7.myemotions.ui.utils.getEmojiForEmotion
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EmotionDetailsViewModel @Inject constructor(
    private val repository: EmotionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val recordId: String = checkNotNull(savedStateHandle["recordId"])

    val emotionRecord: StateFlow<EmotionRecord?> = repository.getEmotionHistory()
        .map { records -> records.find { it.id == recordId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionDetailsScreen(
    recordId: String,
    onNavigateBack: () -> Unit,
    viewModel: EmotionDetailsViewModel = hiltViewModel()
) {
    val record by viewModel.emotionRecord.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
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
        if (record == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val r = record!!
            val theme = com.pranayharjai7.myemotions.ui.theme.getThemeForEmotion(r.emotion)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(theme.primaryColor.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = getEmojiForEmotion(r.emotion), fontSize = 64.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = r.emotion, style = MaterialTheme.typography.displayMedium, color = theme.primaryColor)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        DetailItem(
                            icon = Icons.Default.Schedule,
                            label = "Time Detected",
                            value = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(r.timestamp)),
                            tint = theme.primaryColor
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        DetailItem(
                            icon = Icons.Default.TrackChanges,
                            label = "Confidence Score",
                            value = "${(r.confidence * 100).toInt()}%",
                            tint = theme.primaryColor
                        )
                        LinearProgressIndicator(
                            progress = { r.confidence },
                            modifier = Modifier.fillMaxWidth().padding(start = 56.dp, top = 8.dp).height(4.dp),
                            color = theme.primaryColor,
                            trackColor = theme.primaryColor.copy(alpha = 0.2f),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        DetailItem(
                            icon = Icons.Default.Info,
                            label = "Source",
                            value = r.source.replaceFirstChar { it.uppercase() },
                            tint = theme.primaryColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "Suggested Actions", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                
                val recommendations = getRecommendationsForEmotion(r.emotion).take(2)
                recommendations.forEach { recommendation ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(theme.primaryColor.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(recommendation.icon, contentDescription = null, tint = theme.primaryColor)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = recommendation.title, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(icon: ImageVector, label: String, value: String, tint: Color = MaterialTheme.colorScheme.primary) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).background(tint.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}
