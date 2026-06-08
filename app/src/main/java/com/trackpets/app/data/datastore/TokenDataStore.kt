package com.trackpets.app.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = intPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val EMAIL = stringPreferencesKey("email")
        val IS_STAFF = booleanPreferencesKey("is_staff")
    }

    suspend fun saveTokens(access: String, refresh: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = access
            preferences[REFRESH_TOKEN] = refresh
        }
    }

    suspend fun saveUserInfo(userId: Int, username: String, email: String, isStaff: Boolean) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USERNAME] = username
            preferences[EMAIL] = email
            preferences[IS_STAFF] = isStaff
        }
    }

    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]
        }
    }

    fun getUserId(): Flow<Int?> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID]
        }
    }

    fun getUsername(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USERNAME]
        }
    }

    fun getIsStaff(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_STAFF] ?: false
        }
    }

    fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN] != null
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
