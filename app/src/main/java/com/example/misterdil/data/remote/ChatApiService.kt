package com.example.misterdil.data.remote

import com.example.misterdil.data.models.Conversation
import com.example.misterdil.data.models.Message
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApiService {
    @GET("conversations")
    suspend fun getConversations(): List<Conversation>

    @GET("conversations/{conversationId}/messages")
    suspend fun getMessages(@Path("conversationId") conversationId: String): List<Message>

    @POST("conversations/{conversationId}/messages")
    suspend fun sendMessage(
        @Path("conversationId") conversationId: String,
        @Body message: Message
    ): Message
}
