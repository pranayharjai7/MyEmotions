package com.pranayharjai7.myemotions.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val IS_DYNAMIC_THEME = booleanPreferencesKey("is_dynamic_theme")
        val ARE_NOTIFICATIONS_ENABLED = booleanPreferencesKey("are_notifications_enabled")
    }

    val isDynamicTheme: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_DYNAMIC_THEME] ?: true
        }

    val areNotificationsEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.ARE_NOTIFICATIONS_ENABLED] ?: true
        }

    suspend fun setDynamicTheme(isDynamic: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DYNAMIC_THEME] = isDynamic
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ARE_NOTIFICATIONS_ENABLED] = enabled
        }
    }
}
