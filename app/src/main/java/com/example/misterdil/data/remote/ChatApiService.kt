package com.example.misterdil.data.remote

import com.example.misterdil.data.models.Conversation
import com.example.misterdil.data.models.Message
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class CreateConversationRequest(
    @SerializedName("admin_id")
    val adminId: String,
    @SerializedName("admin_name")
    val adminName: String,
    @SerializedName("project_name")
    val projectName: String
)

interface ChatApiService {
    @GET("conversations")
    suspend fun getConversations(): List<Conversation>

    @POST("conversations")
    suspend fun createConversation(@Body request: CreateConversationRequest): Conversation

    @GET("conversations/{conversationId}/messages")
    suspend fun getMessages(@Path("conversationId") conversationId: String): List<Message>

    @POST("conversations/{conversationId}/messages")
    suspend fun sendMessage(
        @Path("conversationId") conversationId: String,
        @Body message: Message
    ): Message
}
