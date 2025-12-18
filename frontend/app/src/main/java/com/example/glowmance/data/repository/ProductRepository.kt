package com.example.glowmance.data.repository

import com.example.glowmance.data.api.RetrofitClient
import com.example.glowmance.data.model.Product

class ProductRepository {
    private val api = RetrofitClient.productApi

    suspend fun getRecommendations(conditions: List<String>): Result<List<Product>> {
        return try {
            val conditionsString = conditions.joinToString(",")
            val response = api.getRecommendations(conditionsString)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Ürünler yüklenemedi: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val response = api.getAllProducts()
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Ürünler yüklenemedi: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getAdvancedRecommendations(request: com.example.glowmance.data.model.AdvancedRecommendationRequest): Result<List<Product>> {
        return try {
            val response = api.getAdvancedRecommendations(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Ürünler yüklenemedi: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
