package com.example.glowmance.data.repository

import com.example.glowmance.data.network.ApiService
import com.example.glowmance.data.network.Product

/**
 * Repository for product recommendations
 */
class ProductRepository(
    private val apiService: ApiService
) {
    suspend fun getTrendingProducts(limit: Int? = null): Result<List<Product>> {
        return try {
            val response = apiService.getTrendingProducts(limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get trending products: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProductRecommendations(
        limit: Int? = null,
        includeTrending: Boolean? = null
    ): Result<List<Product>> {
        return try {
            val response = apiService.getProductRecommendations(limit, includeTrending)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get recommendations: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
