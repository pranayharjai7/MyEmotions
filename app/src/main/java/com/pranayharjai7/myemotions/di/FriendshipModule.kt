package com.pranayharjai7.myemotions.di

import com.pranayharjai7.myemotions.data.repository.FriendshipRepositoryImpl
import com.pranayharjai7.myemotions.domain.repository.FriendshipRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FriendshipModule {

    @Binds
    @Singleton
    abstract fun bindFriendshipRepository(
        friendshipRepositoryImpl: FriendshipRepositoryImpl
    ): FriendshipRepository
}
