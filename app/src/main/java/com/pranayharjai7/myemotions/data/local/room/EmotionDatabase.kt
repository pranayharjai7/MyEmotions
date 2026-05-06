package com.pranayharjai7.myemotions.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [EmotionRecordEntity::class], version = 3, exportSchema = false)
abstract class EmotionDatabase : RoomDatabase() {
    abstract val emotionDao: EmotionDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Destructive migration: wipe records since we can't reliably associate them with an account
                database.execSQL("DROP TABLE IF EXISTS `emotion_records`")
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `emotion_records` (" +
                    "`id` TEXT NOT NULL, " +
                    "`userId` TEXT NOT NULL, " +
                    "`timestamp` INTEGER NOT NULL, " +
                    "`emotion` TEXT NOT NULL, " +
                    "`confidence` REAL NOT NULL, " +
                    "`source` TEXT NOT NULL, " +
                    "`imageUri` TEXT, " +
                    "`visibility` TEXT NOT NULL, " +
                    "`note` TEXT, " +
                    "`synced` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`id`))"
                )
            }
        }
    }
}
