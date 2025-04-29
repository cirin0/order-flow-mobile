package com.cirin0.orderflowmobile.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cirin0.orderflowmobile.domain.model.AuthResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val EMAIL = stringPreferencesKey("email")
        private val ROLE = stringPreferencesKey("role")
        private val TOKEN_EXPIRATION = longPreferencesKey("token_expiration")
        private val REFRESH_EXPIRATION = longPreferencesKey("refresh_expiration")
    }

    val accessToken: Flow<String> = dataStore.data.map { it[ACCESS_TOKEN] ?: "" }
    val refreshToken: Flow<String> = dataStore.data.map { it[REFRESH_TOKEN] ?: "" }
    val userId: Flow<String> = dataStore.data.map { it[USER_ID] ?: "" }
    val email: Flow<String> = dataStore.data.map { it[EMAIL] ?: "" }
    val role: Flow<String> = dataStore.data.map { it[ROLE] ?: "" }
    val tokenExpiration: Flow<Long> = dataStore.data.map { it[TOKEN_EXPIRATION] ?: 0L }
    val refreshExpiration: Flow<Long> = dataStore.data.map { it[REFRESH_EXPIRATION] ?: 0L }


    suspend fun saveAuthData(authResponse: AuthResponse) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = authResponse.accessToken
            preferences[REFRESH_TOKEN] = authResponse.refreshToken
            preferences[USER_ID] = authResponse.userId
            preferences[EMAIL] = authResponse.email
            preferences[ROLE] = authResponse.role
            preferences[TOKEN_EXPIRATION] = authResponse.expirationTime
            preferences[REFRESH_EXPIRATION] = authResponse.refreshExpirationTime
        }
    }

    suspend fun clearAuthData() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
            preferences.remove(USER_ID)
            preferences.remove(EMAIL)
            preferences.remove(ROLE)
            preferences.remove(TOKEN_EXPIRATION)
            preferences.remove(REFRESH_EXPIRATION)
        }
    }

    suspend fun isTokenValid(): Boolean {
        val preferences = dataStore.data.map { it }.firstOrNull()
        val expirationTime = preferences?.get(TOKEN_EXPIRATION) ?: 0L
        return System.currentTimeMillis() < expirationTime
    }
}
