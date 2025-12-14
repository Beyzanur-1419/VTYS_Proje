package com.example.glowmance.data.network

import android.content.Context
import com.example.glowmance.data.UserPreferences

/**
 * Token provider for network requests
 * UserPreferences'ten token alır
 */
object TokenProvider {
    private var userPreferences: UserPreferences? = null
    
    fun initialize(context: Context) {
        userPreferences = UserPreferences.getInstance(context)
    }
    
    fun getToken(): String? {
        return userPreferences?.getAccessToken()
    }
    
    fun getRefreshToken(): String? {
        return userPreferences?.getRefreshToken()
    }
}

