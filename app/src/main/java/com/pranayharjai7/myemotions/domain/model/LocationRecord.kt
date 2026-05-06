package com.pranayharjai7.myemotions.domain.model

data class LocationRecord(
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val ghostMode: Boolean,
    val updatedAt: Long,
    val lastEmotion: String? = null
)
