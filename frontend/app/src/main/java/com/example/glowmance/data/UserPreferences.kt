package com.example.glowmance.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Kullanıcı tercihlerini yönetmek için yardımcı sınıf
 */
class UserPreferences(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE
    )
    
    /**
     * Kullanıcının ilk cilt analizini tamamlayıp tamamlamadığını kontrol eder
     */
    fun hasCompletedFirstAnalysis(): Boolean {
        return sharedPreferences.getBoolean(KEY_COMPLETED_FIRST_ANALYSIS, false)
    }
    
    /**
     * Kullanıcının ilk cilt analizini tamamladığını kaydeder
     */
    fun setCompletedFirstAnalysis(completed: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_COMPLETED_FIRST_ANALYSIS, completed)
        }
    }
    
    /**
     * Son cilt analiz sonuçlarını kaydeder
     */
    fun saveLastSkinConditionResult(
        hasEczema: Boolean,
        eczemaLevel: String,
        hasAcne: Boolean,
        acneLevel: String,
        hasRosacea: Boolean,
        rosaceaLevel: String,
        isNormal: Boolean
    ) {
        sharedPreferences.edit {
            putBoolean(KEY_HAS_ECZEMA, hasEczema)
            putString(KEY_ECZEMA_LEVEL, eczemaLevel)
            putBoolean(KEY_HAS_ACNE, hasAcne)
            putString(KEY_ACNE_LEVEL, acneLevel)
            putBoolean(KEY_HAS_ROSACEA, hasRosacea)
            putString(KEY_ROSACEA_LEVEL, rosaceaLevel)
            putBoolean(KEY_IS_NORMAL, isNormal)
        }
    }
    
    /**
     * Kaydedilmiş son cilt analiz sonuçlarını getirir
     */
    fun getLastSkinConditionResult(): SkinAnalysisResult {
        return SkinAnalysisResult(
            hasEczema = sharedPreferences.getBoolean(KEY_HAS_ECZEMA, false),
            eczemaLevel = sharedPreferences.getString(KEY_ECZEMA_LEVEL, "Yok") ?: "Yok",
            hasAcne = sharedPreferences.getBoolean(KEY_HAS_ACNE, false),
            acneLevel = sharedPreferences.getString(KEY_ACNE_LEVEL, "Yok") ?: "Yok",
            hasRosacea = sharedPreferences.getBoolean(KEY_HAS_ROSACEA, false),
            rosaceaLevel = sharedPreferences.getString(KEY_ROSACEA_LEVEL, "Yok") ?: "Yok",
            isNormal = sharedPreferences.getBoolean(KEY_IS_NORMAL, true)
        )
    }
    
    /**
     * Access token'ı kaydeder
     */
    fun saveAccessToken(token: String) {
        sharedPreferences.edit {
            putString(KEY_ACCESS_TOKEN, token)
        }
    }
    
    /**
     * Access token'ı getirir
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }
    
    /**
     * Refresh token'ı kaydeder
     */
    fun saveRefreshToken(token: String) {
        sharedPreferences.edit {
            putString(KEY_REFRESH_TOKEN, token)
        }
    }
    
    /**
     * Refresh token'ı getirir
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }
    
    /**
     * Tüm token'ları siler (logout)
     */
    fun clearTokens() {
        sharedPreferences.edit {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
        }
    }
    
    /**
     * Kullanıcı adını kaydeder
     */
    fun saveUserName(name: String) {
        sharedPreferences.edit {
            putString(KEY_USER_NAME, name)
        }
    }
    
    /**
     * Kullanıcı adını getirir
     */
    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }
    
    /**
     * Kullanıcı email'ini kaydeder
     */
    fun saveUserEmail(email: String) {
        sharedPreferences.edit {
            putString(KEY_USER_EMAIL, email)
        }
    }
    
    /**
     * Kullanıcı email'ini getirir
     */
    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }
    
    /**
     * Kullanıcı ID'sini kaydeder
     */
    fun saveUserId(userId: String) {
        sharedPreferences.edit {
            putString(KEY_USER_ID, userId)
        }
    }
    
    /**
     * Kullanıcı ID'sini getirir
     */
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
    
    /**
     * Tüm kullanıcı bilgilerini temizler (logout)
     */
    fun clearUserData() {
        sharedPreferences.edit {
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_ID)
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
        }
    }
    
    companion object {
        private const val PREFERENCES_NAME = "glowmance_preferences"
        private const val KEY_COMPLETED_FIRST_ANALYSIS = "completed_first_analysis"
        private const val KEY_HAS_ECZEMA = "has_eczema"
        private const val KEY_ECZEMA_LEVEL = "eczema_level"
        private const val KEY_HAS_ACNE = "has_acne"
        private const val KEY_ACNE_LEVEL = "acne_level"
        private const val KEY_HAS_ROSACEA = "has_rosacea"
        private const val KEY_ROSACEA_LEVEL = "rosacea_level"
        private const val KEY_IS_NORMAL = "is_normal"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ID = "user_id"
        
        // Singleton instance
        @Volatile
        private var INSTANCE: UserPreferences? = null
        
        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

/**
 * Cilt analiz sonuçlarını temsil eden veri sınıfı
 */
data class SkinAnalysisResult(
    val hasEczema: Boolean = false,
    val eczemaLevel: String = "Yok",
    val hasAcne: Boolean = false,
    val acneLevel: String = "Yok",
    val hasRosacea: Boolean = false,
    val rosaceaLevel: String = "Yok",
    val isNormal: Boolean = true
)
