package com.pranayharjai7.myemotions.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import com.pranayharjai7.myemotions.ui.screens.emotion.getRecommendationsForEmotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class RecommendationHistoryItem(
    val emotion: String,
    val recommendationTitle: String,
    val timestamp: Long
)

@HiltViewModel
class RecommendationsHistoryViewModel @Inject constructor(
    repository: EmotionRepository
) : ViewModel() {

    val history: StateFlow<List<RecommendationHistoryItem>> = repository.getEmotionHistory()
        .map { records ->
            records.mapNotNull { record ->
                val recommendations = getRecommendationsForEmotion(record.emotion)
                val topRecommendation = recommendations.firstOrNull()
                if (topRecommendation != null) {
                    RecommendationHistoryItem(
                        emotion = record.emotion,
                        recommendationTitle = topRecommendation.title,
                        timestamp = record.timestamp
                    )
                } else {
                    null
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
