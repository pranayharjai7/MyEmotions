package com.pranayharjai7.myemotions.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: EmotionRepository
) : ViewModel() {

    private val _history = MutableStateFlow<List<EmotionRecord>>(emptyList())
    val history: StateFlow<List<EmotionRecord>> = _history.asStateFlow()

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
