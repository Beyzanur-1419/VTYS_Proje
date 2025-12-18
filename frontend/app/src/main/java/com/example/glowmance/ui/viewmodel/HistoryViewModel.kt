package com.example.glowmance.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.glowmance.data.UserPreferences
import com.example.glowmance.data.model.AnalysisHistoryItem
import com.example.glowmance.data.repository.AnalysisRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

sealed class HistoryState {
    object Loading : HistoryState()
    data class Success(val history: List<AnalysisHistoryItem>) : HistoryState()
    data class Error(val message: String) : HistoryState()
    object Empty : HistoryState()
}

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AnalysisRepository()
    private val userPreferences = UserPreferences.getInstance(application)

    var historyState by mutableStateOf<HistoryState>(HistoryState.Loading)
        private set

    init {
        fetchHistory()
    }

    fun fetchHistory() {
        viewModelScope.launch {
            historyState = HistoryState.Loading
            
            val token = userPreferences.getAuthToken()
            if (token == null) {
                historyState = HistoryState.Error("Oturum bulunamadı, lütfen tekrar giriş yapın.")
                return@launch
            }

            val result = repository.getHistory(token)
            
            result.onSuccess { historyList ->
                if (historyList.isEmpty()) {
                    historyState = HistoryState.Empty
                } else {
                    // Sort by date descending (newest first)
                    val sortedList = historyList.sortedByDescending { it.createdAt }
                    historyState = HistoryState.Success(sortedList)
                }
            }.onFailure { e ->
                historyState = HistoryState.Error(e.message ?: "Geçmiş yüklenirken bir hata oluştu.")
            }
        }
    }

    fun calculateScore(item: AnalysisHistoryItem): Int {
        var score = 100
        
        if (item.hasAcne) score -= calculateSeverityPenalty(item.acneLevel)
        if (item.hasEczema) score -= calculateSeverityPenalty(item.eczemaLevel)
        if (item.hasRosacea) score -= calculateSeverityPenalty(item.rosaceaLevel)
        if (!item.isNormal && score == 100) score = 85 // General penalty if issues exist but leveled specific ones aren't high
        
        return score.coerceIn(0, 100)
    }

    private fun calculateSeverityPenalty(level: String?): Int {
        return when (level?.lowercase()) {
            "düşük", "low" -> 5
            "orta", "moderate", "medium" -> 10
            "yüksek", "high" -> 20
            else -> 0
        }
    }
    
    fun formatDate(dateString: String): String {
        return try {
            // Backend format: "2025-12-17T15:52:59.916Z" (ISO 8601)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            
            // Output format: "17 Ara 2025"
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("tr", "TR"))
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
    
    fun generateSummary(item: AnalysisHistoryItem): String {
        val issues = mutableListOf<String>()
        if (item.hasAcne) issues.add("Akne")
        if (item.hasEczema) issues.add("Egzama")
        if (item.hasRosacea) issues.add("Rozase")
        
        return if (issues.isEmpty()) {
            "Normal cilt durumu"
        } else {
            "${issues.joinToString(", ")} tespit edildi"
        }
    }
}
