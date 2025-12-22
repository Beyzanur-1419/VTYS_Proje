package com.example.glowmance.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.launch

private val RoseGold = Color(0xFFBD8C7D)
private val RoseGoldLight = Color(0xFFE0C1B3)
private val RoseGoldDark = Color(0xFF9A6959)

private val RalewayFontFamily = FontFamily.SansSerif

@Composable
fun AccountSettingsScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit // Used if account is deleted
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val authApi = com.example.glowmance.data.api.RetrofitClient.authApi
    val userPreferences = com.example.glowmance.data.UserPreferences.getInstance(context)

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { oldPass, newPass ->
                if (newPass.length < 6) {
                    Toast.makeText(context, "Yeni şifre en az 6 karakter olmalıdır.", Toast.LENGTH_LONG).show()
                } else {
                    scope.launch {
                        val token = userPreferences.getAuthToken()
                        if (token != null) {
                            try {
                                 val response = authApi.changePassword("Bearer $token", com.example.glowmance.data.model.ChangePasswordRequest(oldPass, newPass))
                                 if (response.isSuccessful) {
                                      Toast.makeText(context, "Şifre başarıyla güncellendi", Toast.LENGTH_SHORT).show()
                                      showChangePasswordDialog = false
                                 } else {
                                      val errorBody = response.errorBody()?.string() ?: response.message()
                                      Toast.makeText(context, "Hata: $errorBody", Toast.LENGTH_LONG).show()
                                 }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Hesabı Sil",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RalewayFontFamily
                )
            },
            text = {
                Text(
                    text = "Hesabınızı silmek istediğinize emin misiniz? Bu işlem geri alınamaz ve tüm verileriniz kaybolur.",
                    fontFamily = RalewayFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val token = userPreferences.getAuthToken()
                            if (token != null) {
                                try {
                                    val response = authApi.deleteAccount("Bearer $token")
                                    if (response.isSuccessful) {
                                        userPreferences.clearAuth()
                                        onLogout()
                                        Toast.makeText(context, "Hesabınız silindi.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Silme başarısız: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Sil", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal", color = RoseGold)
                }
            },
            containerColor = Color.White
        )
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
                .verticalScroll(rememberScrollState())
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
                    text = "Hesap Ayarları",
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    fontFamily = RalewayFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Options
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Change Password
                AccountActionItem(
                    title = "Şifre Değiştir",
                    icon = R.drawable.ic_lock,
                    onClick = { showChangePasswordDialog = true }
                )

                // Delete Account
                AccountActionItem(
                    title = "Hesabı Sil",
                    icon = R.drawable.ic_delete,
                    textColor = Color.Red,
                    onClick = { showDeleteDialog = true }
                )
            }
        }
    }
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Şifre Değiştir", fontFamily = RalewayFontFamily) },
        text = {
            Column {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Mevcut Şifre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Yeni Şifre") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(oldPassword, newPassword) },
                colors = ButtonDefaults.buttonColors(containerColor = RoseGold)
            ) {
                Text("Güncelle", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = RoseGold)
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun AccountActionItem(
    title: String,
    icon: Int,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E).copy(alpha = 0.8f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Text(
                text = title,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                fontFamily = RalewayFontFamily,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = if(textColor == Color.Red) Color.Red else RoseGold,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
