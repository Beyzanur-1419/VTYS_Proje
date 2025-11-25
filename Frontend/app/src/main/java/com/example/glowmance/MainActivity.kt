package com.example.glowmance

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.glowmance.ui.navigation.Screen
import com.example.glowmance.ui.screens.ForgotPasswordScreen
import com.example.glowmance.ui.screens.HomeScreen
import com.example.glowmance.ui.screens.SignInScreen
import com.example.glowmance.ui.screens.SignUpScreen
import com.example.glowmance.ui.screens.WelcomeScreen
import com.example.glowmance.ui.theme.GlowmanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlowmanceTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GlowmanceApp()
                }
            }
        }
    }
}

/**
 * Main composable function for the app's navigation
 */
@Composable
fun GlowmanceApp() {
    // Use rememberNavController to maintain navigation state
    val navController = rememberNavController()
    
    // Define NavHost with our navigation graph
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        // Welcome screen destination
        composable(route = Screen.Welcome.route) {
            WelcomeScreen(
                onSignInClick = {
                    Log.d("Navigation", "Navigating to SignIn screen")
                    navController.navigate(Screen.SignIn.route) {
                        // Use launchSingleTop to avoid multiple copies of the same destination
                        launchSingleTop = true
                    }
                },
                onLogInClick = {
                    Log.d("Navigation", "Sign Up button clicked from Welcome screen")
                    navController.navigate(Screen.SignUp.route) {
                        // Use launchSingleTop to avoid multiple copies of the same destination
                        launchSingleTop = true
                    }
                }
            )
        }
        
        // Sign In screen destination
        composable(route = Screen.SignIn.route) {
            SignInScreen(
                onSignInClick = {
                    Log.d("Navigation", "Sign In button clicked on SignIn screen")
                    // Navigate to Home screen after successful sign in
                    // Navigate to Home screen and clear back stack
                    navController.navigate(Screen.Home.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                            inclusive = false
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                onForgotPasswordClick = {
                    Log.d("Navigation", "Forgot Password clicked")
                    navController.navigate(Screen.ForgotPassword.route) {
                        // Use launchSingleTop to avoid multiple copies of the same destination
                        launchSingleTop = true
                    }
                },
                onSignUpClick = {
                    Log.d("Navigation", "Sign Up clicked from SignIn screen")
                    navController.navigate(Screen.SignUp.route) {
                        // Use launchSingleTop to avoid multiple copies of the same destination
                        launchSingleTop = true
                    }
                }
            )
        }
        
        // Sign Up screen destination
        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                onSignUpClick = {
                    Log.d("Navigation", "Sign Up button clicked on SignUp screen")
                    // Navigate to Home screen after successful sign up
                    navController.navigate(Screen.Home.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                            inclusive = false
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onSignInClick = {
                    Log.d("Navigation", "Sign In clicked from SignUp screen")
                    navController.navigate(Screen.SignIn.route) {
                        // Just pop the current screen off the back stack
                        popUpTo(Screen.SignUp.route) { 
                            inclusive = true 
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        // Home screen destination
        composable(route = Screen.Home.route) {
            HomeScreen(
                onAnalysisClick = {
                    Log.d("Navigation", "Skin Analysis button clicked")
                    // Will handle skin analysis later
                },
                onNavigateToProfile = {
                    Log.d("Navigation", "Navigate to Profile clicked")
                    // Will implement profile navigation later
                },
                onNavigateToHistory = {
                    Log.d("Navigation", "Navigate to History clicked")
                    // Will implement history navigation later
                },
                onNavigateToShop = {
                    Log.d("Navigation", "Navigate to Shop clicked")
                    // Will implement shop navigation later
                },
                onNavigateToHome = {
                    Log.d("Navigation", "Navigate to Home clicked")
                    // Already on home screen
                }
            )
        }
        
        // Forgot Password screen destination
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClick = {
                    Log.d("Navigation", "Back button clicked on Forgot Password screen")
                    navController.popBackStack()
                },
                onResetPasswordClick = { email ->
                    Log.d("Navigation", "Reset Password button clicked with email: $email")
                    // In a real app, this would trigger the password reset process
                    // For now, we'll just show the success message in the UI
                }
            )
        }
    }
}