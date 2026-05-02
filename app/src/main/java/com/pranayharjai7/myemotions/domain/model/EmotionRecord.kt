package com.pranayharjai7.myemotions.domain.model

data class EmotionRecord(
    val id: String,
    val timestamp: Long,
    val emotion: String,
    val confidence: Float,
    val source: String,
    val imageUri: String? = null
)
