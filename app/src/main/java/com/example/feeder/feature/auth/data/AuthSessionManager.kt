package com.example.feeder.feature.auth.data

import android.content.Context
import androidx.core.content.edit


data class AuthSession (
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val email: String,
    val username: String
)

class AuthSessionManager(
    context : Context
)
{
    private val prefs = context.getSharedPreferences(
        "auth_session",
        Context.MODE_PRIVATE
    )

    fun saveSession(session: AuthSession) {
        prefs.edit {
            putString("access_token", session.accessToken)
                .putString("refresh_token", session.refreshToken)
                .putString("user_id", session.userId)
                .putString("email", session.email)
                .putString("username", session.username)
        }
    }
    fun getSession(): AuthSession? {
        val accessToken = prefs.getString("access_token", null)
        val refreshToken = prefs.getString("refresh_token", null)
        val userId = prefs.getString("user_id", null)
        val email = prefs.getString("email", null)
        val username = prefs.getString("username", null)
        if (
            accessToken.isNullOrBlank() ||
            refreshToken.isNullOrBlank() ||
            userId.isNullOrBlank() ||
            email.isNullOrBlank() ||
            username.isNullOrBlank()
        ) {
            return null
        }
        return AuthSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            email = email,
            username = username
        )
    }

    fun clearSession () {
        prefs.edit { clear() }
    }
    fun isSignedIn() : Boolean {
        return getSession() != null
    }
}
