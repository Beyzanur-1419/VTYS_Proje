package com.example.glowmance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.glowmance.R

private val RoseGold = Color(0xFFBD8C7D)

@Composable
fun GlowmanceBottomNavigationBar(
    selectedTab: Int, // 0: Home, 1: History, 2: Shop, 3: Profile
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Divider with indicator
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
            // Using a Row with weights to position the indicator correctly
            Row(modifier = Modifier.fillMaxWidth()) {
                // Home (0)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.5.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (selectedTab == 0) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .fillMaxHeight()
                                .background(color = RoseGold)
                        )
                    }
                }
                
                // History (1)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.5.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (selectedTab == 1) {
                         Box(
                            modifier = Modifier
                                .width(60.dp)
                                .fillMaxHeight()
                                .background(color = RoseGold)
                        )
                    }
                }
                
                // Shop (2)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.5.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (selectedTab == 2) {
                         Box(
                            modifier = Modifier
                                .width(60.dp)
                                .fillMaxHeight()
                                .background(color = RoseGold)
                        )
                    }
                }
                
                // Profile (3)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.5.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (selectedTab == 3) {
                         Box(
                            modifier = Modifier
                                .width(60.dp)
                                .fillMaxHeight()
                                .background(color = RoseGold)
                        )
                    }
                }
            }
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
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToHome() }
            ) {
                // Using standard Icon if painter not found, but ProfileScreen implies they exist
                // HomeScreen uses Icons.Default.Home
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if(selectedTab == 0) RoseGold else RoseGold.copy(alpha=0.5f), // Slightly dimmed if not selected? Original code uses RoseGold always
                    modifier = Modifier.size(28.dp)
                )
            }

            // History icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToHistory() }
            ) {
                // ProfileScreen uses R.drawable.ic_history
                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = "History",
                    tint = RoseGold,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Shop icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToShop() }
            ) {
                 // ProfileScreen uses R.drawable.ic_shopping_bag
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
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToProfile() }
            ) {
                 // HomeScreen uses Icons.Default.Person
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
