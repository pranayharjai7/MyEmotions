package com.pranayharjai7.myemotions.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EmotionRecordEntity::class], version = 1, exportSchema = false)
abstract class EmotionDatabase : RoomDatabase() {
    abstract val emotionDao: EmotionDao
}
