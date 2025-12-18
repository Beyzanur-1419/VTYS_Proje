package com.example.glowmance.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.glowmance.R
import com.example.glowmance.ui.navigation.Screen
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Define custom colors (same as HomeScreen)
private val RoseGold = Color(0xFFBD8C7D)
private val RoseGoldLight = Color(0xFFE0C1B3)
private val RoseGoldDark = Color(0xFF9A6959)

// Define gradient brush (same as HomeScreen)
private val roseGoldGradient = Brush.linearGradient(
    colors = listOf(RoseGoldLight, RoseGold, RoseGoldDark),
    start = Offset(0f, 0f),
    end = Offset(100f, 100f)
)

@Composable
fun CameraScreen(
    navController: NavController,
    viewModel: CameraViewModel = viewModel()
) {
    // Add state for camera preview visibility
    var showCameraPreview by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Reset view model state when entering this screen
    LaunchedEffect(key1 = Unit) {
        viewModel.resetState()
    }
    
    // Check initial permissions
    var hasCameraPermission by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    var hasGalleryPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    // Update ViewModel state
    viewModel.hasCameraPermission = hasCameraPermission
    viewModel.hasGalleryPermission = hasGalleryPermission
    
    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Process the selected image
            viewModel.isLoading = true
            viewModel.processGalleryImage(
                context = context,
                uri = it,
                onSuccess = {
                    viewModel.isLoading = false
                    // Navigate to scanning screen
                    navController.navigate(Screen.FaceScanning.route)
                },
                onFailure = { errorMessage ->
                    viewModel.isLoading = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(errorMessage)
                    }
                }
            )
        }
    }
    
    // Permission launchers
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        viewModel.hasCameraPermission = isGranted
        
        if (isGranted) {
            // Automatically open camera when permission is granted
            viewModel.showCameraPreview = true
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Kamera izni olmadan yüz taraması yapılamaz")
            }
        }
    }
    
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        RequestPermission()
    ) { isGranted ->
        hasGalleryPermission = isGranted
        viewModel.hasGalleryPermission = isGranted
        
        if (isGranted) {
            // If permission granted, open gallery
            photoPickerLauncher.launch(
                "image/*"
            )
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Galeri izni olmadan fotoğraf seçilemez")
            }
        }
    }
    
    // Navigate to scanning screen when face is detected
    if (viewModel.faceDetected) {
        LaunchedEffect(key1 = true) {
            Log.d("CameraScreen", "Face detected, navigating to scanning screen")
            delay(500) // Short delay before navigation
            navController.navigate(Screen.FaceScanning.route) {
                 popUpTo(Screen.Camera.route) { inclusive = false } // Keep camera in stack? No, probably should pop camera or scanning replaces it.
                 // Actually, usually we don't want to go back to camera preview immediately.
            }
            viewModel.showCameraPreview = false // Stop preview
        }
    }
    
    // Camera or selection screen
    Box(modifier = Modifier.fillMaxSize()) {
        // Show camera preview if enabled
        if (viewModel.showCameraPreview) {
            CameraPreview(
                context = context,
                lifecycleOwner = lifecycleOwner,
                onFaceDetected = { 
                    viewModel.faceDetected = true
                },
                onBack = {
                    viewModel.showCameraPreview = false
                }
            )
        }
        // Background image
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Back button
        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Cilt Analizi",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Subtitle
            Text(
                text = "Yüz analizi için bir seçenek belirleyin",
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            // Camera button
            Button(
                onClick = {
                    if (hasCameraPermission) {
                        // Show camera UI and set state to show camera preview
                        viewModel.showCameraPreview = true
                    } else {
                        // Request camera permission
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush = roseGoldGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "Camera",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Kamerayla Tara",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Gallery button
            Button(
                onClick = {
                    // Check for gallery permission
                    val galleryPermission = if (android.os.Build.VERSION.SDK_INT >= 33) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                    
                    if (hasGalleryPermission) {
                        // Launch gallery picker
                        photoPickerLauncher.launch(
                            "image/*"
                        )
                    } else {
                        // Request gallery permission
                        galleryPermissionLauncher.launch(galleryPermission)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(
                            width = 1.dp,
                            brush = roseGoldGradient,
                            shape = RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gallery),
                            contentDescription = "Gallery",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Galeriden Seç",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
        
        // Loading indicator
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RoseGold)
            }
        }
        
        // Error message
        viewModel.errorMessage?.let { error ->
            LaunchedEffect(error) {
                snackbarHostState.showSnackbar(error)
                viewModel.errorMessage = null
            }
        }
        
        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun CameraPreview(
    context: android.content.Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onFaceDetected: () -> Unit,
    onBack: () -> Unit
) {
    // Camera UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Camera preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                val cameraExecutor = Executors.newSingleThreadExecutor()
                
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor, FaceAnalyzer { faces ->
                                if (faces > 0) {
                                    Log.d("CameraScreen", "Face detected! Count: $faces")
                                    onFaceDetected()
                                }
                            })
                        }
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_FRONT_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("CameraScreen", "Camera binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Back button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
    
    // Clean up effect
    DisposableEffect(Unit) {
        onDispose {
            // Clean up resources if needed
        }
    }
}


class FaceAnalyzer(
    private val onFacesDetected: (Int) -> Unit
) : ImageAnalysis.Analyzer {
    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.2f)
            .enableTracking()
            .build()
    )

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )
            
            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    onFacesDetected(faces.size)
                }
                .addOnFailureListener { e ->
                    Log.e("FaceAnalyzer", "Face detection failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}