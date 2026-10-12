package com.example.feeder.feature.auth.data

import android.content.Context

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val email: String,
    val username: String
)

class AuthSessionManager(
    context: Context
) {

    companion object {
        private const val PREFS_NAME = "auth_session"

        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_USERNAME = "username"
    }

    private val preferences =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    fun saveSession(session: AuthSession) {
        preferences.edit()
            .putString(KEY_ACCESS_TOKEN, session.accessToken)
            .putString(KEY_REFRESH_TOKEN, session.refreshToken)
            .putString(KEY_USER_ID, session.userId)
            .putString(KEY_EMAIL, session.email)
            .putString(KEY_USERNAME, session.username)
            .apply()
    }

    fun getSession(): AuthSession? {
        val accessToken =
            preferences.getString(KEY_ACCESS_TOKEN, null)
                ?: return null

        val refreshToken =
            preferences.getString(KEY_REFRESH_TOKEN, null)
                ?: return null

        val userId =
            preferences.getString(KEY_USER_ID, null)
                ?: return null

        val email =
            preferences.getString(KEY_EMAIL, null)
                ?: ""

        val username =
            preferences.getString(KEY_USERNAME, null)
                ?: ""

        return AuthSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            email = email,
            username = username
        )
    }

    fun getUserId(): String? {
        return getSession()?.userId
    }

    fun clearSession() {
        preferences.edit()
            .clear()
            .apply()
    }
}