package com.example.glowmance.data.repository

import com.example.glowmance.data.api.AnalysisApi
import com.example.glowmance.data.api.RetrofitClient
import com.example.glowmance.data.model.AnalysisHistoryItem

class AnalysisRepository {
    private val api = RetrofitClient.instance.create(AnalysisApi::class.java)

    suspend fun getHistory(token: String): Result<List<AnalysisHistoryItem>> {
        return try {
            val response = api.getHistory("Bearer $token")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
