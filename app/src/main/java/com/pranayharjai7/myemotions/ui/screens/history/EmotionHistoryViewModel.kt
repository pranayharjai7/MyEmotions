package com.pranayharjai7.myemotions.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class EmotionHistoryViewModel @Inject constructor(
    private val repository: EmotionRepository
) : ViewModel() {

    private val _groupedHistory = MutableStateFlow<Map<LocalDate, List<EmotionRecord>>>(emptyMap())
    val groupedHistory: StateFlow<Map<LocalDate, List<EmotionRecord>>> = _groupedHistory.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getEmotionHistory().collect { records ->
                _groupedHistory.value = records.groupBy { record ->
                    Instant.ofEpochMilli(record.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                }.toSortedMap(Comparator.reverseOrder())
            }
        }
    }
}
