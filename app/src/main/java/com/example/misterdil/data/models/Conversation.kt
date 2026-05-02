package com.example.misterdil.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("client_name")
    val clientName: String,
    @SerializedName("project_name")
    val projectName: String,
    @SerializedName("last_message")
    val lastMessage: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("unread_count")
    val unreadCount: Int = 0,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null
)
