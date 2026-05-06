package com.pranayharjai7.myemotions.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.domain.repository.AuthRepository
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: EmotionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<UserInfo?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _history = MutableStateFlow<List<EmotionRecord>>(emptyList())
    val history: StateFlow<List<EmotionRecord>> = _history.asStateFlow()

    val todayEmotionList: StateFlow<List<EmotionRecord>> = _history.map { records ->
        val today = LocalDate.now()
        records.filter { record ->
            Instant.ofEpochMilli(record.timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate() == today
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayLatestEmotion: StateFlow<EmotionRecord?> = todayEmotionList.map { 
        it.firstOrNull() 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val loggingStreak: StateFlow<Int> = _history.map { records ->
        if (records.isEmpty()) return@map 0
        val dates = records.map {
            Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        }.distinct().sortedDescending()
        
        val today = LocalDate.now()
        var streak = 0
        if (dates.isEmpty() || (dates.first() != today && dates.first() != today.minusDays(1))) {
            return@map 0
        }
        
        var currentDate = dates.first()
        for (date in dates) {
            if (date == currentDate) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else {
                break
            }
        }
        streak
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayMoodSnapshot: StateFlow<Map<String, Int>> = todayEmotionList.map { records ->
        records.groupingBy { it.emotion }.eachCount()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val dominantEmotionToday: StateFlow<String?> = todayMoodSnapshot.map { snapshot ->
        snapshot.maxByOrNull { it.value }?.key
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val smartInsight: StateFlow<String> = combine(
        loggingStreak, todayEmotionList, dominantEmotionToday, _history
    ) { streak, todayList, dominant, allHistory ->
        if (todayList.isEmpty()) {
            "Log an emotion to unlock your daily insights."
        } else if (dominant != null && listOf("Sadness", "Anger", "Fear", "Disgust").contains(dominant) && todayList.size > 2) {
            "It's been a challenging day. Remember to be kind to yourself."
        } else if (streak >= 3) {
            "You're on a roll! Logging your emotions builds self-awareness."
        } else {
            val morningPositiveCount = allHistory.count {
                val dt = Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
                dt.hour < 12 && listOf("Happiness", "Calm").contains(it.emotion)
            }
            val afternoonPositiveCount = allHistory.count {
                val dt = Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
                dt.hour >= 12 && listOf("Happiness", "Calm").contains(it.emotion)
            }
            if (morningPositiveCount > afternoonPositiveCount + 5) {
                "You tend to feel most positive in the mornings."
            } else if (afternoonPositiveCount > morningPositiveCount + 5) {
                "You tend to feel most positive in the afternoons or evenings."
            } else if (dominant != null && listOf("Happiness", "Calm").contains(dominant)) {
                "You've been having a great day. Keep up the positive energy!"
            } else {
                "Consistency is key! Keep tracking to uncover deep mood patterns."
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Log an emotion to unlock your daily insights.")

    init {
        viewModelScope.launch {
            repository.fetchEmotionsFromRemote()
        }
        viewModelScope.launch {
            repository.getEmotionHistory().collect { records ->
                _history.value = records
            }
        }
    }
}
