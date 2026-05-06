package com.pranayharjai7.myemotions.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmotionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmotion(emotion: EmotionRecordEntity)

    @Query("SELECT * FROM emotion_records WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllEmotions(userId: String): Flow<List<EmotionRecordEntity>>

    @Query("SELECT * FROM emotion_records WHERE synced = 0 AND userId = :userId ORDER BY timestamp ASC")
    suspend fun getUnsyncedEmotions(userId: String): List<EmotionRecordEntity>

    @Query("UPDATE emotion_records SET synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
