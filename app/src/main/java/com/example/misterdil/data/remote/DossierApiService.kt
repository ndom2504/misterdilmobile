package com.example.misterdil.data.remote

import com.example.misterdil.data.models.Dossier
import retrofit2.http.GET
import retrofit2.http.Path

interface DossierApiService {
    @GET("dossiers")
    suspend fun getDossiers(): List<Dossier>

    @GET("dossiers/{id}")
    suspend fun getDossierById(@Path("id") id: String): Dossier

    companion object {
        const val BASE_URL = "https://misterdil-backend.vercel.app/api/v1/" // Remplacer par l'URL Vercel après déploiement
    }
}
