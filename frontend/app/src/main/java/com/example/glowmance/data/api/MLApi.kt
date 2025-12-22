package com.example.glowmance.data.api

import com.example.glowmance.data.model.AnalysisResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MLApi {
    @Multipart
    @POST("analysis/analyze")
    suspend fun analyzeSkin(
        @retrofit2.http.Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<AnalysisResponse>
}
