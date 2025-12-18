package com.example.glowmance.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.glowmance.R
import com.example.glowmance.data.model.Product
import com.example.glowmance.ui.viewmodel.ProductState
import com.example.glowmance.ui.viewmodel.ProductViewModel

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

@Composable
fun ProductRecommendationsScreen(
    viewModel: ProductViewModel = viewModel(),
    userName: String = viewModel.userName, // Get from ViewModel dynamically
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToShop: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val state = viewModel.productState
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    // Dialog handling
    if (selectedProduct != null) {
        ProductDetailDialog(
            product = selectedProduct!!,
            onDismiss = { selectedProduct = null }
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
                
                // Product list content based on state
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    when (state) {
                        is ProductState.Loading -> {
                            CircularProgressIndicator(
                                color = RoseGold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        is ProductState.Error -> {
                            Text(
                                text = state.message,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        is ProductState.Empty -> {
                            Text(
                                text = "Şu anda önerilecek ürün bulunamadı.",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        is ProductState.Success -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.products) { product ->
                                    ProductCard(
                                        product = product,
                                        onClick = { selectedProduct = product }
                                    )
                                }
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
                    
                    // Shopping bag icon (selected)
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

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp) // Reduced height since price is gone
            .padding(4.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            brush = roseGoldShimmerGradient
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
                    .size(90.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                 val finalUrl = if (product.imageUrl.isNullOrEmpty()) "" else if (product.imageUrl.startsWith("http")) product.imageUrl else "http://10.0.2.2:3001${product.imageUrl}"
                 val painter = rememberAsyncImagePainter(
                    model = androidx.compose.ui.platform.LocalContext.current.run {
                        coil.request.ImageRequest.Builder(this)
                            .data(finalUrl.takeIf { it.isNotEmpty() } ?: R.drawable.logo)
                            .crossfade(true)
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .build()
                    }
                 )

                Image(
                    painter = painter,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(70.dp)
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

                // Description on card (truncated)
                Text(
                    text = product.description ?: "Detay bulunamadı.",
                    style = TextStyle(
                        fontFamily = RalewayFontFamily,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    ),
                    maxLines = 2,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // "Detayları gör" hint
                Text(
                    text = "Detayları ve içeriği gör >",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = RoseGold.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ProductDetailDialog(
    product: Product,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier
                            .clickable(onClick = onDismiss)
                            .padding(4.dp)
                    )
                }

                // Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                     val finalUrl = if (product.imageUrl.isNullOrEmpty()) "" else if (product.imageUrl.startsWith("http")) product.imageUrl else "http://10.0.2.2:3001${product.imageUrl}"
                     val painter = rememberAsyncImagePainter(
                        model = androidx.compose.ui.platform.LocalContext.current.run {
                            coil.request.ImageRequest.Builder(this)
                                .data(finalUrl.takeIf { it.isNotEmpty() } ?: R.drawable.logo)
                                .crossfade(true)
                                .placeholder(R.drawable.logo)
                                .error(R.drawable.logo)
                                .build()
                        }
                     )
                     
                    Image(
                        painter = painter,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = product.name,
                    style = TextStyle(color = RoseGold, fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = product.brand,
                    style = TextStyle(color = Color.Gray, fontSize = 16.sp),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Description Section
                Text(
                    text = "Ürün Açıklaması:",
                    style = TextStyle(color = RoseGold, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = product.description ?: "Açıklama bulunamadı.",
                    style = TextStyle(color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp),
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp),
                    lineHeight = 20.sp
                )

                // Ingredients Section
                if (!product.ingredients.isNullOrEmpty()) {
                     Text(
                        text = "İçindekiler:",
                        style = TextStyle(color = RoseGold, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = product.ingredients.joinToString(", "),
                        style = TextStyle(color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp),
                        modifier = Modifier.align(Alignment.Start),
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
