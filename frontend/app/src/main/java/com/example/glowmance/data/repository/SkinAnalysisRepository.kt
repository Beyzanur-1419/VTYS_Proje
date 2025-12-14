package com.example.glowmance.data.repository

import com.example.glowmance.data.network.ApiService
import com.example.glowmance.data.network.AnalysisHistoryItem
import com.example.glowmance.data.network.AnalysisStats
import com.example.glowmance.data.network.CreateAnalysisRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Repository for skin analysis operations
 */
class SkinAnalysisRepository(
    private val apiService: ApiService
) {
    suspend fun uploadImage(imageFile: File): Result<String> {
        return try {
            val requestFile = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
            
            val response = apiService.uploadImage(requestFile)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.url)
            } else {
                Result.failure(Exception("Image upload failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createAnalysis(request: CreateAnalysisRequest): Result<AnalysisHistoryItem> {
        return try {
            val response = apiService.createAnalysis(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Analysis creation failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAnalysisHistory(
        page: Int? = null,
        limit: Int? = null,
        offset: Int? = null
    ): Result<List<AnalysisHistoryItem>> {
        return try {
            val response = apiService.getAnalysisHistory(page, limit, offset)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get history: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAnalysisStats(): Result<AnalysisStats> {
        return try {
            val response = apiService.getAnalysisStats()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get stats: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAnalysisById(id: String): Result<AnalysisHistoryItem> {
        return try {
            val response = apiService.getAnalysisById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get analysis: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteAnalysis(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteAnalysis(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete analysis: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
