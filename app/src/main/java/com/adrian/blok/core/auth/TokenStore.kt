package com.adrian.blok.core.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "blok_auth")

class TokenStore(private val context: Context) {
    private val tokenKey = stringPreferencesKey("access_token")

    @Volatile
    var cachedToken: String? = null
        private set

    val tokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[tokenKey]
    }

    suspend fun getToken(): String? {
        val token = tokenFlow.first()
        cachedToken = token
        return token
    }

    suspend fun setToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[tokenKey] = token
        }
        cachedToken = token
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(tokenKey)
        }
        cachedToken = null
    }
}
