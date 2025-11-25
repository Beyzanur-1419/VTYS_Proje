package com.example.glowmance.ui.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up")
    object Home : Screen("home")
    object ForgotPassword : Screen("forgot_password")
}