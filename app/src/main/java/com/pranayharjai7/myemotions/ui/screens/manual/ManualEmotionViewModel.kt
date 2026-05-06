package com.pranayharjai7.myemotions.ui.screens.manual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.domain.usecase.SaveEmotionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ManualEmotionViewModel @Inject constructor(
    private val saveEmotionUseCase: SaveEmotionUseCase
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    fun logEmotion(emotionLabel: String, visibility: String = "private", note: String? = null) {
        viewModelScope.launch {
            _isSaving.value = true
            val record = EmotionRecord(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                emotion = emotionLabel,
                confidence = 1.0f,
                source = "manual",
                visibility = visibility,
                note = note
            )
            saveEmotionUseCase(record)
            _isSaving.value = false
            _saveSuccess.value = true
        }
    }
    
    fun resetSuccess() {
        _saveSuccess.value = false
    }
}
