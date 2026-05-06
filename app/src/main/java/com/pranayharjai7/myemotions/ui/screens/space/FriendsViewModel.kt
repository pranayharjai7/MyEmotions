package com.pranayharjai7.myemotions.ui.screens.space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.model.Friendship
import com.pranayharjai7.myemotions.domain.model.Profile
import com.pranayharjai7.myemotions.domain.repository.AuthRepository
import com.pranayharjai7.myemotions.domain.repository.FriendshipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendshipRepository: FriendshipRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val friendships = friendshipRepository.getFriendships()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profiles = friendshipRepository.getProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchResults = MutableStateFlow<List<Profile>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            friendshipRepository.refreshFriendships()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.length >= 3) {
            viewModelScope.launch {
                val result = friendshipRepository.searchUsers(query)
                if (result.isSuccess) {
                    _searchResults.value = result.getOrNull() ?: emptyList()
                } else {
                    _searchResults.value = emptyList()
                }
            }
        } else {
            _searchResults.value = emptyList()
        }
    }

    fun sendFriendRequest(friendId: String) {
        viewModelScope.launch {
            friendshipRepository.sendFriendRequest(friendId)
        }
    }

    fun acceptRequest(friendshipId: String) {
        viewModelScope.launch {
            friendshipRepository.acceptFriendRequest(friendshipId)
        }
    }

    fun rejectRequest(friendshipId: String) {
        viewModelScope.launch {
            friendshipRepository.rejectFriendRequest(friendshipId)
        }
    }

    fun removeFriend(friendshipId: String) {
        viewModelScope.launch {
            friendshipRepository.removeFriend(friendshipId)
        }
    }
}
