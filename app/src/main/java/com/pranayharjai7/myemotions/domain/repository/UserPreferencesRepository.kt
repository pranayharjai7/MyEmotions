package com.pranayharjai7.myemotions.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getGhostMode(userId: String): Flow<Boolean>
    suspend fun setGhostMode(userId: String, isGhostMode: Boolean)
}
