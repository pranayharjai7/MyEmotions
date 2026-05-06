package com.pranayharjai7.myemotions.data.remote.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmotionRecordDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("emotion") val emotion: String,
    @SerialName("confidence") val confidence: Float,
    @SerialName("source") val source: String,
    @SerialName("image_uri") val imageUri: String? = null,
    @SerialName("visibility") val visibility: String = "private",
    @SerialName("note") val note: String? = null
)
