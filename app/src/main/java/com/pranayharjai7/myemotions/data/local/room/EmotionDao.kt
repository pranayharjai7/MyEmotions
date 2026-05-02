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

    @Query("SELECT * FROM emotion_records ORDER BY timestamp DESC")
    fun getAllEmotions(): Flow<List<EmotionRecordEntity>>

    @Query("SELECT * FROM emotion_records WHERE synced = 0 ORDER BY timestamp ASC")
    suspend fun getUnsyncedEmotions(): List<EmotionRecordEntity>

    @Query("UPDATE emotion_records SET synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
