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
    val updatedAt: String?
)
