package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    enrolledCourses: List<Course> = emptyList(),
    onNavigateToSchedule: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToBilling: () -> Unit,
    onCourseClick: (Course) -> Unit, // New callback
    onMarkCompleted: (String) -> Unit, // New callback
    onLogout: () -> Unit
) {
    val primaryPurple = Color(0xFF6366F1)
    val secondPurple = Color(0xFF8B5CF6)
    val backgroundWhite = Color(0xFFFFFFFF)
    val surfaceColor = Color(0xFFF9FAFB)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)

    val upcomingSessions = enrolledCourses.filter { !isPastCourse(it.date, it.time) }
    val pastSessions = enrolledCourses.filter { isPastCourse(it.date, it.time) }

    val welcomeGradient = Brush.verticalGradient(
        colors = listOf(primaryPurple, secondPurple)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = primaryPurple
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Tutor Hub",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            StudentBottomNavigation(currentScreen = "Home", onHomeClick = {}, onScheduleClick = onNavigateToSchedule, onAccountClick = onNavigateToAccount)
        },
        containerColor = surfaceColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(welcomeGradient)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Welcome Back!",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "AZURE LEARNING CLOUD",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            // Billing & Payments Quick Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .offset(y = (-40).dp)
                        .clickable { onNavigateToBilling() },
                    shape = RoundedCornerShape(48.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFEEF2FF)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = primaryPurple,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "BILLING & PAYMENTS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = textPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                "TRANSACTION HISTORY • RECEIPTS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = textSecondary, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // My Schedule Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Schedule",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "${enrolledCourses.size} courses enrolled • ${upcomingSessions.size} sessions upcoming",
                            fontSize = 14.sp,
                            color = textSecondary
                        )
                    }
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = textPrimary, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }

            if (enrolledCourses.isEmpty()) {
                // Empty State
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NO ACTIVE COURSES YET",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textSecondary.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "DISCOVER SESSIONS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = primaryPurple,
                            modifier = Modifier.clickable { onNavigateToSchedule() },
                            style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                        )
                    }
                }
            } else {
                // Render UPCOMING sessions
                if (upcomingSessions.isNotEmpty()) {
                    item {
                        Text(
                            text = "UPCOMING",
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = primaryPurple,
                            letterSpacing = 1.sp
                        )
                    }
                    items(upcomingSessions) { course ->
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                        ) {
                            CourseCard(course, onClick = { onCourseClick(course) })
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onMarkCompleted(course.id) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryPurple)
                            ) {
                                Text("MARK CLASS COMPLETED", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                // Render PAST sessions
                if (pastSessions.isNotEmpty()) {
                    item {
                        Text(
                            text = "PAST SESSIONS",
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = textSecondary,
                            letterSpacing = 1.sp
                        )
                    }
                    items(pastSessions) { course ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                                .alpha(0.6f) // De-emphasize past sessions
                        ) {
                            CourseCard(course, onClick = { onCourseClick(course) })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun StudentDashboardPreview() {
    StudentDashboardScreen(
        onNavigateToSchedule = {},
        onNavigateToAccount = {},
        onNavigateToBilling = {},
        onCourseClick = {},
        onMarkCompleted = {},
        onLogout = {}
    )
}
