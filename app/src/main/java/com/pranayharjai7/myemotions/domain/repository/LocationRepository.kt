package com.pranayharjai7.myemotions.domain.repository

import com.pranayharjai7.myemotions.domain.model.LocationRecord
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getFriendLocations(): Flow<List<LocationRecord>>
    suspend fun updateLocation(latitude: Double, longitude: Double)
    suspend fun setGhostMode(enabled: Boolean)
    suspend fun refreshLocations()
}
