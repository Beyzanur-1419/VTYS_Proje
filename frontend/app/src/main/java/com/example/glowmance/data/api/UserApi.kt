package com.example.glowmance.data.api

import com.example.glowmance.data.model.NotificationSettingsRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface UserApi {
    @PUT("users/settings")
    suspend fun updateSettings(
        @Header("Authorization") token: String,
        @Body settings: NotificationSettingsRequest
    ): Response<ResponseBody> // Using ResponseBody since backend returns JSON but we might not need to parse it strictly if just success 200
}
