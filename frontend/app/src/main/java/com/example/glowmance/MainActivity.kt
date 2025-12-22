package com.example.glowmance

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.glowmance.data.UserPreferences
import com.example.glowmance.ui.navigation.Screen
import com.example.glowmance.ui.screens.CameraScreen
import com.example.glowmance.ui.screens.FaceScanningScreen
import com.example.glowmance.ui.screens.HistoryScreen
import com.example.glowmance.ui.screens.HomeScreen
import com.example.glowmance.ui.screens.ProductRecommendationsScreen
import com.example.glowmance.ui.screens.ProfileScreen
import com.example.glowmance.ui.screens.SignInScreen
import com.example.glowmance.ui.screens.SignUpScreen
import com.example.glowmance.ui.screens.SkinResultScreen
import com.example.glowmance.ui.screens.SkinConditionResult
import com.example.glowmance.ui.theme.GlowmanceTheme
import com.example.glowmance.ui.viewmodel.AuthViewModel
import com.example.glowmance.ui.viewmodel.AuthState
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlowmanceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: android.view.MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}

// Define gradient brushes for MainActivity
private val RoseGold = Color(0xFFBD8C7D)
private val RoseGoldLight = Color(0xFFE0C1B3)
private val RoseGoldDark = Color(0xFF9A6959)
private val RoseGoldShimmer1 = Color(0xFFE0C1B3)
private val RoseGoldShimmer2 = Color(0xFFD4A599)
private val RoseGoldShimmer3 = Color(0xFFBD8C7D)
private val RoseGoldShimmer4 = Color(0xFFC9917F)
private val RoseGoldShimmer5 = Color(0xFF9A6959)

// Using system fonts temporarily
private val RalewayFontFamily = FontFamily.SansSerif
private val LoveloFontFamily = FontFamily.Serif

private val roseGoldGradient = Brush.linearGradient(
    colors = listOf(RoseGoldLight, RoseGold, RoseGoldDark),
    start = Offset(0f, 0f),
    end = Offset(100f, 100f)
)

private val roseGoldShimmerGradient = Brush.linearGradient(
    colors = listOf(
        RoseGoldShimmer1,
        RoseGoldShimmer2,
        RoseGoldShimmer3,
        RoseGoldShimmer4,
        RoseGoldShimmer5,
        RoseGoldShimmer4,
        RoseGoldShimmer3,
        RoseGoldShimmer2
    )
)

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences.getInstance(context) }
    
    // Auth ViewModel
    val authViewModel: AuthViewModel = viewModel()
    
    // Debug ve Navigasyon
    val navigateWithLog: (String) -> Unit = { route ->
        Log.d("Navigation", "Navigating to: $route")
        navController.navigate(route)
    }
    
    // Başlangıç destinasyonu
    val startDestination = remember {
        val hasToken = userPreferences.getAuthToken() != null
        val hasCompletedAnalysis = userPreferences.hasCompletedFirstAnalysis()
        
        if (hasToken) {
             if (hasCompletedAnalysis) {
                 Screen.SkinResult.route
             } else {
                 Screen.Home.route 
             }
        } else {
            Screen.SignIn.route
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Giriş ekranları
        composable(route = Screen.SignIn.route) {
            val loginState = authViewModel.loginState
            val errorMessage = authViewModel.errorMessage
            
            // Başarılı giriş durumunda yönlendirme
            LaunchedEffect(loginState) {
                if (loginState is AuthState.Success) {
                    val user = (loginState as AuthState.Success).user
                    val token = (loginState as AuthState.Success).token
                    
                    Log.d("Auth", "Login Successful: ${user?.email}")
                    if (user != null && token != null) {
                         userPreferences.saveUser(user.id, user.name, user.email)
                         userPreferences.saveAuthToken(token)
                    }

                    authViewModel.resetState() 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            }
            
            SignInScreen(
                onSignInClick = { email, password ->
                    Log.d("Auth", "Login attempt: $email")
                    authViewModel.login(email, password)
                },
                onForgotPasswordClick = { 
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onSignUpClick = { 
                    authViewModel.resetState()
                    navController.navigate(Screen.SignUp.route)
                },
                isLoading = loginState is AuthState.Loading,
                errorMessage = errorMessage
            )
        }
        
        composable(route = Screen.SignUp.route) {
            val registerState = authViewModel.registerState
            val errorMessage = authViewModel.errorMessage
            
            // Başarılı kayıt durumunda yönlendirme
            LaunchedEffect(registerState) {
                if (registerState is AuthState.Success) {
                    val user = (registerState as AuthState.Success).user
                    val token = (registerState as AuthState.Success).token

                    Log.d("Auth", "Registration Successful: ${user?.email}")
                    if (user != null && token != null) {
                        userPreferences.saveUser(user.id, user.name, user.email)
                        userPreferences.saveAuthToken(token)
                    }
                    
                    authViewModel.resetState()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            }

            SignUpScreen(
                onSignUpClick = { name, email, password ->
                    Log.d("Auth", "Register attempt: $email")
                    authViewModel.register(name, email, password)
                },
                onSignInClick = { 
                    authViewModel.resetState()
                    navController.popBackStack()
                },
                isLoading = registerState is AuthState.Loading,
                errorMessage = errorMessage
            )
        }
        
        composable(route = Screen.ForgotPassword.route) {
            // Background image with space theme
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.background_image),
                    contentDescription = "Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Back button
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .clickable {
                            Log.d("Navigation", "Back button clicked")
                            navController.popBackStack()
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // Logo in top right corner
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Logo with shadow for elegance (same size as SignInScreen)
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .shadow(4.dp, RoundedCornerShape(40.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // GLOWMANCE text with rose-gold shimmer effect (same as SignInScreen)
                        Text(
                            text = "GLOWMANCE",
                            style = TextStyle(
                                fontFamily = LoveloFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                letterSpacing = 1.sp,
                                brush = roseGoldShimmerGradient
                            )
                        )
                    }
                }
                
                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    // FORGOT text
                    Text(
                        text = "FORGOT",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(bottom = 0.dp)
                    )
                    
                    // PASSWORD? text with rose gold color
                    Text(
                        text = "PASSWORD?",
                        style = TextStyle(
                            brush = roseGoldShimmerGradient, // RoseGold
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    // Instruction text
                    Text(
                        text = "Enter your email address and we'll send you instructions to reset your password.",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    
                    // Email input field
                    var email by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { 
                            Text(
                                text = "Email",
                                style = TextStyle(
                                    brush = roseGoldShimmerGradient // RoseGold
                                )
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon",
                                tint = RoseGold // RoseGold
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color(0xFFBD8C7D), // RoseGold
                            unfocusedIndicatorColor = Color(0xFFBD8C7D).copy(alpha = 0.5f), // RoseGold with alpha
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    
                    // Reset Password button
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(4.dp, RoundedCornerShape(28.dp))
                            .clip(RoundedCornerShape(28.dp))
                            .background(brush =  roseGoldGradient) // RoseGold
                            .clickable {
                                // Şifre sıfırlama işlemi
                                Log.d("Navigation", "Reset Password clicked")
                                navController.popBackStack()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "RESET PASSWORD",
                            style = TextStyle(
                                fontFamily = RalewayFontFamily,
                                fontSize = 18.sp,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
        
        
                
                // Ana ekranlar
        composable(route = Screen.Home.route) {
            val userName = userPreferences.getUserName() ?: "Kullanıcı"
            
            HomeScreen(
                userName = userName,
                onAnalysisClick = { 
                    Log.d("HomeScreen", "Cilt Analizini Başlat butonuna tıklandı, kamera açılıyor...")
                    navigateWithLog(Screen.Camera.route) 
                },
                onNavigateToProfile = { navigateWithLog(Screen.Profile.route) },
                onNavigateToHistory = { navigateWithLog(Screen.History.route) },
                onNavigateToShop = { navigateWithLog(Screen.ProductRecommendations.route) },
                onNavigateToHome = { navigateWithLog(Screen.Home.route) },
                onSettingsClick = { navigateWithLog(Screen.Profile.route) }, // Ayarlar için şimdilik Profile yönlendir
                onLogoutClick = {
                    userPreferences.clearAuth()
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onNotificationClick = { notification ->
                    Log.d("Notification", "Clicked: ${notification.title}")
                    when {
                        notification.title.contains("Analiz", ignoreCase = true) -> {
                            navigateWithLog(Screen.SkinResult.route)
                        }
                        notification.title.contains("Ürün", ignoreCase = true) -> {
                            navigateWithLog(Screen.ProductRecommendations.route)
                        }
                        else -> {
                            // Diğer bildirimler için varsayılan davranış (örneğin detaya git veya hiçbir şey yapma)
                            Log.d("Notification", "No specific navigation for this notification")
                        }
                    }
                }
            )
        }
        
        // Camera screen
        composable(route = Screen.Camera.route) {
            // Get viewmodel to access shared processing logic if needed, or handle here
             val cameraViewModel: com.example.glowmance.ui.screens.CameraViewModel = viewModel()
            
            CameraScreen(
                onImageCaptured = { uri ->
                    Log.d("Navigation", "Image captured: $uri")
                    // Process image using ViewModel
                    cameraViewModel.processCapturedImage(
                        context = context,
                        uri = uri,
                        onSuccess = {
                             navController.navigate(Screen.FaceScanning.route) {
                                popUpTo(Screen.Camera.route) { inclusive = true }
                            }
                        },
                        onFailure = { error ->
                            android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_LONG).show()
                        }
                    )
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Face scanning screen
        composable(route = Screen.FaceScanning.route) {
            // Analiz tamamlandığında, kullanıcının ilk analizi tamamlandı olarak işaretle
            LaunchedEffect(Unit) {
                userPreferences.setCompletedFirstAnalysis(true)
                
                // Örnek analiz sonuçları kaydet (gerçekte bu kamera analizinden gelecek)
                userPreferences.saveLastSkinConditionResult(
                    hasEczema = true,
                    eczemaLevel = "Egzama – Tahriş Var",
                    hasAcne = true,
                    acneLevel = "Akne – Orta Seviye",
                    hasRosacea = false,
                    rosaceaLevel = "Rozase – Yok",
                    isNormal = false,
                    detectedSkinType = "Karma",
                    detectedDisease = "Akne"
                )
            }
            
            FaceScanningScreen(navController = navController)
        }
        
        composable(route = Screen.SkinResult.route) {
            // Kullanıcının son cilt analiz sonuçlarını getir
            val lastAnalysisResult = userPreferences.getLastSkinConditionResult()
            
            // Eğer kayıtlı bir analiz sonucu yoksa varsayılan değerler kullan
            val skinConditionResult = SkinConditionResult(
                hasEczema = lastAnalysisResult.hasEczema,
                eczemaLevel = lastAnalysisResult.eczemaLevel,
                hasAcne = lastAnalysisResult.hasAcne,
                acneLevel = lastAnalysisResult.acneLevel,
                hasRosacea = lastAnalysisResult.hasRosacea,
                rosaceaLevel = lastAnalysisResult.rosaceaLevel,
                isNormal = lastAnalysisResult.isNormal,
                detectedSkinType = lastAnalysisResult.detectedSkinType,
                detectedDisease = lastAnalysisResult.detectedDisease
            )
            
            // Get actual user name
            val currentUserName = remember { userPreferences.getUserName() ?: "Değerli Üyemiz" }
            
            // Get recommended products
            val savedProducts = remember { userPreferences.getRecommendedProducts() }
            val uiProducts = remember(savedProducts) {
                savedProducts.map { product ->
                    com.example.glowmance.ui.screens.ProductUI(
                        name = product.name,
                        brand = product.brand,
                        imageUrl = product.imageUrl ?: "",
                        price = if (product.price != null) "${product.price} ${product.currency ?: "TL"}" else ""
                    )
                }
            }

            SkinResultScreen(
                userName = currentUserName,
                skinConditionResult = skinConditionResult,
                recommendedProducts = uiProducts,
                onNewAnalysisClick = { navigateWithLog(Screen.Camera.route) },
                onRecommendedProductsClick = { navigateWithLog(Screen.ProductRecommendations.route) },
                onNavigateToProfile = { navigateWithLog(Screen.Profile.route) },
                onNavigateToHistory = { navigateWithLog(Screen.History.route) },
                onNavigateToShop = { navigateWithLog(Screen.ProductRecommendations.route) },
                onNavigateToHome = { navigateWithLog(Screen.Home.route) }
            )
        }
        
        // Diğer ekranlar için composable tanımlamaları
        composable(route = Screen.Profile.route) {
            // State for profile details
            var profileDetails by remember { mutableStateOf(userPreferences.getProfileDetails()) }
            var currentUserName by remember { mutableStateOf(userPreferences.getUserName() ?: "Kullanıcı") }
            var analysisCount by remember { mutableStateOf(0) }
            
            // Fetch analysis count
            LaunchedEffect(Unit) {
                val token = userPreferences.getAuthToken()
                if (token != null) {
                    try {
                        val repository = com.example.glowmance.data.repository.AnalysisRepository()
                        repository.getHistory(token).collect { result ->
                            if (result is com.example.glowmance.data.api.NetworkResult.Success) {
                                analysisCount = result.data?.size ?: 0
                            }
                        }
                    } catch (e: Exception) {
                        // Ignore error for count, default 0
                    }
                }
            }

            ProfileScreen(
                userName = currentUserName,
                skinType = profileDetails.skinType,
                skinGoal = profileDetails.skinGoal,
                age = profileDetails.age,
                analysisCount = analysisCount,
                onNavigateToProfile = { navigateWithLog(Screen.Profile.route) },
                onNavigateToHistory = { navigateWithLog(Screen.History.route) },
                onNavigateToShop = { navigateWithLog(Screen.ProductRecommendations.route) },
                onNavigateToHome = { navigateWithLog(Screen.Home.route) },
                onUpdateProfile = { name, type, goal, age ->
                    userPreferences.saveProfileDetails(type, goal, age)
                    userPreferences.updateUserName(name)
                    profileDetails = com.example.glowmance.data.ProfileDetails(type, goal, age)
                    currentUserName = name
                    android.widget.Toast.makeText(context, "Profil güncellendi", android.widget.Toast.LENGTH_SHORT).show()
                },
                onSettings = { navigateWithLog(Screen.Settings.route) },
                onNotifications = { navigateWithLog(Screen.Notifications.route) },
                onHelp = { navigateWithLog(Screen.Help.route) },
                onLogout = { 
                    userPreferences.clearAuth()
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        // Settings Screen
        composable(route = Screen.Settings.route) {
            com.example.glowmance.ui.screens.AccountSettingsScreen(
                onBackClick = { navController.popBackStack() },
                onLogout = {
                    userPreferences.clearAuth()
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        // Notifications Screen
        composable(route = Screen.Notifications.route) {
            var settings by remember { mutableStateOf(userPreferences.getNotificationSettings()) }
            
            com.example.glowmance.ui.screens.NotificationsScreen(
                currentSettings = settings,
                onBackClick = { navController.popBackStack() },
                onSaveSettings = { analysis, campaigns, tips ->
                    userPreferences.saveNotificationSettings(analysis, campaigns, tips)
                    settings = com.example.glowmance.data.NotificationSettings(analysis, campaigns, tips)
                }
            )
        }

        // Help Screen
        composable(route = Screen.Help.route) {
            com.example.glowmance.ui.screens.HelpSupportScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(route = Screen.History.route) {
            HistoryScreen(
                onNavigateToProfile = { navigateWithLog(Screen.Profile.route) },
                onNavigateToHistory = { navigateWithLog(Screen.History.route) },
                onNavigateToShop = { navigateWithLog(Screen.ProductRecommendations.route) },
                onNavigateToHome = { navigateWithLog(Screen.Home.route) },
                onNavigateToLogin = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onAnalysisItemClick = { historyItem -> 
                    // Save the clicked history item as the "current" result to view details
                    userPreferences.saveLastSkinConditionResult(
                        hasEczema = historyItem.hasEczema,
                        eczemaLevel = historyItem.eczemaLevel ?: "Yok",
                        hasAcne = historyItem.hasAcne,
                        acneLevel = historyItem.acneLevel ?: "Yok",
                        hasRosacea = historyItem.hasRosacea,
                        rosaceaLevel = historyItem.rosaceaLevel ?: "Yok",
                        isNormal = historyItem.isNormal,
                        detectedSkinType = "Bilinmiyor (Geçmiş)",
                        detectedDisease = when {
                            historyItem.hasAcne -> "Akne"
                            historyItem.hasEczema -> "Egzama"
                            historyItem.hasRosacea -> "Gül Hastalığı"
                            historyItem.isNormal -> "Normal"
                            else -> "Belirsiz"
                        }
                    )
                    
                    // Navigate to SkinResult screen
                    navigateWithLog(Screen.SkinResult.route)
                }
            )
        }
        
        composable(route = Screen.Shop.route) {
            // Shop ekranı artık ProductRecommendations ekranına yönlendiriyor
            navigateWithLog(Screen.ProductRecommendations.route)
        }
        
        composable(route = Screen.ProductRecommendations.route) {
            ProductRecommendationsScreen(
                onNavigateToProfile = { navigateWithLog(Screen.Profile.route) },
                onNavigateToHistory = { navigateWithLog(Screen.History.route) },
                onNavigateToShop = { navigateWithLog(Screen.ProductRecommendations.route) },
                onNavigateToHome = { navigateWithLog(Screen.Home.route) },
                onNavigateToLogin = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    GlowmanceTheme {
        AppNavigation()
    }
}