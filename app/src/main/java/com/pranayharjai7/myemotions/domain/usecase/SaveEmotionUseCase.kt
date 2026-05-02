package com.pranayharjai7.myemotions.domain.usecase

import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import javax.inject.Inject

class SaveEmotionUseCase @Inject constructor(
    private val repository: EmotionRepository
) {
    suspend operator fun invoke(record: EmotionRecord) {
        repository.saveEmotion(record)
    }
}
