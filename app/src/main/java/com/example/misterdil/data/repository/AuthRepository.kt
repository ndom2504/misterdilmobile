package com.example.misterdil.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.misterdil.data.remote.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthRepository(
    private val apiService: AuthApiService,
    private val context: Context
) {
    private val TOKEN_KEY     = stringPreferencesKey("auth_token")
    private val ROLE_KEY      = stringPreferencesKey("user_role")
    private val NAME_KEY      = stringPreferencesKey("user_name")
    private val EMAIL_KEY     = stringPreferencesKey("user_email")
    private val PHOTO_URI_KEY = stringPreferencesKey("photo_uri")
    private val USER_ID_KEY   = stringPreferencesKey("user_id")

    val authToken: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userRole:  Flow<String?> = context.dataStore.data.map { it[ROLE_KEY] }
    val userName:  Flow<String?> = context.dataStore.data.map { it[NAME_KEY] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[EMAIL_KEY] }
    val photoUri:  Flow<String?> = context.dataStore.data.map { it[PHOTO_URI_KEY] }
    val userId:    Flow<String?> = context.dataStore.data.map { it[USER_ID_KEY] }

    suspend fun login(email: String, password: String) {
        val r = apiService.login(LoginRequest(email, password))
        saveSession(r.token, r.role, r.name, email, r.userId, r.avatarUrl)
    }

    suspend fun register(name: String, email: String, password: String, role: String) {
        val r = apiService.register(RegisterRequest(name, email, password, role))
        saveSession(r.token, r.role, r.name, email, r.userId, r.avatarUrl)
    }

    suspend fun loginWithGoogle(idToken: String) {
        val r = apiService.googleAuth(GoogleAuthRequest(idToken))
        saveSession(r.token, r.role, r.name, "", r.userId, r.avatarUrl)
    }

    suspend fun savePhotoUri(uri: String) {
        context.dataStore.edit { it[PHOTO_URI_KEY] = uri }
    }

    suspend fun saveName(name: String) {
        context.dataStore.edit { it[NAME_KEY] = name }
    }

    suspend fun logout() {
        try { apiService.logout() } catch (e: Exception) { e.printStackTrace() }
        context.dataStore.edit { it.clear() }
    }

    suspend fun updateProfile(name: String, phone: String, language: String, avatar_url: String? = null): AuthResponse {
        // Use a different endpoint that returns the full user object
        return apiService.updateProfileFull(UpdateProfileRequest(name, phone, language, avatar_url))
    }

    suspend fun changePassword(currentPassword: String, newPassword: String) {
        apiService.changePassword(ChangePasswordRequest(currentPassword, newPassword))
    }

    suspend fun updateNotifications(notificationsEnabled: Boolean, paymentNotificationsEnabled: Boolean) {
        apiService.updateNotifications(UpdateNotificationsRequest(notificationsEnabled, paymentNotificationsEnabled))
    }

    suspend fun deleteAccount() {
        apiService.deleteAccount()
        context.dataStore.edit { it.clear() }
    }

    private suspend fun saveSession(token: String, role: String, name: String, email: String, userId: String, avatarUrl: String? = null) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY]   = token
            prefs[ROLE_KEY]    = role
            prefs[NAME_KEY]    = name
            prefs[EMAIL_KEY]   = email
            prefs[USER_ID_KEY] = userId
            if (avatarUrl != null) {
                prefs[PHOTO_URI_KEY] = avatarUrl
            }
        }
    }
}
