package com.pranayharjai7.myemotions.ui.screens.space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.model.LocationRecord
import com.pranayharjai7.myemotions.domain.model.Profile
import com.pranayharjai7.myemotions.domain.repository.AuthRepository
import com.pranayharjai7.myemotions.domain.repository.FriendshipRepository
import com.pranayharjai7.myemotions.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.pranayharjai7.myemotions.domain.repository.UserPreferencesRepository

data class FriendMapMarker(
    val profile: Profile,
    val location: LocationRecord,
    val isStale: Boolean
)

@HiltViewModel
class SpaceMapViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val friendshipRepository: FriendshipRepository,
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val friendLocations = locationRepository.getFriendLocations()
    val profiles = friendshipRepository.getProfiles()

    val mapMarkers = combine(friendLocations, profiles) { locations, profiles ->
        locations.mapNotNull { loc ->
            val profile = profiles.find { it.id == loc.userId }
            if (profile != null) {
                val isStale = (System.currentTimeMillis() - loc.updatedAt) > 24 * 60 * 60 * 1000 // 24 hours
                FriendMapMarker(profile, loc, isStale)
            } else {
                null
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isGhostMode: StateFlow<Boolean> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user != null) {
                userPreferencesRepository.getGhostMode(user.id)
            } else {
                flowOf(false)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _selectedFriend = MutableStateFlow<FriendMapMarker?>(null)
    val selectedFriend: StateFlow<FriendMapMarker?> = _selectedFriend.asStateFlow()

    init {
        refreshData()
    }

    fun selectFriend(friend: FriendMapMarker?) {
        _selectedFriend.value = friend
    }

    fun toggleGhostMode() {
        viewModelScope.launch {
            val user = authRepository.currentUser.firstOrNull() ?: return@launch
            val currentMode = isGhostMode.value
            userPreferencesRepository.setGhostMode(user.id, !currentMode)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            friendshipRepository.refreshFriendships()
            locationRepository.refreshLocations()
        }
    }
    
    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            locationRepository.updateLocation(latitude, longitude)
        }
    }
}
