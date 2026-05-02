package com.example.misterdil.data.remote

import com.example.misterdil.data.models.Conversation
import com.example.misterdil.data.models.Message
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

data class CreateConversationRequest(
    @SerializedName("admin_id")
    val adminId: String,
    @SerializedName("admin_name")
    val adminName: String,
    @SerializedName("project_name")
    val projectName: String,
    @SerializedName("dossier_id")
    val dossierId: String? = null
)

data class UploadResponse(
    @SerializedName("fileId")
    val fileId: String,
    @SerializedName("fileName")
    val fileName: String,
    @SerializedName("fileUrl")
    val fileUrl: String
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

    @POST("upload")
    suspend fun uploadFile(
        @Body request: UploadRequest
    ): UploadResponse
}

data class UploadRequest(
    @SerializedName("fileName")
    val fileName: String,
    @SerializedName("fileData")
    val fileData: String // Base64 encoded
)
