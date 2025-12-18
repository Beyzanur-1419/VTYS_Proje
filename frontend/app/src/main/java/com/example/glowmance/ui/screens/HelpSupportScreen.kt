package com.example.glowmance.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.glowmance.R

private val RoseGold = Color(0xFFBD8C7D)
private val RoseGoldLight = Color(0xFFE0C1B3)
private val RoseGoldDark = Color(0xFF9A6959)

private val roseGoldGradient = Brush.linearGradient(
    colors = listOf(RoseGoldLight, RoseGold, RoseGoldDark)
)

private val RalewayFontFamily = FontFamily.SansSerif

@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val faqList = listOf(
        "Cilt analizi nasıl yapılır?" to "Yapmanız gereken tek şey yüzünüzün net bir fotoğrafını yüklemek. Yapay zeka algoritmamız cildinizi analiz ederek akne, leke, kırışıklık gibi detayları tespit eder ve size özel rapor sunar.",
        "Ürün önerileri neye göre belirleniyor?" to "Cilt analiz sonucunuza ve belirttiğiniz cilt hedeflerinize (örn: nemlendirme, yaşlanma karşıtı) en uygun içeriklere sahip ürünler veritabanımızdan seçilerek size sunulur.",
        "Sonuçlarım kaydediliyor mu?" to "Evet, analiz geçmişiniz güvenli bir şekilde saklanır. 'Geçmiş' sekmesinden gelişiminizi takip edebilirsiniz.",
        "Premium üyelik gerekli mi?" to "Temel analiz ücretsizdir. Ancak detaylı raporlar ve sınırsız analiz için Premium üyelik avantajlarına göz atabilirsiniz."
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Yardım ve Destek",
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    fontFamily = RalewayFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Content
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Sıkça Sorulan Sorular",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        fontFamily = RalewayFontFamily,
                        color = RoseGold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(faqList.size) { index ->
                    FAQItem(question = faqList[index].first, answer = faqList[index].second)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Contact Button
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:destek@glowmance.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Glowmance Destek Talebi")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(roseGoldGradient, RoundedCornerShape(28.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Bize Ulaşın",
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                                fontFamily = RalewayFontFamily,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E).copy(alpha = 0.8f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    fontFamily = RalewayFontFamily,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = if (expanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down), // Assuming icons exist, otherwise fallback
                    contentDescription = null,
                    tint = RoseGold
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = RoseGold.copy(alpha = 0.3f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = answer,
                        style = TextStyle(fontSize = 14.sp),
                        fontFamily = RalewayFontFamily,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
