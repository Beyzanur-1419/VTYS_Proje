package com.example.glowmance.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.glowmance.R
import com.example.glowmance.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Define custom colors (same as HomeScreen)
private val RoseGold = Color(0xFFBD8C7D)
private val RoseGoldLight = Color(0xFFE0C1B3)
private val RoseGoldDark = Color(0xFF9A6959)

// Define gradient brush (same as HomeScreen)
private val roseGoldGradient = Brush.linearGradient(
    colors = listOf(RoseGoldLight, RoseGold, RoseGoldDark),
    start = Offset(0f, 0f),
    end = Offset(100f, 100f)
)

@Composable
fun FaceScanningScreen(
    navController: NavController,
    viewModel: com.example.glowmance.ui.viewmodel.AnalysisViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // var progress by remember { mutableStateOf(0f) } // Remove manual progress for now or simulate it while loading
    
    // Real analysis process
    LaunchedEffect(key1 = true) {
        viewModel.performAnalysis(context) {
             // On success
             Log.d("FaceScanningScreen", "Analiz başarılı, sonuç ekranına yönlendiriliyor...")
             navController.navigate(Screen.SkinResult.route) {
                popUpTo(Screen.FaceScanning.route) { inclusive = true }
             }
        }
    }
    
    // Background with space theme
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
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
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Title at the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Glowmance AI Skin Advisor",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = if (viewModel.isLoading) "Cilt Analizi işleniyor..." else "Analiz Tamamlandı",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Subtitle
            Text(
                text = if (viewModel.isLoading) 
                    "Yapay zeka analizimiz yüz hatlarınızı inceliyor ve size özel sonuçlar hazırlıyor."
                else
                    "Sonuçlar hazırlanıyor, yönlendiriliyorsunuz...",
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Face scan animation image
            Image(
                painter = painterResource(id = R.drawable.skinresult),
                contentDescription = "Face Scanning",
                modifier = Modifier.size(280.dp),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Progress bar (Indeterminate while loading)
            if (viewModel.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = RoseGold,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
            
            // Error Message
             viewModel.error?.let { error ->
                Text(
                    text = error,
                    style = TextStyle(
                        color = Color.Red, // Visible error color
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Bottom text
            Text(
                text = "Lütfen bekleyin, sonuçlarınız hazırlanıyor...",
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
