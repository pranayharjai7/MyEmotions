package com.pranayharjai7.myemotions.domain.model

data class EmotionRecord(
    val id: String,
    val userId: String = "",
    val timestamp: Long,
    val emotion: String,
    val confidence: Float,
    val source: String,
    val imageUri: String? = null,
    val visibility: String = "private",
    val note: String? = null
)
