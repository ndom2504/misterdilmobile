package com.example.misterdil.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "dossiers")
data class Dossier(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("client_name")
    val clientName: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("progress")
    val progress: Float,
    @SerializedName("last_update")
    val lastUpdate: String,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("user_id")
    val userId: String? = null
)
