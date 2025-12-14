package com.example.glowmance.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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

@Composable
fun ProfileScreen(
    userName: String = "Ela",
    skinType: String = "Karma / Hassas",
    skinGoal: String = "Leke Karşıtı & Nem",
    age: Int = 26,
    analysisCount: Int = 12,
    routineMatch: Int = 85,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToShop: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onSettings: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onHelp: () -> Unit = {},
    onLogout: () -> Unit = {}
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Main content area - Profile
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Cilt Kimlik Kartı
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A2E)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = roseGoldShimmerGradient
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Kart Başlığı
                        Text(
                            text = "Cilt Kimlik Kartı",
                            style = TextStyle(
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            ),
                            fontFamily = RalewayFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = RoseGoldLight,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp,bottom = 16.dp)
                        )
                        
                        // Profil Resmi ve Bilgiler
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Profil Resmi
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        brush = roseGoldGradient,
                                        shape = CircleShape
                                    )
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.skinresult),
                                    contentDescription = "Profil Resmi",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // Kullanıcı Bilgileri
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                // Ad
                                Row(
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Text(
                                        text = "Ad: ",
                                        style = TextStyle(fontSize = 14.sp),
                                        fontFamily = RalewayFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = userName,
                                        style = TextStyle(fontSize = 14.sp),
                                        fontFamily = RalewayFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                
                                // Cilt Tipi
                                Row(
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Text(
                                        text = "Cilt Tipi: ",
                                        style = TextStyle(fontSize = 14.sp),
                                        fontFamily = RalewayFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = skinType,
                                        style = TextStyle(fontSize = 14.sp),
                                        fontFamily = RalewayFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                
                                // Ana Hedef
                                Row(
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Text(
                                        text = "Ana Hedef: ",
                                        style = TextStyle(fontSize = 14.sp),
                                        fontFamily = RalewayFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = skinGoal,
                                        style = TextStyle(fontSize = 14.sp),
                                        fontFamily = RalewayFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                
                                // Yaş
                                Row {
                                    Text(
                                        text = "Yaş: ",
                                        style = TextStyle(fontSize = 14.sp),
                                        fontFamily = RalewayFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = age.toString(),
                                        style = TextStyle(fontSize = 14.sp),
                                        fontFamily = RalewayFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // İstatistikler
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Analiz Sayısı
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = analysisCount.toString(),
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = "Analiz",
                                    style = TextStyle(
                                        fontSize = 14.sp
                                    ),
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            
                            // Dikey Ayırıcı
                            Box(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp)
                                    .background(Color.White.copy(alpha = 0.3f))
                            )
                            
                            // Rutin Uyumu
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "%$routineMatch",
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = "Rutin Uyumu",
                                    style = TextStyle(
                                        fontSize = 14.sp
                                    ),
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
                
                // Profil Menü Öğeleri
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    // Hesap Ayarları
                    ProfileMenuItem(
                        icon = R.drawable.ic_settings,
                        title = "Hesap Ayarları",
                        onClick = onSettings
                    )
                    
                    // Cilt Profilini Düzenle
                    ProfileMenuItem(
                        icon = R.drawable.ic_edit,
                        title = "Cilt Profilini Düzenle",
                        onClick = onEditProfile
                    )
                    
                    // Bildirimler
                    ProfileMenuItem(
                        icon = R.drawable.ic_notifications,
                        title = "Bildirimler",
                        onClick = onNotifications
                    )
                    
                    // Yardım & Destek
                    ProfileMenuItem(
                        icon = R.drawable.ic_help,
                        title = "Yardım & Destek",
                        onClick = onHelp
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Çıkış Yap Butonu
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(brush = roseGoldGradient)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        ) { onLogout() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Çıkış Yap",
                        style = TextStyle(
                            fontSize = 16.sp,
                        ),
                        color = Color.White
                    )
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
                    
                    // Thicker part of divider under selected icon (Profile selected in this screen)
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(2.5.dp)
                            .align(Alignment.TopEnd)
                            .padding(end = 40.dp)
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
                            painter = painterResource(id = R.drawable.ic_home),
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
                    
                    // Shopping bag icon
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
                    
                    // Profile icon (selected)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        ) { onNavigateToProfile() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
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
fun ProfileMenuItem(
    icon: Int,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            ) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = RoseGold,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Title
        Text(
            text = title,
            style = TextStyle(
                fontSize = 16.sp
            ),
            fontFamily = RalewayFontFamily,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        
        // Arrow icon
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_forward),
            contentDescription = "İleri",
            tint = RoseGold.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen()
    }
}

