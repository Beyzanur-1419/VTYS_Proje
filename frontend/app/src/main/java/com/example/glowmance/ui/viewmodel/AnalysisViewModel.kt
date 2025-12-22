package com.example.glowmance.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glowmance.data.UserPreferences
import com.example.glowmance.data.api.RetrofitClient
import com.example.glowmance.data.model.AnalysisResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class AnalysisViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var analysisResult by mutableStateOf<AnalysisResponse?>(null)

    fun performAnalysis(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                // 1. Get URI
                val userPrefs = UserPreferences.getInstance(context)
                val uriString = userPrefs.getLastCapturedImage()
                
                if (uriString == null) {
                    error = "Görüntü bulunamadı."
                    isLoading = false
                    return@launch
                }

                val uri = Uri.parse(uriString)
                
                // 2. Prepare File
                val file = getFileFromUri(context, uri)
                if (file == null) {
                    error = "Dosya oluşturulamadı."
                    isLoading = false
                    return@launch
                }
                
                Log.d("AnalysisViewModel", "Sending analysis request for file: ${file.name}")

                // 3. Prepare Multipart
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                // 3.5 Get Token
                val token = userPrefs.getAuthToken()
                if (token == null) {
                    error = "Oturum süresi dolmuş. Lütfen tekrar giriş yapın."
                    isLoading = false
                    return@launch
                }

                // 4. Call API
                val response = RetrofitClient.mlApi.analyzeSkin("Bearer $token", body)
                
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    Log.d("AnalysisViewModel", "Analysis success: ${result.data.result.skin_type}")
                    
                    // 5. Save Results to Preferences
                    val data = result.data.result
                    val diseaseName = data.disease?.lowercase() ?: ""
                    
                    userPrefs.saveLastSkinConditionResult(
                        hasEczema = data.hasEczema == true || diseaseName.contains("egzama"),
                        eczemaLevel = if (data.hasEczema == true || diseaseName.contains("egzama")) "Var" else "Yok",
                        hasAcne = data.hasAcne == true || diseaseName.contains("akne"),
                        acneLevel = if (data.hasAcne == true || diseaseName.contains("akne")) "Var" else "Yok",
                        hasRosacea = diseaseName.contains("rosacea") || diseaseName.contains("rozase") || diseaseName.contains("gül hastalığı"),
                        rosaceaLevel = if (diseaseName.contains("rosacea") || diseaseName.contains("rozase") || diseaseName.contains("gül hastalığı")) "Var" else "Yok",
                        isNormal = diseaseName == "healthy" || diseaseName == "normal",
                        detectedSkinType = data.skin_type ?: "Bilinmiyor",
                        detectedDisease = data.disease ?: "Belirsiz"
                    )
                    
                    userPrefs.setCompletedFirstAnalysis(true)
                    
                    // Save recommended products
                    if (result.data.products != null) {
                        userPrefs.saveRecommendedProducts(result.data.products)
                    }

                    analysisResult = result
                    onSuccess()
                } else {
                    error = "Analiz başarısız: ${response.code()} ${response.message()}"
                    Log.e("AnalysisViewModel", "API Error: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.e("AnalysisViewModel", "Exception", e)
                error = "Hata: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("analysis_image", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
