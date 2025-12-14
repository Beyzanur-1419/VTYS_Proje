package com.example.glowmance

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.glowmance.data.network.NetworkModule
import com.example.glowmance.data.network.TokenProvider
import com.example.glowmance.data.repository.AuthRepository
import com.example.glowmance.ui.navigation.Screen
import kotlinx.coroutines.launch
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // TokenProvider'ı initialize et
        TokenProvider.initialize(this)
        
        enableEdgeToEdge()
        setContent {
            GlowmanceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
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
    val authRepository = remember { AuthRepository(NetworkModule.apiService) }
    val apiService = remember { NetworkModule.apiService }
    val coroutineScope = rememberCoroutineScope()
    
    // Hata mesajı göstermek için helper function
    fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        Log.e("AppNavigation", message)
    }
    
    fun showSuccess(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        Log.d("AppNavigation", message)
    }
    
    // Kullanıcı adını al (state olarak tutuluyor, güncellenebilir)
    var userName by remember { 
        mutableStateOf(userPreferences.getUserName() ?: "Kullanıcı")
    }
    
    // Debug için navigasyon değişikliklerini logla
    val navigateWithLog: (String) -> Unit = { route ->
        Log.d("Navigation", "Navigating to: $route")
        navController.navigate(route)
    }
    
    // Başlangıç destinasyonu için kontrol
    val startDestination = remember {
        // Kullanıcı giriş yapmış ve ilk cilt analizini tamamlamışsa SkinResult ekranından başla
        // Aksi takdirde SignIn ekranından başla
        if (userPreferences.hasCompletedFirstAnalysis()) {
            Screen.SkinResult.route
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
            SignInScreen(
                onSignInClick = { email, password ->
                    Log.d("Navigation", "SignIn button clicked with email: $email")
                    if (email.isBlank() || password.isBlank()) {
                        showError("Lütfen email ve şifre girin")
                        return@SignInScreen
                    }
                    
                    coroutineScope.launch {
                        try {
                            Log.d("Auth", "Attempting login...")
                            authRepository.login(email, password)
                                .onSuccess { response ->
                                    // Token ve kullanıcı bilgilerini kaydet
                                    if (response.accessToken.isNotBlank()) {
                                        userPreferences.saveAccessToken(response.accessToken)
                                    } else {
                                        Log.e("Auth", "AccessToken is empty!")
                                        showError("Token alınamadı. Lütfen tekrar deneyin.")
                                        return@launch
                                    }
                                    
                                    response.refreshToken?.let { 
                                        if (it.isNotBlank()) {
                                            userPreferences.saveRefreshToken(it)
                                        }
                                    }
                                    userPreferences.saveUserName(response.user.name)
                                    userPreferences.saveUserEmail(response.user.email)
                                    userPreferences.saveUserId(response.user.id)
                                    
                                    // Kullanıcı adını güncelle
                                    userName = response.user.name
                                    
                                    Log.d("Auth", "Login successful: ${response.user.name}")
                                    showSuccess("Hoş geldiniz, ${response.user.name}!")
                                    
                                    // Ana ekrana git
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.SignIn.route) { inclusive = true }
                                    }
                                }
                                .onFailure { error ->
                                    val errorMessage = error.message ?: "Giriş yapılamadı. Lütfen tekrar deneyin."
                                    Log.e("Auth", "Login failed: ${error.message}", error)
                                    showError(errorMessage)
                                }
                        } catch (e: Exception) {
                            val errorMessage = "Bağlantı hatası: ${e.message ?: "Bilinmeyen hata"}"
                            Log.e("Auth", "Login exception", e)
                            showError(errorMessage)
                        }
                    }
                },
                onForgotPasswordClick = { 
                    Log.d("Navigation", "Forgot Password clicked")
                    // Şifremi Unuttum ekranına yönlendir
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onSignUpClick = { 
                    Log.d("Navigation", "Sign Up clicked")
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }
        
        composable(route = Screen.SignUp.route) {
            // Gerçek SignUpScreen'i kullan
            SignUpScreen(
                onSignUpClick = { email, password, name ->
                    Log.d("Navigation", "Sign Up button clicked with email: $email, name: $name")
                    
                    // Validasyon
                    if (name.isBlank()) {
                        showError("Lütfen kullanıcı adı girin")
                        return@SignUpScreen
                    }
                    if (email.isBlank()) {
                        showError("Lütfen email girin")
                        return@SignUpScreen
                    }
                    if (password.isBlank() || password.length < 6) {
                        showError("Şifre en az 6 karakter olmalıdır")
                        return@SignUpScreen
                    }
                    
                    coroutineScope.launch {
                        try {
                            Log.d("Auth", "Attempting registration...")
                            authRepository.register(email, password, name)
                                .onSuccess { response ->
                                    // Token ve kullanıcı bilgilerini kaydet
                                    if (response.accessToken.isNotBlank()) {
                                        userPreferences.saveAccessToken(response.accessToken)
                                    } else {
                                        Log.e("Auth", "AccessToken is empty!")
                                        showError("Token alınamadı. Lütfen tekrar deneyin.")
                                        return@launch
                                    }
                                    
                                    response.refreshToken?.let { 
                                        if (it.isNotBlank()) {
                                            userPreferences.saveRefreshToken(it)
                                        }
                                    }
                                    userPreferences.saveUserName(response.user.name)
                                    userPreferences.saveUserEmail(response.user.email)
                                    userPreferences.saveUserId(response.user.id)
                                    
                                    // Kullanıcı adını güncelle
                                    userName = response.user.name
                                    
                                    Log.d("Auth", "Registration successful: ${response.user.name}")
                                    showSuccess("Kayıt başarılı! Hoş geldiniz, ${response.user.name}!")
                                    
                                    // Kayıt başarılı olduğunda ana ekrana git
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.SignIn.route) { inclusive = true }
                                    }
                                }
                                .onFailure { error ->
                                    val errorMessage = error.message ?: "Kayıt yapılamadı. Lütfen tekrar deneyin."
                                    Log.e("Auth", "Registration failed: ${error.message}", error)
                                    showError(errorMessage)
                                }
                        } catch (e: Exception) {
                            val errorMessage = "Bağlantı hatası: ${e.message ?: "Bilinmeyen hata"}"
                            Log.e("Auth", "Registration exception", e)
                            showError(errorMessage)
                        }
                    }
                },
                onSignInClick = { 
                    Log.d("Navigation", "Back to SignIn clicked")
                    navController.popBackStack()
                }
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
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        ) {
                            Log.d("Navigation", "Back button clicked")
                            navController.popBackStack()
                        }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple()
                            ) {
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
            HomeScreen(
                userName = userName,
                onAnalysisClick = { 
                    Log.d("HomeScreen", "Cilt Analizini Başlat butonuna tıklandı, kamera açılıyor...")
                    navigateWithLog(Screen.Camera.route) 
                },
                onNavigateToProfile = { navigateWithLog(Screen.Profile.route) },
                onNavigateToHistory = { navigateWithLog(Screen.History.route) },
                onNavigateToShop = { navigateWithLog(Screen.ProductRecommendations.route) },
                onNavigateToHome = { navigateWithLog(Screen.Home.route) }
            )
        }
        
        // Camera screen
        composable(route = Screen.Camera.route) {
            CameraScreen(navController = navController)
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
                    isNormal = false
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
                isNormal = lastAnalysisResult.isNormal
            )
            
            SkinResultScreen(
                userName = userName,
                skinConditionResult = skinConditionResult,
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
            // Profil bilgilerini backend'den çek
            var profileUserName by remember { mutableStateOf(userName) }
            var skinType by remember { mutableStateOf("Karma / Hassas") }
            var skinGoal by remember { mutableStateOf("Leke Karşıtı & Nem") }
            var age by remember { mutableStateOf(26) }
            var analysisCount by remember { mutableStateOf(0) }
            var routineMatch by remember { mutableStateOf(85) }
            
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    try {
                        val response = apiService.getUserProfile()
                        if (response.isSuccessful && response.body() != null) {
                            val user = response.body()!!
                            profileUserName = user.name
                            userPreferences.saveUserName(user.name)
                            userName = user.name
                            
                            // Cilt bilgilerini güncelle
                            user.skinType?.let { skinType = it }
                            user.skinGoal?.let { skinGoal = it }
                            user.age?.let { age = it }
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileScreen", "Failed to load profile", e)
                    }
                    
                    // Analiz istatistiklerini çek
                    try {
                        val statsResponse = apiService.getAnalysisStats()
                        if (statsResponse.isSuccessful && statsResponse.body() != null) {
                            analysisCount = statsResponse.body()!!.totalAnalyses
                            // Rutin uyumu hesapla (basit bir algoritma)
                            routineMatch = when {
                                analysisCount > 20 -> 95
                                analysisCount > 10 -> 90
                                analysisCount > 5 -> 85
                                analysisCount > 0 -> 80
                                else -> 75
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileScreen", "Failed to load stats", e)
                    }
                }
            }
            
            ProfileScreen(
                userName = profileUserName,
                skinType = skinType,
                skinGoal = skinGoal,
                age = age,
                analysisCount = analysisCount,
                routineMatch = routineMatch,
                onNavigateToProfile = { navigateWithLog(Screen.Profile.route) },
                onNavigateToHistory = { navigateWithLog(Screen.History.route) },
                onNavigateToShop = { navigateWithLog(Screen.ProductRecommendations.route) },
                onNavigateToHome = { navigateWithLog(Screen.Home.route) },
                onEditProfile = { 
                    // Cilt profili güncelleme için dialog veya ekran açılabilir
                    // Şimdilik basit bir mesaj göster
                    showSuccess("Cilt profili düzenleme özelliği yakında eklenecek")
                },
                onSettings = { 
                    coroutineScope.launch {
                        try {
                            val settingsResponse = apiService.getSettings()
                            if (settingsResponse.isSuccessful && settingsResponse.body() != null) {
                                val settings = settingsResponse.body()!!
                                showSuccess("Bildirimler: ${if (settings.notificationEnabled) "Açık" else "Kapalı"}\nEmail Bildirimleri: ${if (settings.emailNotifications) "Açık" else "Kapalı"}")
                            } else {
                                showSuccess("Ayarlar yüklenemedi")
                            }
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Failed to load settings", e)
                            showError("Ayarlar yüklenemedi: ${e.message}")
                        }
                    }
                },
                onNotifications = { 
                    coroutineScope.launch {
                        try {
                            val notificationsResponse = apiService.getNotifications()
                            if (notificationsResponse.isSuccessful && notificationsResponse.body() != null) {
                                val notifications = notificationsResponse.body()!!
                                if (notifications.notifications.isEmpty()) {
                                    showSuccess("Henüz bildiriminiz yok")
                                } else {
                                    showSuccess("${notifications.unreadCount} okunmamış bildiriminiz var")
                                }
                            } else {
                                showSuccess("Bildirimler yüklenemedi")
                            }
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Failed to load notifications", e)
                            showError("Bildirimler yüklenemedi: ${e.message}")
                        }
                    }
                },
                onHelp = { 
                    showSuccess("Yardım & Destek: support@glowmance.com")
                },
                onLogout = { 
                    // Token ve kullanıcı bilgilerini temizle
                    userPreferences.clearUserData()
                    userName = "Kullanıcı"
                    showSuccess("Çıkış yapıldı")
                    // Çıkış yapıldığında giriş ekranına dön
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.History.route) {
            // Analiz geçmişini backend'den çek
            var historyList by remember { mutableStateOf<List<com.example.glowmance.data.network.AnalysisHistoryItem>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    isLoading = true
                    try {
                        val response = apiService.getAnalysisHistory()
                        if (response.isSuccessful && response.body() != null) {
                            historyList = response.body()!!
                        } else {
                            showError("Analiz geçmişi yüklenemedi")
                        }
                    } catch (e: Exception) {
                        Log.e("HistoryScreen", "Failed to load history", e)
                        showError("Bağlantı hatası: ${e.message}")
                    } finally {
                        isLoading = false
                    }
                }
            }
            
            HistoryScreen(
                userName = userName,
                historyList = historyList,
                isLoading = isLoading,
                onNavigateToProfile = { navigateWithLog(Screen.Profile.route) },
                onNavigateToHistory = { navigateWithLog(Screen.History.route) },
                onNavigateToShop = { navigateWithLog(Screen.ProductRecommendations.route) },
                onNavigateToHome = { navigateWithLog(Screen.Home.route) },
                onAnalysisItemClick = { historyItem -> 
                    // Geçmiş analiz detayına tıklandığında SkinResult ekranına git
                    // Analiz sonuçlarını kaydet
                    val result = historyItem.skinConditionResult
                    userPreferences.saveLastSkinConditionResult(
                        hasEczema = result.hasEczema,
                        eczemaLevel = result.eczemaLevel,
                        hasAcne = result.hasAcne,
                        acneLevel = result.acneLevel,
                        hasRosacea = result.hasRosacea,
                        rosaceaLevel = result.rosaceaLevel,
                        isNormal = result.isNormal
                    )
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
                userName = userName,
                onNavigateToProfile = { navigateWithLog(Screen.Profile.route) },
                onNavigateToHistory = { navigateWithLog(Screen.History.route) },
                onNavigateToShop = { navigateWithLog(Screen.ProductRecommendations.route) },
                onNavigateToHome = { navigateWithLog(Screen.Home.route) }
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