package com.pranayharjai7.myemotions.ui.screens

/**
 * Represents the various states of authentication.
 */
sealed class AuthState {
    /**
     * The initial state, no authentication action is in progress.
     */
    data object Idle : AuthState()

    /**
     * An authentication action is currently in progress.
     */
    data object Loading : AuthState()

    /**
     * The authentication action was successful.
     */
    data object Success : AuthState()

    /**
     * The authentication action failed with an error message.
     */
    data class Error(val message: String) : AuthState()
}
