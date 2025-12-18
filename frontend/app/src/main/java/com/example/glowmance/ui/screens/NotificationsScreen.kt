package com.example.glowmance.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
fun NotificationsScreen(
    currentSettings: com.example.glowmance.data.NotificationSettings,
    onBackClick: () -> Unit,
    onSaveSettings: (Boolean, Boolean, Boolean) -> Unit
) {
    var analysisReminder by remember { mutableStateOf(currentSettings.analysisReminder) }
    var campaigns by remember { mutableStateOf(currentSettings.campaigns) }
    var tips by remember { mutableStateOf(currentSettings.tips) }

    val scope = rememberCoroutineScope()
    val userApi = com.example.glowmance.data.api.RetrofitClient.userApi
    val userPreferences = com.example.glowmance.data.UserPreferences.getInstance(androidx.compose.ui.platform.LocalContext.current)

    // Save changes whenever toggles change
    LaunchedEffect(analysisReminder, campaigns, tips) {
        onSaveSettings(analysisReminder, campaigns, tips)
        // Backend Sync
        val token = userPreferences.getAuthToken()
        if (token != null) {
            try {
                userApi.updateSettings(
                    "Bearer $token",
                    com.example.glowmance.data.model.NotificationSettingsRequest(
                        notificationEnabled = true, // Master toggle assumption
                        emailNotifications = true, // Master toggle assumption
                        analysisReminder = analysisReminder,
                        campaigns = campaigns,
                        tips = tips
                    )
                )
            } catch (e: Exception) {
                // Silent fail or log
            }
        }
    }

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
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Bildirim Ayarları",
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    fontFamily = RalewayFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

    val context = androidx.compose.ui.platform.LocalContext.current

    // ... (rest of the composable)

            // Settings List
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NotificationToggleItem(
                    title = "Haftalık Analiz Hatırlatması",
                    description = "Ciltinizin gelişimini takip etmeniz için haftalık hatırlatmalar",
                    checked = analysisReminder,
                    onCheckedChange = { 
                        analysisReminder = it 
                        if (!it) {
                             android.widget.Toast.makeText(context, "Artık analiz hatırlatması almayacaksınız.", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                NotificationToggleItem(
                    title = "Kampanya ve İndirimler",
                    description = "Size özel ürün fırsatlarından haberdar olun",
                    checked = campaigns,
                    onCheckedChange = { 
                        campaigns = it 
                         if (!it) {
                             android.widget.Toast.makeText(context, "Artık kampanya bildirimi almayacaksınız.", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                NotificationToggleItem(
                    title = "Günlük Cilt Bakım İpuçları",
                    description = "Mevsim ve hava durumuna göre günlük öneriler",
                    checked = tips,
                    onCheckedChange = { 
                        tips = it 
                        if (!it) {
                            android.widget.Toast.makeText(context, "Artık bu bildirimleri almayacaksınız.", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E).copy(alpha = 0.8f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f).padding(end = 16.dp)
            ) {
                Text(
                    text = title,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    fontFamily = RalewayFontFamily,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = TextStyle(fontSize = 12.sp),
                    fontFamily = RalewayFontFamily,
                    color = Color.White.copy(alpha = 0.7f)
                )
                
                // Feedback Text
                if (!checked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bildirimlerden haberdar olmak için açın.",
                        style = TextStyle(fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        fontFamily = RalewayFontFamily,
                        color = RoseGold
                    )
                } else {
                     // Option to show "You will generally receive notifications" or nothing.
                     // User asked: "kapattığı içinde artık bildirim almıcaksınız desin" -> implied when OFF or turning OFF.
                     // The text above covers the OFF state.
                     // If we want a Toast when turning off, we can do that in the callback.
                }
            }
            
            Switch(
                checked = checked,
                onCheckedChange = { isChecked ->
                    onCheckedChange(isChecked)
                    if (!isChecked) {
                         // Toast or Snackbar could go here, but compoasable redraws with text above.
                         // Let's rely on the text update for "artık bildirim almayacaksınız" semantic.
                         // But to be precise to the request "kapattığı içinde artık bildirim almıcaksınız desin":
                         // I will add a text that appears when checked is false.
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = RoseGold,
                    checkedTrackColor = RoseGold.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }
    }
}
