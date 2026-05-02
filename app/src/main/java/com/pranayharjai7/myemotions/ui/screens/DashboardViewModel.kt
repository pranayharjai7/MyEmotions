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
        }.take(5)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayLatestEmotion: StateFlow<EmotionRecord?> = todayEmotionList.map { 
        it.firstOrNull() 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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
