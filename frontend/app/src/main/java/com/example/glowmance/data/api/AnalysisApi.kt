package com.example.glowmance.data.api

import com.example.glowmance.data.model.AnalysisHistoryItem
import retrofit2.http.GET
import retrofit2.http.Header

interface AnalysisApi {
    @GET("analysis/history")
    suspend fun getHistory(@Header("Authorization") token: String): List<AnalysisHistoryItem>
}
