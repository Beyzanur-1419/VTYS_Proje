package com.example.glowmance.data.api

import com.example.glowmance.data.model.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query

interface ProductApi {
    @GET("products")
    suspend fun getAllProducts(): Response<ProductResponse>

    @GET("products/recommendations")
    suspend fun getRecommendations(
        @Query("conditions") conditions: String
    ): Response<ProductResponse>
    @POST("products/recommendations/advanced")
    suspend fun getAdvancedRecommendations(
        @Body request: com.example.glowmance.data.model.AdvancedRecommendationRequest
    ): Response<com.example.glowmance.data.model.ProductResponse>
}
