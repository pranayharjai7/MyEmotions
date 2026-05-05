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

import com.pranayharjai7.myemotions.data.repository.SettingsRepository

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: EmotionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    /**
     * Provides the current MoodTheme based on the user's latest recorded emotion.
     * Transitions are automatically triggered when new emotions are logged,
     * unless the dynamic theme is disabled in settings.
     */
    val currentTheme: StateFlow<MoodTheme> = combine(
        repository.getEmotionHistory(),
        settingsRepository.isDynamicTheme
    ) { records, isDynamic ->
        if (isDynamic) {
            val latest = records.firstOrNull()
            getThemeForEmotion(latest?.emotion)
        } else {
            DefaultAzureTheme
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DefaultAzureTheme
        )
}
