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
     * Kullanıcının token bilgisini kaydeder
     */
    fun saveAuthToken(token: String) {
        sharedPreferences.edit {
            putString(KEY_AUTH_TOKEN, token)
        }
    }

    /**
     * Kayıtlı token bilgisini getirir
     */
    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Kullanıcı bilgilerini kaydeder
     */
    /**
     * Kullanıcı bilgilerini kaydeder
     */
    fun saveUser(id: String, name: String, email: String) {
        sharedPreferences.edit {
            putString(KEY_USER_ID, id)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
        }
    }

    /**
     * Sadece kullanıcı adını günceller
     */
    fun updateUserName(name: String) {
        sharedPreferences.edit {
            putString(KEY_USER_NAME, name)
        }
    }

    /**
     * Kayıtlı kullanıcı adını getirir
     */
    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    /**
     * Oturumu kapatır (Token ve kullanıcı bilgilerini siler)
     */
    fun clearAuth() {
        sharedPreferences.edit {
            remove(KEY_AUTH_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            // Analiz sonucunu silmek isteyip istemediğimize karar verelim
            // Genelde çıkış yapınca her şey silinir:
            remove(KEY_COMPLETED_FIRST_ANALYSIS)
        }
    }
    
    /**
     * Profil detaylarını kaydeder
     */
    fun saveProfileDetails(skinType: String, skinGoal: String, age: Int) {
        sharedPreferences.edit {
            putString(KEY_SKIN_TYPE, skinType)
            putString(KEY_SKIN_GOAL, skinGoal)
            putInt(KEY_AGE, age)
        }
    }

    /**
     * Profil detaylarını getirir (Varsayılan değerlerle)
     */
    fun getProfileDetails(): ProfileDetails {
        return ProfileDetails(
            skinType = sharedPreferences.getString(KEY_SKIN_TYPE, "Karma / Hassas") ?: "Karma / Hassas",
            skinGoal = sharedPreferences.getString(KEY_SKIN_GOAL, "Leke Karşıtı & Nem") ?: "Leke Karşıtı & Nem",
            age = sharedPreferences.getInt(KEY_AGE, 26)
        )
    }

    /**
     * Bildirim ayarlarını kaydeder
     */
    fun saveNotificationSettings(analysisReminder: Boolean, campaigns: Boolean, tips: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_NOTIF_ANALYSIS_REMINDER, analysisReminder)
            putBoolean(KEY_NOTIF_CAMPAIGNS, campaigns)
            putBoolean(KEY_NOTIF_TIPS, tips)
        }
    }

    /**
     * Bildirim ayarlarını getirir
     */
    fun getNotificationSettings(): NotificationSettings {
        return NotificationSettings(
            analysisReminder = sharedPreferences.getBoolean(KEY_NOTIF_ANALYSIS_REMINDER, true),
            campaigns = sharedPreferences.getBoolean(KEY_NOTIF_CAMPAIGNS, true),
            tips = sharedPreferences.getBoolean(KEY_NOTIF_TIPS, true)
        )
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
        
        // Auth keys
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"

        // Profile keys
        private const val KEY_SKIN_TYPE = "skin_type"
        private const val KEY_SKIN_GOAL = "skin_goal"
        private const val KEY_AGE = "age"
        
        // Notification keys
        private const val KEY_NOTIF_ANALYSIS_REMINDER = "notif_analysis_reminder"
        private const val KEY_NOTIF_CAMPAIGNS = "notif_campaigns"
        private const val KEY_NOTIF_TIPS = "notif_tips"
        
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

data class ProfileDetails(
    val skinType: String,
    val skinGoal: String,
    val age: Int
)

data class NotificationSettings(
    val analysisReminder: Boolean,
    val campaigns: Boolean,
    val tips: Boolean
)

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
