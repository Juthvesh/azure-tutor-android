package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AccountScreen(
    role: UserRole = UserRole.TUTOR,
    userName: String = "James Anderson",
    userEmail: String = "jamesanderson@demo.wise.live",
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToEarnings: () -> Unit, // This might be onNavigateToSchedule for student
    onNavigateToEditProfile: () -> Unit,
    onNavigateToReviews: () -> Unit, // New callback
    onLogout: () -> Unit
) {
    val backgroundGray = Color(0xFFF9FAFB)
    val primaryPurple = Color(0xFF6366F1)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)

    Scaffold(
        bottomBar = {
            if (role == UserRole.TUTOR) {
                UnifiedBottomNavigation(
                    currentScreen = "Account",
                    onNavigateToDashboard = onNavigateToDashboard,
                    onNavigateToEarnings = onNavigateToEarnings,
                    onNavigateToAccount = {}
                )
            } else {
                StudentBottomNavigation(
                    currentScreen = "Account",
                    onHomeClick = onNavigateToDashboard,
                    onScheduleClick = onNavigateToEarnings, // Reusing this for Schedule in Student case
                    onAccountClick = {}
                )
            }
        },
        containerColor = backgroundGray
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Section
            item {
                AccountHeader(onNavigateBack)
            }

            // My Profile Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ProfileCard(
                    name = userName,
                    email = userEmail,
                    role = if (role == UserRole.STUDENT) "Student" else "Tutor",
                    onEditProfileClick = onNavigateToEditProfile
                )
            }

            // Additional Options
            item {
                Text(
                    text = "Account Options",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondary,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp)
                )
                
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AccountOptionItem(
                        title = "Reviews",
                        icon = Icons.Outlined.StarOutline,
                        onClick = onNavigateToReviews
                    )
                    AccountOptionItem(
                        title = "Privacy Policy",
                        icon = Icons.Outlined.Description
                    )
                    AccountOptionItem(
                        title = "Help & Support",
                        icon = Icons.Default.HelpOutline
                    )
                    AccountOptionItem(
                        title = "Log out",
                        icon = Icons.AutoMirrored.Filled.Logout,
                        onClick = onLogout,
                        isDestructive = true
                    )
                }
            }
            
            // Footer Version Info
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "v259.1202.1849",
                        color = textSecondary,
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync",
                        tint = textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun AccountHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6366F1), Color(0xFF4F46E5))
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Description, // Placeholder for Logo
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Tutor Hub",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ProfileCard(name: String, email: String, role: String, onEditProfileClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF7C3AED), Color(0xFF6366F1))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = if (name.isNotBlank()) name.take(1).uppercase() else "?"
                    Text(initial, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Column {
                    Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    Text(email, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFFEF9C3), // Yellowish badge
                        shape = RoundedCornerShape(50),
                    ) {
                        Text(
                            text = role,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF854D0E),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onEditProfileClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
            ) {
                Text("Edit Profile", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AccountOptionItem(
    title: String, 
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit = {},
    isDestructive: Boolean = false
) {
    val contentColor = if (isDestructive) Color(0xFFEF4444) else Color(0xFF374151)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Medium, color = contentColor, modifier = Modifier.weight(1f))
            if (!isDestructive) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
            }
        }
    }
}



@Preview
@Composable
fun AccountScreenPreview() {
    AccountScreen(
        onNavigateBack = {},
        onNavigateToDashboard = {}, 
        onNavigateToEarnings = {}, 
        onNavigateToEditProfile = { },
        onNavigateToReviews = { },
        onLogout = { }
    )
}
