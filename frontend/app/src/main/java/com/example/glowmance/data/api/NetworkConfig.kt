package com.example.glowmance.data.api

object NetworkConfig {
    // Change this URL when using Ngrok or a different local IP
    // For Emulator: "http://10.0.2.2:3000/"
    // For Physical Device: "http://YOUR_PC_IP:3000/" or Ngrok URL
    // Reverting to Local IP as Tunneling    // Beyza's Computer IP
    const val BASE_URL = "http://192.168.137.1:3000/api/v1/"
    const val IMAGE_BASE_URL = "http://192.168.137.1:3000"

    const val TIMEOUT_SECONDS = 120L // Increased for ML inference

    // Helper to fix image URLs that might be missing the host
    fun getFullImageUrl(partialUrl: String?): String {
        if (partialUrl.isNullOrEmpty()) return ""
        if (partialUrl.startsWith("http")) return partialUrl
        // Ensure no double slash if partial starts with /
        val cleanBase = IMAGE_BASE_URL.removeSuffix("/")
        val cleanPath = if (partialUrl.startsWith("/")) partialUrl else "/$partialUrl"
        return "$cleanBase$cleanPath"
    }
}
