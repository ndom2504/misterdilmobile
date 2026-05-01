package com.example.misterdil.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class PaymentIntentRequest(val amount: Long, val currency: String)
data class PaymentIntentResponse(
    val clientSecret: String,
    val publishableKey: String,
    val customerId: String?,
    val ephemeralKeySecret: String?
)

interface PaymentApiService {
    @POST("payments/create-intent")
    suspend fun createPaymentIntent(@Body request: PaymentIntentRequest): PaymentIntentResponse
}
