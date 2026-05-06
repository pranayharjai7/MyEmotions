package com.pranayharjai7.myemotions.data.repository

import com.pranayharjai7.myemotions.domain.model.LocationRecord
import com.pranayharjai7.myemotions.domain.repository.LocationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresListDataFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

@Serializable
data class LocationDto(
    val user_id: String,
    val latitude: Double,
    val longitude: Double,
    val ghost_mode: Boolean,
    val updated_at: String,
    val last_emotion: String? = null
)

class LocationRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : LocationRepository {

    private val _friendLocations = MutableStateFlow<List<LocationRecord>>(emptyList())
    
    @OptIn(io.github.jan.supabase.annotations.SupabaseExperimental::class)
    override fun getFriendLocations(): Flow<List<LocationRecord>> = supabaseClient.realtime
        .channel("locations")
        .postgresListDataFlow(
            table = "locations",
            schema = "public",
            primaryKey = LocationDto::user_id
        )
        .map { dtos ->
            dtos.map { dto ->
                LocationRecord(
                    userId = dto.user_id,
                    latitude = dto.latitude,
                    longitude = dto.longitude,
                    ghostMode = dto.ghost_mode,
                    updatedAt = System.currentTimeMillis(),
                    lastEmotion = dto.last_emotion
                )
            }
        }
        .onStart { refreshLocations() } // Initial fetch
        .flowOn(Dispatchers.IO)

    override suspend fun updateLocation(latitude: Double, longitude: Double): Unit = withContext(Dispatchers.IO) {
        try {
            val user = supabaseClient.auth.currentUserOrNull() ?: return@withContext
            val payload = buildJsonObject {
                put("user_id", user.id)
                put("latitude", latitude)
                put("longitude", longitude)
            }
            supabaseClient.postgrest["locations"].upsert(payload)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun setGhostMode(enabled: Boolean): Unit = withContext(Dispatchers.IO) {
        try {
            val user = supabaseClient.auth.currentUserOrNull() ?: return@withContext
            val payload = buildJsonObject {
                put("ghost_mode", enabled)
            }
            supabaseClient.postgrest["locations"].update(payload) {
                filter {
                    eq("user_id", user.id)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun refreshLocations(): Unit = withContext(Dispatchers.IO) {
        try {
            val dtos = supabaseClient.postgrest["locations"].select().decodeList<LocationDto>()
            val records = dtos.map {
                LocationRecord(
                    userId = it.user_id,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    ghostMode = it.ghost_mode,
                    updatedAt = System.currentTimeMillis(), // Assuming parsed or simple current for UI
                    lastEmotion = it.last_emotion
                )
            }
            _friendLocations.value = records
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
