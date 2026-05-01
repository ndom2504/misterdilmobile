package com.example.misterdil.data.remote

import com.example.misterdil.data.models.Dossier
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class AdminProfile(
    val id: Int,
    val name: String,
    val email: String
)

data class CreateDossierRequest(
    val type: String,
    @SerializedName("form_data")
    val formData: Map<String, String> = emptyMap()
)

interface DossierApiService {
    @GET("dossiers")
    suspend fun getDossiers(): List<Dossier>

    @GET("dossiers/{id}")
    suspend fun getDossierById(@Path("id") id: String): Dossier

    @POST("dossiers")
    suspend fun createDossier(@Body request: CreateDossierRequest): Dossier

    @GET("admins")
    suspend fun getAdmins(): List<AdminProfile>

    companion object {
        val BASE_URL: String = "https://backend-chi-olive-77.vercel.app/api/v1/"
    }
}
