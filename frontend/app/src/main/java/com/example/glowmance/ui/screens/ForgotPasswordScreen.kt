package com.example.glowmance.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.CircularProgressIndicator
import com.example.glowmance.R
import com.example.glowmance.ui.theme.GlowmanceTheme
import com.example.glowmance.ui.viewmodel.ForgotPasswordViewModel
import kotlinx.coroutines.launch

// Define custom colors (same as other screens for consistency)
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
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBackClick: () -> Unit = {},
    onResetPasswordSuccess: () -> Unit = {}
) {
    // State variables
    var email by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }
    var emailErrorMessage by remember { mutableStateOf("") }
    
    // Observe UI state
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Email validation function
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    // Handle success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("Password reset email sent to $email")
            }
            // Reset state after showing message
            viewModel.resetState()
            onResetPasswordSuccess()
        }
    }
    
    // Handle errors
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
            }
            viewModel.clearError()
        }
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
                .padding(24.dp),
        ) {
            // Back button row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onBackClick() }
                )
            }
            
            // Logo and title section in top right corner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
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

            // Forgot Password heading
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // "FORGOT" text
                Text(
                    text = "FORGOT",
                    style = TextStyle(
                        fontFamily = RalewayFontFamily,
                        fontSize = 40.sp,
                        color = Color.White,
                        textAlign = TextAlign.Start
                    )
                )

                // "PASSWORD?" text with shimmer effect
                Text(
                    text = "PASSWORD?",
                    style = TextStyle(
                        fontFamily = RalewayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        textAlign = TextAlign.Start,
                        brush = roseGoldShimmerGradient
                    )
                )
            }
            
            // Instructions text
            Text(
                text = "Enter your email address and we'll send you instructions to reset your password.",
                style = TextStyle(
                    fontFamily = RalewayFontFamily,
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Email input field
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    // Clear error when user starts typing
                    if (isEmailError) {
                        isEmailError = false
                        emailErrorMessage = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isEmailError) 4.dp else 16.dp),
                label = {
                    Text(
                        text = "Email",
                        style = TextStyle(
                            brush = roseGoldShimmerGradient,
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
                isError = isEmailError,
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
            
            // Error message
            if (isEmailError) {
                Text(
                    text = emailErrorMessage,
                    style = TextStyle(
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontFamily = RalewayFontFamily
                    ),
                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
                )
            }

            // Reset Password button with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .background(brush = roseGoldGradient)
                    .clickable(enabled = !uiState.isLoading) {
                        if (email.isEmpty()) {
                            isEmailError = true
                            emailErrorMessage = "Email cannot be empty"
                        } else if (!isValidEmail(email)) {
                            isEmailError = true
                            emailErrorMessage = "Please enter a valid email address"
                        } else {
                            // Valid email, proceed with reset
                            viewModel.resetPassword(email, onResetPasswordSuccess)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "RESET PASSWORD",
                        style = TextStyle(
                            fontFamily = RalewayFontFamily,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Snackbar for success/error messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
            snackbar = { snackbarData ->
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .shadow(4.dp, RoundedCornerShape(8.dp)),
                    containerColor = RoseGold,
                    contentColor = Color.White,
                    content = {
                        Text(
                            text = snackbarData.visuals.message,
                            style = TextStyle(
                                fontFamily = RalewayFontFamily,
                                fontSize = 14.sp
                            )
                        )
                    }
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    GlowmanceTheme {
        // Preview için mock ViewModel gerekli, şimdilik boş bırakıyoruz
    }
}
