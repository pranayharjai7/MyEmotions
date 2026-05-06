package com.pranayharjai7.myemotions.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pranayharjai7.myemotions.domain.model.EmotionRecord

@Entity(tableName = "emotion_records")
data class EmotionRecordEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val timestamp: Long,
    val emotion: String,
    val confidence: Float,
    val source: String,
    val imageUri: String?,
    val visibility: String,
    val note: String?,
    val synced: Boolean
) {
    fun toDomainModel(): EmotionRecord {
        return EmotionRecord(
            id = id,
            userId = userId,
            timestamp = timestamp,
            emotion = emotion,
            confidence = confidence,
            source = source,
            imageUri = imageUri,
            visibility = visibility,
            note = note
        )
    }

    companion object {
        fun fromDomainModel(record: EmotionRecord, synced: Boolean): EmotionRecordEntity {
            return EmotionRecordEntity(
                id = record.id,
                userId = record.userId,
                timestamp = record.timestamp,
                emotion = record.emotion,
                confidence = record.confidence,
                source = record.source,
                imageUri = record.imageUri,
                visibility = record.visibility,
                note = record.note,
                synced = synced
            )
        }
    }
}
