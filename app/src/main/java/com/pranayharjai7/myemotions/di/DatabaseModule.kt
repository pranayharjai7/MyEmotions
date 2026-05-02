package com.pranayharjai7.myemotions.di

import android.content.Context
import androidx.room.Room
import com.pranayharjai7.myemotions.data.local.room.EmotionDao
import com.pranayharjai7.myemotions.data.local.room.EmotionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEmotionDatabase(@ApplicationContext context: Context): EmotionDatabase {
        return Room.databaseBuilder(
            context,
            EmotionDatabase::class.java,
            "EmotionDatabase"
        ).build()
    }

    @Provides
    @Singleton
    fun provideEmotionDao(database: EmotionDatabase): EmotionDao {
        return database.emotionDao
    }
}
