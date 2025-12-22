package com.example.glowmance.ui.screens

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.glowmance.R
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CameraScreen(
    onImageCaptured: (Uri) -> Unit,
    onBackClick: () -> Unit,
    viewModel: CameraViewModel = viewModel()
) {
    val context = LocalContext.current
    
    // Create a temporary file for the camera image
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            Log.d("CameraScreen", "Image captured successfully: $tempImageUri")
             // Move to permanent storage or just pass the URI
            onImageCaptured(tempImageUri!!)
        } else {
            Log.d("CameraScreen", "Camera capture cancelled")
        }
    }

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.d("CameraScreen", "Image selected from gallery: $uri")
            onImageCaptured(uri)
        }
    }

    // Function to create temp file and launch camera
    fun launchCamera() {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageFile = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                context.cacheDir
            )
            // Get URI using FileProvider
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
            tempImageUri = uri
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            Log.e("CameraScreen", "Error launching camera", e)
            Toast.makeText(context, "Kamera açılamadı: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(context, "Kamera izni gerekli", Toast.LENGTH_LONG).show()
        }
    }

    // UI
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        // Gradient Brushes (Same as HomeScreen)
        val RoseGoldLight = Color(0xFFE0C1B3)
        val RoseGold = Color(0xFFBD8C7D)
        val RoseGoldDark = Color(0xFF9A6959)
        val roseGoldGradient = androidx.compose.ui.graphics.Brush.linearGradient(
            colors = listOf(RoseGoldLight, RoseGold, RoseGoldDark),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(100f, 100f)
        )

        // Back Button (Top Left)
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(
                Icons.Default.ArrowBack, 
                contentDescription = "Geri", 
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Text
            Text(
                text = "Cilt Analizi",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Analiz için fotoğraf kaynağını seçin",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Open Camera Button
            Button(
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 8.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp) // Reset padding for gradient box
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(roseGoldGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "Kamera",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Kamerayı Aç", 
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gallery Button
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 8.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gallery), 
                        contentDescription = "Galeri", 
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Galeriden Seç", 
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}