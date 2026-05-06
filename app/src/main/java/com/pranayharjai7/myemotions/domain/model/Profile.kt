package com.pranayharjai7.myemotions.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val username: String? = null,
    val display_name: String? = null,
    val email: String? = null,
    val avatar_url: String? = null
)
