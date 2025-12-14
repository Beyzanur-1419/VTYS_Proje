package com.example.glowmance.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.glowmance.ui.screens.roseGoldShimmerGradient

// Define custom colors
private val RoseGold = Color(0xFFBD8C7D)
private val RoseGoldLight = Color(0xFFE0C1B3)
private val RoseGoldDark = Color(0xFF9A6959)
private val RoseGoldShimmer1 = Color(0xFFE0C1B3)
private val RoseGoldShimmer2 = Color(0xFFD4A599)
private val RoseGoldShimmer3 = Color(0xFFBD8C7D)
private val RoseGoldShimmer4 = Color(0xFFC9917F)
private val RoseGoldShimmer5 = Color(0xFF9A6959)

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

// Using system fonts temporarily
private val RalewayFontFamily = FontFamily.SansSerif
private val LoveloFontFamily = FontFamily.Serif

// Ürün verisi için data class
data class Product(
    val id: Int,
    val name: String,
    val brand: String,
    val imageResId: Int,
    val rating: Float,
    val price: String,
    val description: String
)

@Composable
fun ProductRecommendationsScreen(
    userName: String = "Ayşe",
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToShop: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    // Örnek ürün verileri
    val productList = listOf(
        Product(
            id = 1,
            name = "Nemlendirici Krem",
            brand = "GlowCare",
            imageResId = R.drawable.logo, // Geçici olarak logo kullanılıyor
            rating = 4.8f,
            price = "249,90 TL",
            description = "Cildinizde tespit edilen kuruluk için ideal nemlendirme sağlar"
        ),
        Product(
            id = 2,
            name = "Akne Karşıtı Serum",
            brand = "PureSkin",
            imageResId = R.drawable.logo, // Geçici olarak logo kullanılıyor
            rating = 4.5f,
            price = "189,90 TL",
            description = "Cildinizde tespit edilen akne için özel formül içerir"
        ),
        Product(
            id = 3,
            name = "Güneş Koruyucu SPF 50",
            brand = "SunShield",
            imageResId = R.drawable.logo, // Geçici olarak logo kullanılıyor
            rating = 4.9f,
            price = "159,90 TL",
            description = "Cildinizi UV ışınlarından koruyarak lekeleri önler"
        ),
        Product(
            id = 4,
            name = "Yatıştırıcı Tonik",
            brand = "CalmSkin",
            imageResId = R.drawable.logo, // Geçici olarak logo kullanılıyor
            rating = 4.6f,
            price = "129,90 TL",
            description = "Tespit edilen cilt hassasiyetini azaltır ve yatıştırır"
        ),
        Product(
            id = 5,
            name = "Hyaluronik Asit Serumu",
            brand = "HydraPlus",
            imageResId = R.drawable.logo, // Geçici olarak logo kullanılıyor
            rating = 4.7f,
            price = "219,90 TL",
            description = "Cildinizin nem dengesini sağlar ve ince çizgileri azaltır"
        )
    )

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
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Main content area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Welcome message with gradient
                Text(
                    text = "Merhaba $userName,",
                    style = TextStyle(
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center,
                        brush = roseGoldShimmerGradient
                    ),
                    fontFamily = RalewayFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp,bottom = 24.dp)
                )
                
                // Subtitle message with gradient
                Text(
                    text = "Cilt durumuna göre önerilen ürünler",
                    style = TextStyle(
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        brush = roseGoldShimmerGradient
                    ),
                    fontFamily = RalewayFontFamily,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // Product list
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(productList) { product ->
                        ProductCard(product = product)
                    }
                }
            }
            
            // Bottom navigation bar with custom divider
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Divider with thicker part under selected icon
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Base thin divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(color = RoseGold.copy(alpha = 0.7f))
                    )
                    
                    // Thicker part of divider under selected icon (Shop selected in this screen)
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(2.5.dp)
                            .align(Alignment.TopCenter)
                            .background(color = RoseGold)
                    )
                }
                
                // Navigation icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Home icon
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        ) { onNavigateToHome() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = RoseGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    // History icon
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        ) { onNavigateToHistory() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_history),
                            contentDescription = "History",
                            tint = RoseGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    // Shopping bag icon (selected)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        ) { onNavigateToShop() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_shopping_bag),
                            contentDescription = "Shop",
                            tint = RoseGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    // Profile icon
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        ) { onNavigateToProfile() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = RoseGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = product.imageResId),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
                // Product details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // Product name
                    Text(
                        text = product.name,
                        style = TextStyle(
                            fontSize = 18.sp
                        ),
                        fontFamily = RalewayFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    
                    // Brand
                    Text(
                        text = product.brand,
                        style = TextStyle(
                            fontSize = 14.sp
                        ),
                        fontFamily = RalewayFontFamily,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    // Description - Neden almalı
                    Text(
                        text = product.description,
                        style = TextStyle(
                            fontSize = 13.sp
                        ),
                        fontFamily = RalewayFontFamily,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    // Rating and Price Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Rating
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFD700), // Gold color for stars
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            Text(
                                text = product.rating.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp
                                ),
                                fontFamily = RalewayFontFamily,
                                color = Color.White,
                            )
                        }
                        
                        // Price with gradient
                        Text(
                            text = product.price,
                            style = TextStyle(
                                fontSize = 16.sp
                            ),
                            fontFamily = RalewayFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductRecommendationsScreenPreview() {
    MaterialTheme {
        ProductRecommendationsScreen()
    }
}
