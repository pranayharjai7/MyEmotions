package com.pranayharjai7.myemotions.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Friendship(
    val id: String,
    val user_id: String,
    val friend_id: String,
    val status: String,
    val created_at: String
)
