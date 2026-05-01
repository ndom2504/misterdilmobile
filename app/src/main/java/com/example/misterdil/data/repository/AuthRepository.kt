package com.example.misterdil.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.misterdil.data.remote.AuthApiService
import com.example.misterdil.data.remote.GoogleAuthRequest
import com.example.misterdil.data.remote.LoginRequest
import com.example.misterdil.data.remote.RegisterRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthRepository(
    private val apiService: AuthApiService,
    private val context: Context
) {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val ROLE_KEY  = stringPreferencesKey("user_role")
    private val NAME_KEY  = stringPreferencesKey("user_name")

    val authToken: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userRole:  Flow<String?> = context.dataStore.data.map { it[ROLE_KEY] }
    val userName:  Flow<String?> = context.dataStore.data.map { it[NAME_KEY] }

    suspend fun login(email: String, password: String) {
        val r = apiService.login(LoginRequest(email, password))
        saveSession(r.token, r.role, r.name)
    }

    suspend fun register(name: String, email: String, password: String, role: String) {
        val r = apiService.register(RegisterRequest(name, email, password, role))
        saveSession(r.token, r.role, r.name)
    }

    suspend fun loginWithGoogle(idToken: String) {
        val r = apiService.googleAuth(GoogleAuthRequest(idToken))
        saveSession(r.token, r.role, r.name)
    }

    suspend fun logout() {
        try { apiService.logout() } catch (e: Exception) { e.printStackTrace() }
        context.dataStore.edit { it.clear() }
    }

    private suspend fun saveSession(token: String, role: String, name: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[ROLE_KEY]  = role
            prefs[NAME_KEY]  = name
        }
    }
}
