package com.pranayharjai7.myemotions.ui.screens.emotion

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.model.EmotionResult
import com.pranayharjai7.myemotions.domain.usecase.DetectEmotionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EmotionCaptureUiState {
    data object Idle : EmotionCaptureUiState
    data object Loading : EmotionCaptureUiState
    data class Success(val result: EmotionResult) : EmotionCaptureUiState
    data class Error(val message: String) : EmotionCaptureUiState
}

@HiltViewModel
class EmotionCaptureViewModel @Inject constructor(
    private val detectEmotionUseCase: DetectEmotionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EmotionCaptureUiState>(EmotionCaptureUiState.Idle)
    val uiState: StateFlow<EmotionCaptureUiState> = _uiState.asStateFlow()

    fun detectEmotion(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = EmotionCaptureUiState.Loading
            detectEmotionUseCase(bitmap)
                .onSuccess { result ->
                    _uiState.value = EmotionCaptureUiState.Success(result)
                }
                .onFailure { error ->
                    _uiState.value = EmotionCaptureUiState.Error(error.message ?: "Unknown error occurred")
                }
        }
    }

    fun resetState() {
        _uiState.value = EmotionCaptureUiState.Idle
    }
}
