package com.pranayharjai7.myemotions.ui.screens.space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

@Serializable
data class ReactionItem(
    val reaction_type: String,
    val user_id: String,
    val display_name: String?,
    val username: String?
)

@Serializable
data class FeedItem(
    val emotion_id: String,
    val user_id: String,
    val username: String?,
    val display_name: String?,
    val avatar_url: String?,
    val emotion: String,
    val note: String?,
    val unix_timestamp: Long,
    val created_at: String,
    val reactions: List<ReactionItem> = emptyList()
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _feedItems = MutableStateFlow<List<FeedItem>>(emptyList())
    val feedItems = _feedItems.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val feed = supabaseClient.postgrest.rpc("get_space_feed").decodeList<FeedItem>()
                _feedItems.value = feed
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendReaction(emotionId: String, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = authRepository.currentUser.firstOrNull() ?: return@launch
                
                // Optimistic UI Update
                val newReaction = ReactionItem(
                    reaction_type = type,
                    user_id = user.id,
                    display_name = (user.userMetadata?.get("full_name") ?: user.userMetadata?.get("display_name"))?.toString()?.replace("\"", ""),
                    username = null
                )
                
                _feedItems.value = _feedItems.value.map { item ->
                    if (item.emotion_id == emotionId) {
                        // Remove existing reaction from this user if any
                        val filteredReactions = item.reactions.filter { it.user_id != user.id }.toMutableList()
                        filteredReactions.add(newReaction)
                        item.copy(reactions = filteredReactions)
                    } else {
                        item
                    }
                }

                // Backend upsert
                val payload = buildJsonObject {
                    put("emotion_id", emotionId)
                    put("user_id", user.id)
                    put("reaction_type", type)
                }
                supabaseClient.postgrest["emotion_reactions"].upsert(payload)
                
            } catch (e: Exception) {
                e.printStackTrace()
                loadFeed() // Revert on failure
            }
        }
    }
    
    fun removeReaction(emotionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = authRepository.currentUser.firstOrNull() ?: return@launch
                
                // Optimistic UI Update
                _feedItems.value = _feedItems.value.map { item ->
                    if (item.emotion_id == emotionId) {
                        item.copy(reactions = item.reactions.filter { it.user_id != user.id })
                    } else {
                        item
                    }
                }

                supabaseClient.postgrest["emotion_reactions"]
                    .delete {
                        filter {
                            eq("emotion_id", emotionId)
                            eq("user_id", user.id)
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                loadFeed() // Revert on failure
            }
        }
    }
}
