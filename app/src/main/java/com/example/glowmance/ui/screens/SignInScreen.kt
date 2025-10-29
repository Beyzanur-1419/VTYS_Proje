package com.example.glowmance.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Divider
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
private val RalewayFontFamily = FontFamily.SansSerif
private val LoveloFontFamily = FontFamily.Serif

@Composable
fun SignInScreen(
    onSignInClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    // State variables
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

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
                .padding(24.dp),
        ) {
            // Logo and title section in top right corner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp,bottom = 32.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo with shadow for elegance (smaller size)
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(4.dp, RoundedCornerShape(40.dp)),
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

                    // Glowmance title with rose-gold shimmer effect (smaller size)
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
            }

            // Welcome Back heading
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // "WELCOME" text
                Text(
                    text = "WELCOME",
                    style = TextStyle(
                        fontFamily = RalewayFontFamily,
                        fontSize = 40.sp,
                        color = Color.White,
                        textAlign = TextAlign.Start
                    )
                )

                // "BACK!" text with shimmer effect
                Text(
                    text = "BACK!",
                    style = TextStyle(
                        fontFamily = RalewayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        textAlign = TextAlign.Start,
                        brush = roseGoldShimmerGradient
                    )
                )
            }

            // Email input field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                label = {
                    Text(
                        text = "Email",
                        style = TextStyle(
                            brush = roseGoldShimmerGradient, // Rose gold geçişli yazı
                            fontFamily = RalewayFontFamily,
                            fontSize = 14.sp
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = RoseGold
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.Gray,
                    cursorColor = RoseGold,
                    errorCursorColor = Color.Red,
                    focusedIndicatorColor = RoseGold,
                    unfocusedIndicatorColor = RoseGold.copy(alpha = 0.5f),
                    errorIndicatorColor = Color.Red,
                    focusedLabelColor = RoseGold,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    errorLabelColor = Color.Red
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(16.dp)
            )

            // Password input field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                label = {
                    Text(
                        text = "Password",
                        style = TextStyle(
                            brush = roseGoldShimmerGradient, // Rose gold geçişli yazı
                            fontFamily = RalewayFontFamily,
                            fontSize = 14.sp
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon",
                        tint = RoseGold
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible)
                                    R.drawable.ic_visibility
                                else
                                    R.drawable.ic_visibility_off
                            ),
                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                            tint = RoseGold
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.Gray,
                    cursorColor = RoseGold,
                    errorCursorColor = Color.Red,
                    focusedIndicatorColor = RoseGold,
                    unfocusedIndicatorColor = RoseGold.copy(alpha = 0.5f),
                    errorIndicatorColor = Color.Red,
                    focusedLabelColor = RoseGold,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    errorLabelColor = Color.Red
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(16.dp)
            )

            // Remember Me checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = RoseGold,
                        uncheckedColor = RoseGold.copy(alpha = 0.5f),
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "Remember Me",
                    style = TextStyle(
                        brush = roseGoldShimmerGradient,
                        fontSize = 14.sp,
                        fontFamily = RalewayFontFamily
                    )
                )
            }

            // Sign In button with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                        fontSize = 18.sp,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                )
            }

            // Forgot Password link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Forgot Password?",
                    style = TextStyle(
                        color = RoseGold,
                        fontSize = 16.sp,
                        fontFamily = RalewayFontFamily
                    ),
                    modifier = Modifier.clickable { onForgotPasswordClick() }
                )
            }

            Spacer(modifier = Modifier.weight(2f))
            
            // Horizontal divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    thickness = 1.dp,
                    color = RoseGold.copy(alpha = 0.5f)
                )
            }

            // Don't have an account text with Sign up link
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = RalewayFontFamily
                    )
                )

                // Sign up now with shimmer effect
                val signUpText = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            brush = roseGoldShimmerGradient,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Sign up now")
                    }
                }

                Text(
                    text = signUpText,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    GlowmanceTheme {
        SignInScreen()
    }
}