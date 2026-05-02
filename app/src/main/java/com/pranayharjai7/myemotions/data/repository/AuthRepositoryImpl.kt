package com.pranayharjai7.myemotions.data.repository

import com.pranayharjai7.myemotions.data.remote.supabase.SupabaseManager
import com.pranayharjai7.myemotions.domain.repository.AuthRepository
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl : AuthRepository {
    
    private val auth = SupabaseManager.client.auth

    override val currentUser: Flow<UserInfo?> = auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Authenticated -> status.session.user
            else -> null
        }
    }

    override suspend fun signUp(email: String, password: String) {
        auth.signUpWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signIn(email: String, password: String) {
        auth.signInWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signInWithGoogle(idToken: String) {
        auth.signInWith(io.github.jan.supabase.gotrue.providers.builtin.IDToken) {
            this.idToken = idToken
            this.provider = Google
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return auth.currentSessionOrNull() != null
    }
}
