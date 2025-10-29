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
import com.example.glowmance.ui.navigation.Screen
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

@Composable
fun GlowmanceApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onSignInClick = {
                    Log.d("Navigation", "Navigating to SignIn screen")
                    navController.navigate(Screen.SignIn.route)
                },
                onLogInClick = {
                    Log.d("Navigation", "Sign Up button clicked from Welcome screen")
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }
        
        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInClick = {
                    Log.d("Navigation", "Sign In button clicked on SignIn screen")
                    // Navigate to Home screen after successful sign in
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    Log.d("Navigation", "Forgot Password clicked")
                    // Will be implemented later
                },
                onSignUpClick = {
                    Log.d("Navigation", "Sign Up clicked from SignIn screen")
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpClick = {
                    Log.d("Navigation", "Sign Up button clicked on SignUp screen")
                    // Navigate to Home screen after successful sign up
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onSignInClick = {
                    Log.d("Navigation", "Sign In clicked from SignUp screen")
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
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
    }
}