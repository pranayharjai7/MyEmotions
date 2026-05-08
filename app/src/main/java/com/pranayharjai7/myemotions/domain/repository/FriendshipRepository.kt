package com.pranayharjai7.myemotions.domain.repository

import com.pranayharjai7.myemotions.domain.model.Friendship
import com.pranayharjai7.myemotions.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface FriendshipRepository {
    fun getFriendships(): Flow<List<Friendship>>
    fun getProfiles(): Flow<List<Profile>>
    suspend fun searchUsers(query: String): Result<List<Profile>>
    suspend fun sendFriendRequest(friendId: String): Result<Unit>
    suspend fun acceptFriendRequest(friendshipId: String): Result<Unit>
    suspend fun rejectFriendRequest(friendshipId: String): Result<Unit>
    suspend fun removeFriend(friendshipId: String): Result<Unit>
    suspend fun blockUser(friendId: String): Result<Unit>
    suspend fun unblockUser(friendId: String): Result<Unit>
    suspend fun refreshFriendships()
}
