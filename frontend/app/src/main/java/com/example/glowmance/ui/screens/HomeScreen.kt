package com.example.glowmance.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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

// Notification data class
data class Notification(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val isRead: Boolean
)

@Composable
fun HomeScreen(
    userName: String = "Ayşe",
    onAnalysisClick: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToShop: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
) {
    // State for bottom sheets
    var showProfileBottomSheet by remember { mutableStateOf(false) }
    var showNotificationBottomSheet by remember { mutableStateOf(false) }
    
    // Mock notification data
    val notifications = remember {
        listOf(
            Notification(
                id = "1",
                title = "Cilt Analiz Sonucu",
                description = "Cilt analizin hazır! Sonuçlarını görüntüle.",
                timestamp = "10 dakika önce",
                isRead = false
            ),
            Notification(
                id = "2",
                title = "Rutin Hatırlatıcı",
                description = "Akşam rutinini uygulama zamanı geldi.",
                timestamp = "1 saat önce",
                isRead = false
            ),
            Notification(
                id = "3",
                title = "Ürün Önerisi",
                description = "Cilt tipine uygun yeni ürünler keşfet!",
                timestamp = "Dün",
                isRead = true
            )
        )
    }
    
    // Check for unread notifications
    val hasUnreadNotifications = notifications.any { !it.isRead }
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
                    // Notification icon with badge
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = RoseGold,
                            modifier = Modifier
                                .size(31.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple()
                                ) { showNotificationBottomSheet = true }
                        )
                        
                        // Notification badge
                        if (hasUnreadNotifications) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color.White, RoundedCornerShape(5.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Profile icon - opens bottom sheet instead of navigating
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = RoseGold,
                        modifier = Modifier
                            .size(31.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple()
                            ) { showProfileBottomSheet = true }
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
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple()
                                ) { onAnalysisClick() },
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
    
    // Show Profile Bottom Sheet if state is true
    if (showProfileBottomSheet) {
        ProfileBottomSheet(
            onDismiss = { showProfileBottomSheet = false },
            onProfileClick = { 
                showProfileBottomSheet = false
                onNavigateToProfile() 
            },
            onSettingsClick = { /* Navigate to settings */ },
            onLogoutClick = { /* Handle logout */ }
        )
    }
    
    // Show Notification Bottom Sheet if state is true
    if (showNotificationBottomSheet) {
        NotificationBottomSheet(
            notifications = notifications,
            onDismiss = { showNotificationBottomSheet = false },
            onNotificationClick = { /* Handle notification click */ }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
    onDismiss: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Black.copy(alpha = 0.85f),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            // Header
            Text(
                text = "Profil Menü",
                style = TextStyle(
                    fontFamily = RalewayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    brush = roseGoldShimmerGradient
                ),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Profile option
            ListItem(
                headlineContent = { 
                    Text(
                        "Profil", 
                        color = Color.White,
                        fontFamily = RalewayFontFamily
                    ) 
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = RoseGold
                    )
                },
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple()
                ) { onProfileClick() }
            )
            
            // Settings option
            ListItem(
                headlineContent = { 
                    Text(
                        "Ayarlar", 
                        color = Color.White,
                        fontFamily = RalewayFontFamily
                    ) 
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = RoseGold
                    )
                },
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple()
                ) { onSettingsClick() }
            )
            
            // Logout option
            ListItem(
                headlineContent = { 
                    Text(
                        "Çıkış Yap", 
                        color = Color.White,
                        fontFamily = RalewayFontFamily
                    ) 
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = RoseGold
                    )
                },
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple()
                ) { onLogoutClick() }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationBottomSheet(
    notifications: List<Notification>,
    onDismiss: () -> Unit,
    onNotificationClick: (Notification) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Black.copy(alpha = 0.85f),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Header
            Text(
                text = "Bildirimler",
                style = TextStyle(
                    fontFamily = RalewayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    brush = roseGoldShimmerGradient
                ),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (notifications.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bildirim bulunmuyor",
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = RalewayFontFamily
                    )
                }
            } else {
                // Notification list
                LazyColumn {
                    items(notifications) { notification ->
                        NotificationItem(
                            notification = notification,
                            onClick = { onNotificationClick(notification) }
                        )
                        
                        // Divider between items (except after the last one)
                        if (notification != notifications.last()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(0.5.dp)
                                    .background(RoseGold.copy(alpha = 0.3f))
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Unread indicator
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(RoseGold, RoundedCornerShape(4.dp))
                    .padding(end = 8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }
        
        // Notification content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Title
            Text(
                text = notification.title,
                style = TextStyle(
                    fontFamily = RalewayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = if (!notification.isRead) Color.White else Color.White.copy(alpha = 0.8f)
            )
            
            // Description
            Text(
                text = notification.description,
                style = TextStyle(
                    fontFamily = RalewayFontFamily,
                    fontSize = 14.sp
                ),
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 2
            )
            
            // Timestamp
            Text(
                text = notification.timestamp,
                style = TextStyle(
                    fontFamily = RalewayFontFamily,
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp)
            )
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