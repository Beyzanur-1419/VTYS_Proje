package com.example.glowmance.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.glowmance.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// Define custom colors
private val RoseGold = Color(0xFFBD8C7D)
private val RoseGoldLight = Color(0xFFE0C1B3)
private val RoseGoldDark = Color(0xFF9A6959)
private val RoseGoldShimmer1 = Color(0xFFE0C1B3)
private val RoseGoldShimmer2 = Color(0xFFD4A599)
private val RoseGoldShimmer3 = Color(0xFFBD8C7D)
private val RoseGoldShimmer4 = Color(0xFFC9917F)
private val RoseGoldShimmer5 = Color(0xFF9A6959)

// Skin condition colors
private val EczemaColor = Color(0xFFD32F2F) // Red
private val EczemaColorLight = Color(0xFFEF5350)
private val AcneColor = Color(0xFFFF9800) // Orange
private val AcneColorLight = Color(0xFFFFB74D)
private val RosaceaColor = Color(0xFFE91E63) // Pink
private val RosaceaColorLight = Color(0xFFF48FB1)
private val NormalColor = Color(0xFF4CAF50) // Green
private val NormalColorLight = Color(0xFF81C784)

// Define gradient brushes
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

// Skin condition gradients
private val eczemaGradient = Brush.linearGradient(
    colors = listOf(EczemaColorLight, EczemaColor),
    start = Offset(0f, 0f),
    end = Offset(100f, 100f)
)

private val acneGradient = Brush.linearGradient(
    colors = listOf(AcneColorLight, AcneColor),
    start = Offset(0f, 0f),
    end = Offset(100f, 100f)
)

private val rosaceaGradient = Brush.linearGradient(
    colors = listOf(RosaceaColorLight, RosaceaColor),
    start = Offset(0f, 0f),
    end = Offset(100f, 100f)
)

private val normalGradient = Brush.linearGradient(
    colors = listOf(NormalColorLight, NormalColor),
    start = Offset(0f, 0f),
    end = Offset(100f, 100f)
)

// Using system fonts temporarily
// TODO: Replace with custom fonts once added to the project
private val RalewayFontFamily = FontFamily.SansSerif
private val LoveloFontFamily = FontFamily.Serif

// Data class to represent skin condition results
data class SkinConditionResult(
    val hasEczema: Boolean = false,
    val eczemaLevel: String = "Yok",
    val hasAcne: Boolean = false,
    val acneLevel: String = "Yok",
    val hasRosacea: Boolean = false,
    val rosaceaLevel: String = "Yok",
    val isNormal: Boolean = true,
    val detectedSkinType: String = "",
    val detectedDisease: String = ""
)

// Extension function to convert to data layer SkinAnalysisResult
fun SkinConditionResult.toSkinAnalysisResult() = com.example.glowmance.data.SkinAnalysisResult(
    hasEczema = hasEczema,
    eczemaLevel = eczemaLevel,
    hasAcne = hasAcne,
    acneLevel = acneLevel,
    hasRosacea = hasRosacea,
    rosaceaLevel = rosaceaLevel,
    isNormal = isNormal,
    detectedSkinType = detectedSkinType,
    detectedDisease = detectedDisease
)

@Composable
fun SkinResultScreen(
    userName: String = "Ayşe",
    skinConditionResult: SkinConditionResult = SkinConditionResult(),
    recommendedProducts: List<ProductUI> = emptyList(),
    onNewAnalysisClick: () -> Unit = {},
    onRecommendedProductsClick: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToShop: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
) {
    // This Box acts as our background container
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image with space theme
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Content column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // Top bar with logo on left and notification/profile on right
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Logo and title section in top left corner
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 45.dp)
                ) {
                    // Logo (küçük boyutlu, gölgeli)
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .shadow(4.dp, RoundedCornerShape(30.dp)),
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

                    // Glowmance başlığı (rose-gold shimmer efekti)
                    Box(contentAlignment = Alignment.Center) {
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
                
                // Notification and profile icons on right
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 65.dp, start = 10.dp)
                ) {
                    // Notification icon
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = RoseGold,
                        modifier = Modifier
                            .size(31.dp)
                            .clickable { /* Bildirim tıklama olayı */ }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Profile icon
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = RoseGold,
                        modifier = Modifier
                            .size(31.dp)
                            .clickable { onNavigateToProfile() }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Main content area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Welcome message with gradient
                Text(
                    text = "Merhaba $userName,",
                    style = TextStyle(
                        fontFamily = RalewayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center,
                        brush = roseGoldShimmerGradient
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Result message with gradient
                Text(
                    text = "işte cildinin durumu",
                    style = TextStyle(
                        fontFamily = RalewayFontFamily,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        brush = roseGoldShimmerGradient
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // --- NEW SUMMARY CARD ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ANALİZ SONUCU",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (skinConditionResult.detectedSkinType.isNotEmpty()) 
                                skinConditionResult.detectedSkinType.uppercase() else "BİLİNMİYOR",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                brush = roseGoldGradient
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                         Text(
                            text = "(${if (skinConditionResult.detectedDisease.isNotEmpty()) 
                                skinConditionResult.detectedDisease else "Durum Tespit Edilemedi"})",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = RoseGoldDark
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                // ------------------------
                
                // Skin condition cards in a 2x2 grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // First row of cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Eczema Card
                        SkinConditionCard(
                            title = "Egzema",
                            condition = if (skinConditionResult.hasEczema) 
                                "Egzama – Tahriş Var" else "Egzama – Yok",
                            gradient = eczemaGradient,
                            isActive = skinConditionResult.hasEczema,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Acne Card
                        SkinConditionCard(
                            title = "Akne Durumu",
                            condition = if (skinConditionResult.hasAcne) 
                                "Akne – Orta Seviye" else "Akne – Yok",
                            gradient = acneGradient,
                            isActive = skinConditionResult.hasAcne,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Second row of cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Rosacea Card
                        SkinConditionCard(
                            title = "Rozase",
                            condition = if (skinConditionResult.hasRosacea) 
                                "Gül Hastalığı – Hassasiyet Yüksek" else "Rozase – Yok",
                            gradient = rosaceaGradient,
                            isActive = skinConditionResult.hasRosacea,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Normal Card
                        SkinConditionCard(
                            title = "Normal",
                            condition = if (skinConditionResult.isNormal) 
                                "Cilt Durumu: Normal" else "Normal Değil",
                            gradient = normalGradient,
                            isActive = skinConditionResult.isNormal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Action buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // New Analysis Button with gradient background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                            .shadow(6.dp, RoundedCornerShape(28.dp))
                            .clip(RoundedCornerShape(28.dp))
                            .background(brush = roseGoldGradient)
                            .clickable { onNewAnalysisClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Yeni Analiz Yap",
                            style = TextStyle(
                                fontFamily = RalewayFontFamily,
                                fontSize = 18.sp,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Recommended Products Button with gradient background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                            .shadow(6.dp, RoundedCornerShape(28.dp))
                            .clip(RoundedCornerShape(28.dp))
                            .background(brush = roseGoldGradient)
                            .clickable { onRecommendedProductsClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Önerilen ürünlere göz at",
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
            
            // Recommended Products Section REMOVED as per user request
            // if (recommendedProducts.isNotEmpty()) { ... }
            
            // Bottom Navigation Bar
            com.example.glowmance.ui.components.GlowmanceBottomNavigationBar(
                selectedTab = -1,
                onNavigateToHome = onNavigateToHome,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToShop = onNavigateToShop,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    }
}

// Simple Product Data Class for UI
data class ProductUI(
    val name: String,
    val brand: String,
    val imageUrl: String,
    val price: String = ""
)

@Composable
fun ProductCard(product: ProductUI) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(240.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Product Image (Placeholder or AsyncImage)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.Gray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                 // Using a placeholder icon for now since we might not have Coil set up perfectly yet 
                 // or just use basic Image if drawable resource available. 
                 // For dynamic URLs we need Coil/Glide. Assuming Coil is available or using placeholder.
                 Icon(
                     imageVector = Icons.Default.Home, // Placeholder icon
                     contentDescription = null,
                     tint = RoseGoldShimmer3,
                     modifier = Modifier.size(48.dp)
                 )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.brand,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = product.name,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                if (product.price.isNotEmpty()) {
                    Text(
                        text = product.price,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = RoseGoldDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SkinConditionCard(
    title: String,
    condition: String,
    gradient: Brush,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(150.dp)
            .padding(4.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Şeffaf arka plan
        ),
        border = BorderStroke(
            width = 1.dp,
            brush = roseGoldShimmerGradient // Rose gold çerçeve
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = title,
                color = Color.White, // Beyaz başlık
                fontFamily = RalewayFontFamily,
                letterSpacing = 1.sp,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            
            // Condition with gradient if active
            if (isActive) {
                Text(
                    text = condition,
                    style = TextStyle(
                        brush = gradient, // Aktif durumda gradient renk korunuyor
                        fontFamily = RalewayFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                )
            } else {
                Text(
                    text = condition,
                    color = Color.White.copy(alpha = 0.7f), // Beyaz ama hafif saydam
                    fontFamily = RalewayFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SkinResultScreenPreview() {
    MaterialTheme {
        SkinResultScreen(
            skinConditionResult = SkinConditionResult(
                hasEczema = true,
                hasAcne = true,
                hasRosacea = false,
                isNormal = false
            )
        )
    }
}