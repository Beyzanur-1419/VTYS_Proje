package com.example.glowmance.data.repository

import com.example.glowmance.data.api.AnalysisApi
import com.example.glowmance.data.api.NetworkResult
import com.example.glowmance.data.api.RetrofitClient
import com.example.glowmance.data.model.AnalysisHistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException

class AnalysisRepository {
    // Correct way to get the API instance
    private val api: AnalysisApi = RetrofitClient.analysisApi

    fun getHistory(token: String): Flow<NetworkResult<List<AnalysisHistoryItem>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getHistory("Bearer $token")
            emit(NetworkResult.Success(response))
        } catch (e: HttpException) {
             // Handle HTTP errors (4xx, 5xx)
             val message = when(e.code()) {
                 401 -> "Oturum süresi doldu"
                 403 -> "Erişim reddedildi"
                 404 -> "Sunucu bulunamadı"
                 500 -> "Sunucu hatası"
                 else -> "Bir hata oluştu: ${e.code()}"
             }
             emit(NetworkResult.Error(message))
        } catch (e: IOException) {
            // Handle Network errors (No internet, timeout)
            emit(NetworkResult.Error("İnternet bağlantınızı kontrol edin"))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Beklenmeyen bir hata: ${e.localizedMessage}"))
        }
    }.flowOn(Dispatchers.IO)
}
