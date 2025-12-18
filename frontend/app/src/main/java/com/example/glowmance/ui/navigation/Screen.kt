package com.example.glowmance.ui.navigation

sealed class Screen(val route: String) {
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object History : Screen("history")
    object Shop : Screen("shop")
    object SkinResult : Screen("skin_result")
    object Camera : Screen("camera")
    object FaceScanning : Screen("face_scanning")
    object ProductRecommendations : Screen("product_recommendations")
    object Settings : Screen("settings")
    object Notifications : Screen("notifications")
    object Help : Screen("help")
}

