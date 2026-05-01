package com.example.misterdil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.misterdil.data.models.Dossier
import kotlinx.coroutines.flow.Flow

@Dao
interface DossierDao {
    @Query("SELECT * FROM dossiers")
    fun getAllDossiers(): Flow<List<Dossier>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDossiers(dossiers: List<Dossier>)

    @Query("DELETE FROM dossiers")
    suspend fun clearAll()
}
