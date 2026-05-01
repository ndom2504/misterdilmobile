package com.example.misterdil.data.repository

import com.example.misterdil.data.local.DossierDao
import com.example.misterdil.data.models.Dossier
import com.example.misterdil.data.remote.AdminProfile
import com.example.misterdil.data.remote.CreateDossierRequest
import com.example.misterdil.data.remote.DossierApiService
import com.example.misterdil.data.remote.UpdateProfileRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DossierRepository(
    private val apiService: DossierApiService,
    private val dossierDao: DossierDao
) {
    val allDossiers: Flow<List<Dossier>> = dossierDao.getAllDossiers()

    suspend fun refreshDossiers() {
        withContext(Dispatchers.IO) {
            try {
                val remoteDossiers = apiService.getDossiers()
                dossierDao.insertDossiers(remoteDossiers)
            } catch (e: Exception) {
                // Ici on pourrait gérer les erreurs plus finement (ex: Timber ou log)
                e.printStackTrace()
            }
        }
    }

    suspend fun createDossier(type: String, formData: Map<String, String>): Dossier {
        return withContext(Dispatchers.IO) {
            val dossier = apiService.createDossier(CreateDossierRequest(type, formData))
            dossierDao.insertDossiers(listOf(dossier))
            dossier
        }
    }

    suspend fun getAdmins(): List<AdminProfile> {
        return withContext(Dispatchers.IO) {
            apiService.getAdmins()
        }
    }

    suspend fun updateProfile(name: String? = null, avatar_url: String? = null): AdminProfile {
        return withContext(Dispatchers.IO) {
            apiService.updateProfile(UpdateProfileRequest(name, avatar_url))
        }
    }
}
