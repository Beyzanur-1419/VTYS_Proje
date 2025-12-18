package com.example.glowmance.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: String, // UUID from backend
    @SerializedName(value = "title", alternate = ["name"]) val name: String, // Mapped 'title' or 'name' for consistency
    @SerializedName("brand") val brand: String,
    @SerializedName("price") val price: Double?, // Changed to Double based on JSON response (289.99)
    @SerializedName("currency") val currency: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("ingredients") val ingredients: List<String>?, // JSON array -> List
    @SerializedName("image_url") val imageUrl: String?
)

data class ProductResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("data") val data: List<Product>
)

data class AdvancedRecommendationRequest(
    val skinType: String?,
    val problems: List<String>?,
    val sensitivity: String?,
    val acne: String?,
    val careLevel: String?
)
