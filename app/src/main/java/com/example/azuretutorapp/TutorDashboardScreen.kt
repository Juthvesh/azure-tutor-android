package com.example.azuretutorapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID



@Composable
fun TutorDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRevenue: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToVerifyIdentity: () -> Unit,
    onNavigateToCreateCourse: () -> Unit,
    onCourseClick: (Course) -> Unit, // New callback
    userName: String = "Tutor", // Added for personalization
    publishedCourses: List<Course>,
    isRazorpayConnected: Boolean,
    totalEarnings: Double = 0.0 // Added for dynamic earnings
) {
    val backgroundGray = Color(0xFFF9FAFB)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val primaryPurple = Color(0xFF6366F1)
    
    // No local courses here anymore, using passed publishedCourses

    Scaffold(
        bottomBar = { 
            UnifiedBottomNavigation(
                currentScreen = "Dashboard",
                onNavigateToDashboard = {},
                onNavigateToEarnings = onNavigateToRevenue,
                onNavigateToAccount = onNavigateToAccount
            )
        },
        containerColor = backgroundGray
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top
        ) {
            // Header
            item {
                DashboardHeader(userName)
            }

            // Payout Verification Section (Purple Banner) - Only show if not connected
            if (!isRazorpayConnected) {
                item {
                    PayoutVerificationBanner(onNavigateToVerifyIdentity)
                }
            }

            // Earnings & Status Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        title = "EARNINGS",
                        value = "₹${"%,.0f".format(totalEarnings)}",
                        icon = Icons.Default.ShowChart, // Updated to ShowChart icon for earnings
                        iconColor = Color(0xFF6366F1),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "RAZORPAY STATUS",
                        value = if (isRazorpayConnected) "Active" else "Pending",
                        icon = Icons.Default.Security, 
                        iconColor = if (isRazorpayConnected) Color(0xFF10B981) else primaryPurple,
                        valueColor = if (isRazorpayConnected) Color(0xFF10B981) else Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Management Header
            item {
                Row(
                   modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                   horizontalArrangement = Arrangement.SpaceBetween,
                   verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MANAGEMENT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        letterSpacing = 1.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF6366F1), CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE SYSTEM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6366F1)
                        )
                    }
                }
            }

            // Management Actions
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ManagementItem(
                        title = "Create New Course",
                        subtitle = "LAUNCH PREMIUM SESSIONS",
                        icon = Icons.Default.Add,
                        iconBg = Color(0xFF6366F1),
                        hasLock = !isRazorpayConnected,
                        onClick = onNavigateToCreateCourse
                    )
                    ManagementItem(
                        title = "Verification Settings",
                        subtitle = "IDENTITY & PAYOUT INFO",
                        icon = Icons.Default.Security,
                        iconBg = Color(0xFFECFDF5),
                        iconTint = Color(0xFF10B981),
                        onClick = onNavigateToVerifyIdentity
                    )
                }
            }

            // Published Portfolio Header
            item {
                Text(
                    text = "PUBLISHED PORTFOLIO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 32.dp, bottom = 16.dp)
                )
            }

            // Existing Course Cards
            items(publishedCourses) { course ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    CourseCard(course, onClick = { onCourseClick(course) })
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun DashboardHeader(name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF7C3AED), Color(0xFF4F46E5))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Book, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("Hi, $name", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("ENTERPRISE PORTAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun PayoutVerificationBanner(onVerifyClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Text("PAYOUT VERIFICATION", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Account Restricted", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFF59E0B), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color.White) // Lock icon placeholder
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Action Required", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            "Complete your Razorpay identity verification\nto publish paid courses.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onVerifyClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("VERIFY IDENTITY", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("→", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, iconColor: Color, modifier: Modifier = Modifier, valueColor: Color = Color(0xFF111827)) {
    Card(
        modifier = modifier.height(150.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Box(
                modifier = Modifier.size(40.dp).background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
        }
    }
}

@Composable
fun ManagementItem(title: String, subtitle: String, icon: ImageVector, iconBg: Color, iconTint: Color = Color.White, hasLock: Boolean = false, onClick: () -> Unit = {}) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(88.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).background(iconBg, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827))
                Text(subtitle, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
            }
            if (hasLock) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp).align(Alignment.Top))
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFFE5E7EB))
        }
    }
}

@Composable
fun CourseCard(course: Course, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Subject Image Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                if (course.imageUrl != null) {
                    val bitmap = remember(course.imageUrl) {
                        try {
                            android.graphics.BitmapFactory.decodeFile(course.imageUrl).asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (bitmap != null) {
                        androidx.compose.foundation.Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(course.imageGradientColors))
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(course.imageGradientColors))
                    )
                }

                // Overlay Badge
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = course.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Content
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = course.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${course.enrolledCount} ENROLLED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9CA3AF),
                                letterSpacing = 1.sp
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "•", color = Color(0xFFE5E7EB))
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = "₹",
                                fontSize = 13.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = " ${course.price.toInt()}",
                                fontSize = 13.sp,
                                color = Color(0xFF111827),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Footer: Date and Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = Color(0xFF818CF8),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = course.date,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4B5563),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF818CF8),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = course.time,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4B5563),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    // Forward Action
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier
                            .background(Color(0xFFEEF2FF), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Details",
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}



@Preview
@Composable
fun TutorDashboardPreview() {
    TutorDashboardScreen(
        onNavigateBack = {}, 
        onNavigateToRevenue = {}, 
        onNavigateToAccount = {}, 
        onNavigateToVerifyIdentity = {}, 
        onNavigateToCreateCourse = {},
        onCourseClick = {},
        publishedCourses = emptyList(),
        isRazorpayConnected = false
    )
}
