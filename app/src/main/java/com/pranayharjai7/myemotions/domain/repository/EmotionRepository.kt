package com.pranayharjai7.myemotions.domain.repository

import android.graphics.Bitmap
import com.pranayharjai7.myemotions.domain.model.EmotionResult

interface EmotionRepository {
    suspend fun detectEmotion(bitmap: Bitmap): Result<EmotionResult>
}
