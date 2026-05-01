package com.example.misterdil.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.*

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String = "user"
)

data class GoogleAuthRequest(
    @SerializedName("idToken") val idToken: String
)

data class UpdateProfileRequest(
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("language") val language: String
)

data class ChangePasswordRequest(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String
)

data class UpdateNotificationsRequest(
    @SerializedName("notificationsEnabled") val notificationsEnabled: Boolean,
    @SerializedName("paymentNotificationsEnabled") val paymentNotificationsEnabled: Boolean
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String = "user"
)

data class ProfileResponse(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("language") val language: String
)

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout()

    @PUT("auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ProfileResponse

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest)

    @PUT("auth/notifications")
    suspend fun updateNotifications(@Body request: UpdateNotificationsRequest)

    @DELETE("auth/account")
    suspend fun deleteAccount()
}
