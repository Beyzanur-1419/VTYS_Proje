package com.example.glowmance.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

// Geçmiş analiz verisi için data class
data class SkinAnalysisHistory(
    val id: Int,
    val date: Date,
    val skinConditionResult: SkinConditionResult,
    val imageResId: Int = R.drawable.skinresult, // Geçici olarak varsayılan resim
    val score: Int = 0 // Puan göstergesi için eklendi
)

@Composable
fun HistoryScreen(
    userName: String = "Ayşe",
    historyList: List<com.example.glowmance.data.network.AnalysisHistoryItem> = emptyList(),
    isLoading: Boolean = false,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToShop: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onAnalysisItemClick: (SkinAnalysisHistory) -> Unit = {}
) {
    // Backend'den gelen veriyi SkinAnalysisHistory formatına dönüştür
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val dateFormatOutput = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    
    val convertedHistoryList = historyList.map { item ->
        val date = try {
            dateFormat.parse(item.createdAt) ?: Date()
        } catch (e: Exception) {
            Date()
        }
        
        // Puan hesaplama (basit bir algoritma)
        val score = when {
            item.isNormal == true -> 95
            !(item.hasEczema ?: false) && !(item.hasAcne ?: false) && !(item.hasRosacea ?: false) -> 90
            (item.hasEczema ?: false) && (item.hasAcne ?: false) -> 70
            (item.hasEczema ?: false) || (item.hasAcne ?: false) || (item.hasRosacea ?: false) -> 80
            else -> 75
        }
        
        SkinAnalysisHistory(
            id = item.id.toIntOrNull() ?: 0,
            date = date,
            skinConditionResult = SkinConditionResult(
                hasEczema = item.hasEczema ?: false,
                hasAcne = item.hasAcne ?: false,
                hasRosacea = item.hasRosacea ?: false,
                isNormal = item.isNormal ?: false
            ),
            imageResId = R.drawable.skinresult,
            score = score
        )
    }

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
            
            // Main content area - Yeni Tasarım
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Başlık
                Text(
                    text = "Cilt Gelişim Yolculuğun",
                    style = TextStyle(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp,
                        brush = roseGoldShimmerGradient
                    ),
                    fontFamily = RalewayFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp,bottom = 24.dp)
                )
                
                // Büyük Değişim Kartı
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
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
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Büyük Değişim Başlık
                        Text(
                            text = "Büyük Değişim",
                            style = TextStyle(
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            ),
                            fontFamily = RalewayFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = RoseGoldLight,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Öncesi ve Sonrası Görselleri
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Başlangıç Görsel
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = 2.dp,
                                            color = RoseGold,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.skinresult),
                                        contentDescription = "Öncesi",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                
                                Text(
                                    text = "Başlangıç",
                                    style = TextStyle(fontSize = 14.sp),
                                    fontFamily = RalewayFontFamily,
                                    color = RoseGold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            
                            // Ok İşareti
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_forward),
                                contentDescription = "İlerleme",
                                tint = RoseGold,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            // Bugün Görsel
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = 2.dp,
                                            color = RoseGold,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.skinresult),
                                        contentDescription = "Sonrası",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                
                                Text(
                                    text = "Bugün",
                                    style = TextStyle(fontSize = 14.sp),
                                    fontFamily = RalewayFontFamily,
                                    color = RoseGold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
                
                // Analiz Zaman Çizelgesi
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Zaman Çizelgesi Başlık
                    Text(
                        text = "Cilt Analiz Geçmişi",
                        style = TextStyle(
                            fontSize = 18.sp,
                            brush = roseGoldShimmerGradient
                        ),
                        fontFamily = RalewayFontFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Zaman Çizelgesi
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Yükleniyor...",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    } else if (convertedHistoryList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Henüz analiz geçmişi yok",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(convertedHistoryList) { historyItem ->
                                TimelineItem(
                                    historyItem = historyItem,
                                    onClick = { onAnalysisItemClick(historyItem) }
                                )
                            }
                        }
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
                    
                    // Thicker part of divider under selected icon (History selected in this screen)
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(2.5.dp)
                            .align(Alignment.CenterStart)
                            .padding(start = 80.dp)
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
                    
                    // History icon (selected)
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
                    
                    // Profile icon
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
fun TimelineItem(
    historyItem: SkinAnalysisHistory,
    onClick: () -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("tr"))
    val shortDateFormat = SimpleDateFormat("dd MMM yyyy", Locale("tr"))
    val formattedDate = shortDateFormat.format(historyItem.date)
    
    // Cilt durumuna göre özet metin oluştur
    val conditionSummary = when {
        historyItem.skinConditionResult.isNormal -> "Normal cilt durumu"
        historyItem.skinConditionResult.hasEczema && historyItem.skinConditionResult.hasAcne -> "Egzama ve Akne tespit edildi"
        historyItem.skinConditionResult.hasEczema -> "Egzama tespit edildi"
        historyItem.skinConditionResult.hasAcne -> "Akne seviyesinde düşüş"
        historyItem.skinConditionResult.hasRosacea -> "İlk Analiz"
        else -> "Nem oranı arttı"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            ) { onClick() },
        verticalAlignment = Alignment.Top
    ) {
        // Sol taraf - Zaman çizgisi
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(80.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Dikey çizgi
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(80.dp)
                    .background(RoseGold.copy(alpha = 0.5f))
            )
            
            // Daire
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(RoseGold)
            )
        }
        
        // Sağ taraf - Analiz kartı
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A2E).copy(alpha = 0.8f)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = RoseGold.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Küçük resim
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = painterResource(id = historyItem.imageResId),
                        contentDescription = "Analiz Resmi",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                // Tarih ve durum
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = "$formattedDate - ${conditionSummary}",
                        style = TextStyle(fontSize = 14.sp),
                        fontFamily = RalewayFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
                
                // Puan göstergesi
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when {
                                historyItem.score >= 90 -> Color(0xFF4CAF50) // Yeşil
                                historyItem.score >= 80 -> Color(0xFFFFA726) // Turuncu
                                else -> Color(0xFFE57373) // Kırmızı
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = historyItem.score.toString(),
                        style = TextStyle(fontSize = 14.sp),
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
fun HistoryScreenPreview() {
    MaterialTheme {
        HistoryScreen()
    }
}