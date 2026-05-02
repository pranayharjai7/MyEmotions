package com.pranayharjai7.myemotions.domain.usecase

import android.graphics.Bitmap
import com.pranayharjai7.myemotions.domain.model.EmotionResult
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import javax.inject.Inject

class DetectEmotionUseCase @Inject constructor(
    private val repository: EmotionRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): Result<EmotionResult> {
        return repository.detectEmotion(bitmap)
    }
}
