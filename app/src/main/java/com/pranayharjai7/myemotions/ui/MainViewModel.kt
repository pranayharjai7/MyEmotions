package com.pranayharjai7.myemotions.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import com.pranayharjai7.myemotions.ui.theme.DefaultAzureTheme
import com.pranayharjai7.myemotions.ui.theme.MoodTheme
import com.pranayharjai7.myemotions.ui.theme.getThemeForEmotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: EmotionRepository
) : ViewModel() {

    /**
     * Provides the current MoodTheme based on the user's latest recorded emotion.
     * Transitions are automatically triggered when new emotions are logged.
     */
    val currentTheme: StateFlow<MoodTheme> = repository.getEmotionHistory()
        .map { records ->
            val latest = records.firstOrNull()
            getThemeForEmotion(latest?.emotion)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DefaultAzureTheme
        )
}
