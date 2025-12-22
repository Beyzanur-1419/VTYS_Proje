package com.example.glowmance.data.model

data class AnalysisHistoryItem(
    val id: String,
    val userId: String,
    val imageUrl: String?,
    val hasEczema: Boolean,
    val eczemaLevel: String?,
    val hasAcne: Boolean,
    val acneLevel: String?,
    val hasRosacea: Boolean,
    val rosaceaLevel: String?,
    val isNormal: Boolean,
    val createdAt: String,
    val updatedAt: String?,
    val products: List<Product>? // Added for history items
)

data class AnalysisResponse(
    val success: Boolean,
    val data: AnalysisData
)

data class AnalysisData(
    val analysisId: String,
    val result: AnalysisResult,
    val products: List<com.example.glowmance.data.model.Product>
)

data class AnalysisResult(
    val skin_type: String?,
    val disease: String?,
    val hasAcne: Boolean?,
    val hasEczema: Boolean?,
    val confidence: Double?
)
