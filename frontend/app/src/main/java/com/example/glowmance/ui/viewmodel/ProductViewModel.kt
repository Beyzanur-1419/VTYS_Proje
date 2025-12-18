package com.example.glowmance.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glowmance.data.model.Product
import com.example.glowmance.data.repository.ProductRepository
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel

sealed class ProductState {
    object Loading : ProductState()
    data class Success(val products: List<Product>) : ProductState()
    data class Error(val message: String) : ProductState()
    object Empty : ProductState()
}



class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProductRepository()
    private val userPreferences = com.example.glowmance.data.UserPreferences.getInstance(application)
    
    var productState by mutableStateOf<ProductState>(ProductState.Loading)
        private set
        
    var userName by mutableStateOf(userPreferences.getUserName() ?: "Kullanıcı")
        private set

    init {
        // Fetch recommendations based on user profile
        fetchAdvancedRecommendations()
    }

    fun fetchAdvancedRecommendations() {
        viewModelScope.launch {
            productState = ProductState.Loading
            
            // Get profile details from preferences
            val profile = userPreferences.getProfileDetails()
            // Using last analysis result for problems (mock logic mapping)
            val lastAnalysis = userPreferences.getLastSkinConditionResult()
            
            val problems = mutableListOf<String>()
            if (lastAnalysis.hasAcne) problems.add("akne")
            if (lastAnalysis.hasEczema) problems.add("egzama")
            if (lastAnalysis.hasRosacea) problems.add("rozase")
            
            val request = com.example.glowmance.data.model.AdvancedRecommendationRequest(
                skinType = profile.skinType.takeIf { it.isNotEmpty() } ?: "Normal",
                problems = problems,
                sensitivity = if (lastAnalysis.hasEczema || lastAnalysis.hasRosacea) "Yüksek" else "Normal",
                acne = if (lastAnalysis.hasAcne) "Var" else "Yok",
                careLevel = "Orta" // Default or fetched if available
            )
            
            val result = repository.getAdvancedRecommendations(request)
            
            result.onSuccess { products ->
                if (products.isEmpty()) {
                    productState = ProductState.Empty
                } else {
                    productState = ProductState.Success(products)
                }
            }.onFailure { e ->
                productState = ProductState.Error(e.message ?: "Ürünler yüklenirken hata oluştu.")
            }
        }
    }
}
