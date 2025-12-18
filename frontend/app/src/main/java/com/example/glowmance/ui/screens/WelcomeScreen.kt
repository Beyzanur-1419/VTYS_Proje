package com.example.glowmance.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
// See README.md for instructions on adding custom fonts
private val RalewayFontFamily = FontFamily.SansSerif
private val LoveloFontFamily = FontFamily.Serif

@Composable
fun WelcomeScreen(
    onSignInClick: () -> Unit = {},
    onLogInClick: () -> Unit = {}
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
        
        // Content column with vertical arrangement
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo and title section - positioned higher
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(0.4f)
                    .padding(top = 60.dp),
                verticalArrangement = Arrangement.Top
            ) {
                // Logo with shadow for elegance
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(160.dp)
                        .shadow(8.dp, RoundedCornerShape(80.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Glowmance title with rose-gold shimmer effect
                Box(contentAlignment = Alignment.Center) {
                    // Create shimmer effect with gradient text
                    Text(
                        text = "GLOWMANCE",
                        style = TextStyle(
                            fontFamily = LoveloFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            letterSpacing = 2.sp,
                            brush = roseGoldShimmerGradient
                        )
                    )
                }
            }
            
            // "Let's Get Started" text centered between logo and buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.2f),
                verticalArrangement = Arrangement.Center
            ) {
                // "Let's Get" text
                Text(
                    text = "Let's Get",
                    style = TextStyle(
                        fontFamily = RalewayFontFamily,
                        fontSize = 60.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                )
                
                // "Started!" text on a new line with bolder style and shimmer effect
                Text(
                    text = "Started!",
                    style = TextStyle(
                        fontFamily = RalewayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 60.sp,
                        textAlign = TextAlign.Center,
                        brush = roseGoldShimmerGradient
                    )
                )
            }
            
            // Buttons section
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .weight(0.4f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                
                // Sign In button with gradient background
                val signInGradient = Brush.linearGradient(
                    colors = listOf(RoseGoldLight, RoseGold, RoseGoldDark)
                )
                
                // Custom gradient button with shimmer effect
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp))
                        .background(brush = roseGoldGradient)
                        .clickable { 
                            println("SIGN IN clicked")
                            onSignInClick() 
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SIGN IN",
                        style = TextStyle(
                            fontFamily = RalewayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    )
                }
                
                // Add spacer between buttons
                Spacer(modifier = Modifier.height(10.dp))
                
                // SIGN UP button with transparent background and gradient border
                OutlinedButton(
                    onClick = { 
                        println("SIGN UP clicked")
                        onLogInClick() 
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    border = BorderStroke(
                        width = 2.dp,
                        brush = roseGoldShimmerGradient
                    )
                ) {
                    Text(
                        text = "SIGN UP",
                        style = TextStyle(
                            fontFamily = RalewayFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    GlowmanceTheme {
        WelcomeScreen()
    }
}