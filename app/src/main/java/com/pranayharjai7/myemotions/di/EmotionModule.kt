package com.pranayharjai7.myemotions.di

import com.pranayharjai7.myemotions.data.repository.EmotionRepositoryImpl
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class EmotionModule {

    @Binds
    abstract fun bindEmotionRepository(
        impl: EmotionRepositoryImpl
    ): EmotionRepository
}
