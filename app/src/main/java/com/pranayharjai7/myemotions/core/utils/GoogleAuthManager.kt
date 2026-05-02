package com.pranayharjai7.myemotions.core.utils

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.pranayharjai7.myemotions.BuildConfig

/**
 * Manages Google authentication using the Credential Manager API.
 */
class GoogleAuthManager(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    /**
     * Initiates the Google Sign-In flow and returns the ID token on success, or null on failure/cancellation.
     */
    @android.annotation.SuppressLint("CredentialManagerSignInWithGoogle")
    suspend fun signIn(): String? {
        val activity = context as? Activity ?: return null

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential

            when {
                credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    googleIdTokenCredential.idToken
                }
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Clears the current credential state (sign out).
     */
    suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}
