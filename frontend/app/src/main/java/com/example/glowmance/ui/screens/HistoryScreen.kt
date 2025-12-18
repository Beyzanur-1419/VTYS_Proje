package com.example.glowmance.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.glowmance.R
import com.example.glowmance.data.model.AnalysisHistoryItem
import com.example.glowmance.ui.viewmodel.HistoryState
import com.example.glowmance.ui.viewmodel.HistoryViewModel

// Define custom colors
private val RoseGold = Color(0xFFBD8C7D)
private val RoseGoldLight = Color(0xFFE0C1B3)
private val DarkBackground = Color(0xFF1E1E1E) // Fallback if image fails
private val CardBackground = Color(0xFF2C2C2C).copy(alpha = 0.8f)

private val roseGoldGradient = Brush.linearGradient(
    colors = listOf(RoseGoldLight, RoseGold),
    start = Offset(0f, 0f),
    end = Offset(100f, 100f)
)

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel(),
    onNavigateToProfile: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToHome: () -> Unit,
    onAnalysisItemClick: (AnalysisHistoryItem) -> Unit = {}
) {
    val state = viewModel.historyState

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Cilt Gelişim Yolculuğun",
                style = TextStyle(
                    color = RoseGold,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
            )

            // Content based on state
            when (state) {
                is HistoryState.Loading -> {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         CircularProgressIndicator(color = RoseGold)
                     }
                }
                is HistoryState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.White)
                    }
                }
                is HistoryState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Henüz bir analiz geçmişiniz bulunmamaktadır.", color = Color.White)
                    }
                }
                is HistoryState.Success -> {
                    val historyList = state.history
                    
                    // Comparison Card (Big Change)
                    // Only show if we have at least 1 item to show something, ideal is 2
                    if (historyList.isNotEmpty()) {
                        val start = historyList.last() // Oldest
                        val current = historyList.first() // Newest
                         
                         ComparisonCard(start, current)
                         Spacer(modifier = Modifier.height(24.dp))
                    }
                    
                    // Timeline Title
                    Text(
                        text = "Cilt Analiz Geçmişi",
                        style = TextStyle(
                            color = RoseGoldLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    
                    // Timeline List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                            .padding(bottom = 80.dp) // Bottom padding for nav bar
                    ) {
                        itemsIndexed(historyList) { index, item ->
                             TimelineItem(
                                 item = item, 
                                 isLast = index == historyList.lastIndex,
                                 viewModel = viewModel,
                                 onClick = { onAnalysisItemClick(item) }
                             )
                        }
                    }
                }
            }
        }
        
        // Bottom Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
             com.example.glowmance.ui.components.GlowmanceBottomNavigationBar(
                 selectedTab = 1, // History
                 onNavigateToHome = onNavigateToHome,
                 onNavigateToHistory = onNavigateToHistory,
                 onNavigateToShop = onNavigateToShop,
                 onNavigateToProfile = onNavigateToProfile
             )
        }
    }
}

@Composable
fun ComparisonCard(start: AnalysisHistoryItem, current: AnalysisHistoryItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, RoseGold.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Büyük Değişim",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnalysisImageCircle(start.imageUrl, "Başlangıç")
                
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Change",
                    tint = RoseGold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                AnalysisImageCircle(current.imageUrl, "Bugün")
            }
        }
    }
}

@Composable
fun AnalysisImageCircle(imageUrl: String?, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .border(1.dp, RoseGold, CircleShape)
                .padding(4.dp)
                .clip(CircleShape)
        ) {
             val painter = if (imageUrl.isNullOrEmpty()) {
                 painterResource(id = R.drawable.skinresult) // Placeholder
             } else {
                 val finalUrl = if (imageUrl.startsWith("http")) imageUrl else "http://10.52.210.183:3001$imageUrl"
                 rememberAsyncImagePainter(model = finalUrl)
             }
             
            Image(
                painter = painter,
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = label,
            style = TextStyle(color = Color.Gray, fontSize = 12.sp),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun TimelineItem(
    item: AnalysisHistoryItem, 
    isLast: Boolean, 
    viewModel: HistoryViewModel,
    onClick: () -> Unit
) {
    val score = viewModel.calculateScore(item)
    val scoreColor = when {
        score >= 90 -> Color(0xFF4CAF50) // Green
        score >= 75 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick)
    ) {
        // Timeline Line Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            // Dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(RoseGold)
            )
            // Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(RoseGold.copy(alpha = 0.5f))
                )
            }
        }
        
        // Card Content
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp, end = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, RoseGold.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     // Thumbnail
                      Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, RoseGold, CircleShape)
                    ) {
                        val finalUrl = if (item.imageUrl.isNullOrEmpty()) "" else if (item.imageUrl.startsWith("http")) item.imageUrl else "http://10.52.210.183:3001${item.imageUrl}"
                        val painter = if (finalUrl.isEmpty()) painterResource(id = R.drawable.skinresult) else rememberAsyncImagePainter(model = finalUrl)
                        Image(
                            painter = painter,
                            contentDescription = "Thumb",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = viewModel.formatDate(item.createdAt),
                            style = TextStyle(color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = viewModel.generateSummary(item), 
                            style = TextStyle(color = Color.White.copy(alpha = 0.9f), fontSize = 10.sp),
                            maxLines = 2
                        )
                    }
                 }
                 
                 // Score Badge
                 Box(
                     modifier = Modifier
                         .size(32.dp)
                         .clip(RoundedCornerShape(8.dp))
                         .background(scoreColor),
                     contentAlignment = Alignment.Center
                 ) {
                     Text(
                         text = score.toString(),
                         style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                     )
                 }
            }
        }
    }
}

