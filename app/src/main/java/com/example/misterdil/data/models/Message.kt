package com.example.misterdil.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @ColumnInfo(name = "conversation_id")
    @SerializedName("conversation_id")
    val conversationId: String,
    @ColumnInfo(name = "sender_id")
    @SerializedName("sender_id")
    val senderId: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "is_from_me")
    @SerializedName("is_from_me")
    val isFromMe: Boolean,
    @ColumnInfo(name = "sender_avatar")
    @SerializedName("sender_avatar")
    val senderAvatar: String? = null
)
