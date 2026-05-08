package com.pranayharjai7.myemotions.data.repository

import com.pranayharjai7.myemotions.domain.model.Friendship
import com.pranayharjai7.myemotions.domain.model.Profile
import com.pranayharjai7.myemotions.domain.repository.FriendshipRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class FriendshipRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : FriendshipRepository {

    private val _friendships = MutableStateFlow<List<Friendship>>(emptyList())
    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())

    override fun getFriendships(): Flow<List<Friendship>> = _friendships.asStateFlow()
    override fun getProfiles(): Flow<List<Profile>> = _profiles.asStateFlow()

    override suspend fun refreshFriendships() {
        try {
            val user = supabaseClient.auth.currentUserOrNull() ?: return
            // Supabase postgrest query equivalent for (user_id = user.id OR friend_id = user.id)
            val friendships = supabaseClient.postgrest["friendships"]
                .select()
                .decodeList<Friendship>()
            _friendships.value = friendships

            val profileIds = friendships.flatMap { listOf(it.user_id, it.friend_id) }.distinct().filter { it != user.id }
            if (profileIds.isNotEmpty()) {
                val profiles = supabaseClient.postgrest["profiles"]
                    .select {
                        filter {
                            isIn("id", profileIds)
                        }
                    }
                    .decodeList<Profile>()
                _profiles.value = profiles
            } else {
                _profiles.value = emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun searchUsers(query: String): Result<List<Profile>> = withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("search_query", query)
            }
            val result = supabaseClient.postgrest.rpc("search_users", params).decodeList<Profile>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendFriendRequest(friendId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = supabaseClient.auth.currentUserOrNull() ?: throw Exception("Not logged in")
            val payload = buildJsonObject {
                put("user_id", user.id)
                put("friend_id", friendId)
                put("status", "pending")
            }
            supabaseClient.postgrest["friendships"].insert(payload)
            refreshFriendships()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptFriendRequest(friendshipId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val payload = buildJsonObject {
                put("status", "accepted")
            }
            supabaseClient.postgrest["friendships"].update(payload) {
                filter {
                    eq("id", friendshipId)
                }
            }
            refreshFriendships()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejectFriendRequest(friendshipId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest["friendships"].delete {
                filter {
                    eq("id", friendshipId)
                }
            }
            refreshFriendships()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFriend(friendshipId: String): Result<Unit> = rejectFriendRequest(friendshipId)

    override suspend fun blockUser(friendId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = supabaseClient.auth.currentUserOrNull() ?: throw Exception("Not logged in")
            
            // Delete any existing friendships
            supabaseClient.postgrest["friendships"].delete {
                filter {
                    or {
                        and {
                            eq("user_id", user.id)
                            eq("friend_id", friendId)
                        }
                        and {
                            eq("user_id", friendId)
                            eq("friend_id", user.id)
                        }
                    }
                }
            }

            // Insert blocked status
            val payload = buildJsonObject {
                put("user_id", user.id)
                put("friend_id", friendId)
                put("status", "blocked")
            }
            supabaseClient.postgrest["friendships"].insert(payload)
            refreshFriendships()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unblockUser(friendId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = supabaseClient.auth.currentUserOrNull() ?: throw Exception("Not logged in")
            supabaseClient.postgrest["friendships"].delete {
                filter {
                    eq("user_id", user.id)
                    eq("friend_id", friendId)
                    eq("status", "blocked")
                }
            }
            refreshFriendships()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
