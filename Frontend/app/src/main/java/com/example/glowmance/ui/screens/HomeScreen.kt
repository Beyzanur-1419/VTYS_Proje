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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.glowmance.ui.theme.GlowmanceTheme

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
// TODO: Replace with custom fonts once added to the project
private val RalewayFontFamily = FontFamily.SansSerif
private val LoveloFontFamily = FontFamily.Serif

@Composable
fun HomeScreen(
    userName: String = "Ayşe",
    onAnalysisClick: () -> Unit = {},
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
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp),
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
                    .weight(1f)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Welcome message box with subtle background
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
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
                        
                        // Second message with gradient
                        Text(
                            text = "cildine ışıltı katmaya hazır mısın?",
                            style = TextStyle(
                                fontFamily = RalewayFontFamily,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                brush = roseGoldShimmerGradient
                            ),
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                        
                        // Skin Analysis Button with gradient background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(56.dp)
                                .shadow(6.dp, RoundedCornerShape(28.dp))
                                .clip(RoundedCornerShape(28.dp))
                                .background(brush = roseGoldGradient)
                                .clickable { onAnalysisClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cilt Analizini Başlat",
                                style = TextStyle(
                                    fontFamily = RalewayFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    letterSpacing = 1.sp,
                                    color = Color.White
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Tagline with enhanced style
                        Text(
                            text = "Cildinle aranızdaki parıltılı aşk.",
                            style = TextStyle(
                                fontFamily = RalewayFontFamily,
                                fontSize = 18.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontWeight = FontWeight.Medium,
                                brush = roseGoldShimmerGradient,
                                textAlign = TextAlign.Center
                            )
                        )
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
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.5.dp,
                        color = RoseGold.copy(alpha = 0.7f)
                    )
                    
                    // Thicker part of divider under selected icon
                    Divider(
                        modifier = Modifier
                            .width(60.dp)
                            .align(Alignment.TopStart)
                            .offset(x = (-2).dp), // Position for first icon (Home)
                        thickness = 2.5.dp,
                        color = RoseGold
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
                        modifier = Modifier.clickable { onNavigateToHome() }
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
                        modifier = Modifier.clickable { onNavigateToHistory() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_history),
                            contentDescription = "History",
                            tint = RoseGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    // Shopping bag icon
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onNavigateToShop() }
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
                        modifier = Modifier.clickable { onNavigateToProfile() }
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    GlowmanceTheme {
        HomeScreen()
    }
}