package com.pranayharjai7.myemotions.domain.repository

import android.graphics.Bitmap
import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.domain.model.EmotionResult
import kotlinx.coroutines.flow.Flow

interface EmotionRepository {
    suspend fun detectEmotion(bitmap: Bitmap): Result<EmotionResult>
    suspend fun saveEmotion(record: EmotionRecord)
    fun getEmotionHistory(): Flow<List<EmotionRecord>>
    fun getEmotionsByUserId(userId: String): Flow<List<EmotionRecord>>
    suspend fun syncPendingEmotions()
    suspend fun fetchEmotionsFromRemote()
}
