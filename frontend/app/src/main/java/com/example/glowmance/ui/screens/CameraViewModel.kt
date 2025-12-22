package com.example.glowmance.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class CameraViewModel : ViewModel() {
    
    // UI state
    var hasCameraPermission by mutableStateOf(false)
    var hasGalleryPermission by mutableStateOf(false)
    var faceDetected by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var showCameraPreview by mutableStateOf(false)
    
    // Face detector
    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.2f)
        .enableTracking()
        .build()
        
    private val faceDetector = FaceDetection.getClient(faceDetectorOptions)
    
    // Analyze image from camera or gallery
    fun analyzeImage(image: InputImage, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        isLoading = true
        errorMessage = null
        
        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.size > 0) {
                    Log.d("CameraViewModel", "Face detected! Count: ${faces.size}")
                    faceDetected = true
                    onSuccess()
                } else {
                    Log.d("CameraViewModel", "No full face detected, but proceeding anyway (User Request)")
                    // BYPASS: Allow partial faces or objects
                    faceDetected = true 
                    onSuccess()
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                Log.e("CameraViewModel", "Face detection failed", e)
                onFailure("Yüz analizi sırasında bir hata oluştu: ${e.localizedMessage}")
                isLoading = false
            }
    }
    
    // Process image from gallery
    fun processGalleryImage(context: Context, uri: Uri, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        isLoading = true
        try {
            Log.d("CameraViewModel", "Processing gallery image: $uri")
            val image = InputImage.fromFilePath(context, uri)
            analyzeImage(image, onSuccess = {
                // Save URI to preferences for the next screen
                com.example.glowmance.data.UserPreferences.getInstance(context).saveLastCapturedImage(uri.toString())
                onSuccess()
            }, onFailure)
        } catch (e: Exception) {
            Log.e("CameraViewModel", "Error processing gallery image", e)
            isLoading = false
            onFailure("Resim işlenirken bir hata oluştu: ${e.localizedMessage}")
        }
    }

    // Process image captured from camera
    fun processCapturedImage(context: Context, uri: Uri, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        isLoading = true
        try {
            Log.d("CameraViewModel", "Processing captured image: $uri")
            val image = InputImage.fromFilePath(context, uri)
            analyzeImage(image, onSuccess = {
                // Save URI to preferences
                com.example.glowmance.data.UserPreferences.getInstance(context).saveLastCapturedImage(uri.toString())
                onSuccess()
            }, onFailure)
        } catch (e: Exception) {
            Log.e("CameraViewModel", "Error processing captured image", e)
            isLoading = false
            onFailure("Fotoğraf işlenirken hata oluştu: ${e.localizedMessage}")
        }
    }
    
    // Reset state
    fun resetState() {
        faceDetected = false
        errorMessage = null
        showCameraPreview = false
    }
    
    override fun onCleared() {
        super.onCleared()
        faceDetector.close()
    }
}
