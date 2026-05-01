package com.example.misterdil

import android.app.Application
import com.example.misterdil.data.local.AppDatabase
import com.example.misterdil.data.remote.AuthApiService
import com.example.misterdil.data.remote.AuthInterceptor
import com.example.misterdil.data.remote.ChatApiService
import com.example.misterdil.data.remote.DossierApiService
import com.example.misterdil.data.remote.PaymentApiService
import com.example.misterdil.data.repository.AuthRepository
import com.example.misterdil.data.repository.ChatRepository
import com.example.misterdil.data.repository.DossierRepository
import com.stripe.android.PaymentConfiguration
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MisterdilApplication : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }

    val authRepository by lazy {
        AuthRepository(authApiServiceWithoutAuth, this)
    }

    private val okHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(authRepository))
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(DossierApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Version de l'API service pour le login (sans l'intercepteur de token pour éviter la boucle infinie ou l'absence de token)
    private val authApiServiceWithoutAuth by lazy {
        Retrofit.Builder()
            .baseUrl(DossierApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    private val apiService by lazy {
        retrofit.create(DossierApiService::class.java)
    }

    val paymentApiService by lazy {
        retrofit.create(PaymentApiService::class.java)
    }

    private val chatApiService by lazy {
        retrofit.create(ChatApiService::class.java)
    }

    val repository by lazy { 
        DossierRepository(apiService, database.dossierDao()) 
    }

    val chatRepository by lazy {
        ChatRepository(
            chatApiService, 
            database.messageDao(),
            database.conversationDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_your_publishable_key"
        )
    }
}
