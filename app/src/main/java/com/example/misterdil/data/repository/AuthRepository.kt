package com.example.misterdil.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.misterdil.data.remote.AuthApiService
import com.example.misterdil.data.remote.LoginRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthRepository(
    private val apiService: AuthApiService,
    private val context: Context
) {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    suspend fun login(email: String, password: String) {
        val response = apiService.login(LoginRequest(email, password))
        saveToken(response.token)
    }

    suspend fun logout() {
        try {
            apiService.logout()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        clearToken()
    }

    private suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    private suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}
