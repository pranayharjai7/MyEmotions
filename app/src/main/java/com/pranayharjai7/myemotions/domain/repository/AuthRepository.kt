package com.pranayharjai7.myemotions.domain.repository

import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<UserInfo?>
    
    suspend fun signUp(email: String, password: String)
    suspend fun signIn(email: String, password: String)
    suspend fun signInWithGoogle(idToken: String)
    suspend fun signOut()
    suspend fun isUserLoggedIn(): Boolean
}
