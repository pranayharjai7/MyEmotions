package com.pranayharjai7.myemotions.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.pranayharjai7.myemotions.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    override fun getGhostMode(userId: String): Flow<Boolean> {
        val key = booleanPreferencesKey("ghost_mode_$userId")
        return dataStore.data.map { preferences ->
            preferences[key] ?: false
        }
    }

    override suspend fun setGhostMode(userId: String, isGhostMode: Boolean) {
        val key = booleanPreferencesKey("ghost_mode_$userId")
        dataStore.edit { preferences ->
            preferences[key] = isGhostMode
        }
    }
}
