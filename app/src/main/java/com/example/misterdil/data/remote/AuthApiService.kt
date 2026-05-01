package com.example.misterdil.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

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

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String = "user"
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
}
