package com.pranayharjai7.myemotions.ui.screens.space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.domain.model.Profile
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import com.pranayharjai7.myemotions.domain.repository.FriendshipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FriendProfileState(
    val profile: Profile? = null,
    val emotions: List<EmotionRecord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FriendProfileViewModel @Inject constructor(
    private val friendshipRepository: FriendshipRepository,
    private val emotionRepository: EmotionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FriendProfileState())
    val state = _state.asStateFlow()

    fun loadFriendProfile(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val profiles = friendshipRepository.getProfiles().firstOrNull() ?: emptyList()
                val profile = profiles.find { it.id == userId }
                
                if (profile != null) {
                    _state.update { it.copy(profile = profile) }
                    
                    // Fetch emotions for distribution
                    emotionRepository.getEmotionsByUserId(userId)
                        .onEach { emotions ->
                            _state.update { it.copy(emotions = emotions, isLoading = false) }
                        }
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .collect()
                } else {
                    _state.update { it.copy(error = "Profile not found", isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
