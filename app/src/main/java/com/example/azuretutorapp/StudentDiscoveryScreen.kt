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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDiscoveryScreen(
    onNavigateBack: () -> Unit,
    publishedCourses: List<Course>,
    onEnroll: (Course) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAccount: () -> Unit,
    enrollments: List<Enrollment> = emptyList(),
    reviews: List<CourseReview> = emptyList() // Added reviews parameter
) {
    val primaryPurple = Color(0xFF6366F1)
    val secondPurple = Color(0xFF4F46E5)
    val backgroundWhite = Color(0xFFFFFFFF)
    val surfaceColor = Color(0xFFF9FAFB)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)

    val heroGradient = Brush.verticalGradient(
        colors = listOf(primaryPurple, secondPurple)
    )

    var selectedTab by remember { mutableStateOf("AVAILABLE NOW") }
    
    val availableNowCourses = publishedCourses.filter { !isPastCourse(it.date, it.time) }
    
    val historyCourses = publishedCourses.filter { course -> 
        isPastCourse(course.date, course.time) || 
        enrollments.any { it.courseId == course.id && it.isCompleted } 
    }

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
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            StudentBottomNavigation(
                currentScreen = "Schedule",
                onHomeClick = onNavigateToHome,
                onScheduleClick = {},
                onAccountClick = onNavigateToAccount
            )
        },
        containerColor = surfaceColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(heroGradient)
                        .padding(24.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(
                            text = "DISCOVERY HUB",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "AZURE CLOUD MARKET",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            // Tab Selectors
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .offset(y = (-30).dp)
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf("AVAILABLE NOW", "HISTORY")
                    
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clickable { selectedTab = tab },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) primaryPurple else Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = tab,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Color.White else textSecondary,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // Course List based on tab
            if (selectedTab == "AVAILABLE NOW") {
                items(availableNowCourses, key = { it.id }) { course ->
                    val isEnrolled = enrollments.any { it.courseId == course.id }
                    DiscoveryCourseCard(
                        course = course, 
                        reviews = reviews, 
                        onEnroll = { onEnroll(course) },
                        isEnrolled = isEnrolled
                    )
                }
            } else {
                items(historyCourses, key = { it.id }) { course ->
                    HistoryCourseCard(course = course)
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun HistoryCourseCard(course: Course) {
    val primaryPurple = Color(0xFF6366F1)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon/Image
            Surface(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF9FAFB)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.tutorName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = "${course.category} • ${course.date}",
                    fontSize = 11.sp,
                    color = textSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Review Button
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .height(36.dp)
                    .width(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF9FAFB),
                    contentColor = textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "REVIEW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun DiscoveryCourseCard(
    course: Course, 
    reviews: List<CourseReview>, 
    onEnroll: () -> Unit,
    isEnrolled: Boolean = false
) {
    val primaryPurple = Color(0xFF6366F1)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)

    // Calculate rating for this tutor
    val tutorReviews = reviews.filter { review -> 
        // We match by tutor email if it's available in Course model
        // but since CourseReview only has courseId, we'll match by courseId's tutor
        // In a real app we'd match by tutorId/Email. Let's assume courseId is unique or tutor is consistent.
        // Actually, let's just match reviews for THIS course to keep it simple and accurate for the UI.
        review.courseId == course.id 
    }
    val avgRating = if (tutorReviews.isNotEmpty()) tutorReviews.map { it.rating }.average() else 0.0
    val reviewCount = tutorReviews.size

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column {
            // Subject Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
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
                            contentDescription = course.category,
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
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd),
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = course.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryPurple,
                        letterSpacing = 1.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                // Avatar Placeholder
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF3F4F6)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = textSecondary, modifier = Modifier.size(28.dp))
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.tutorName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimary
                    )
                    
                    Text(
                        text = "${course.category} • ${course.title.uppercase()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryPurple,
                        letterSpacing = 0.5.sp
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                tint = primaryPurple,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = course.date,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = primaryPurple,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = course.time,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
                
                // Rating Badge (Replacing Price Badge)
                Surface(
                    color = Color(0xFFEEF2FF),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (avgRating > 0) String.format("%.1f", avgRating) else "4.5",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = primaryPurple
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Enroll Button - Styled to match mockup
                Button(
                    onClick = {
                        android.util.Log.d(
                            "COURSE_CLICK",
                            "Clicked course: ${course.title} | id=${course.id} | price=${course.price}"
                        )
                        onEnroll()
                    },
                    enabled = !isEnrolled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEnrolled) Color(0xFF10B981) else primaryPurple,
                    disabledContainerColor = Color(0xFF10B981) // Green for completion
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (isEnrolled) "ALREADY ENROLLED" else "ENROLL NOW • ₹${course.price.toInt()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            }
        }
    }
}

}

@Preview
@Composable
fun StudentDiscoveryPreview() {
    val sampleCourses = listOf(
        Course(
            title = "React Hooks Deep Dive",
            tutorName = "Michael Chen",
            category = "PROGRAMMING",
            enrolledCount = 0,
            price = 25.0,
            date = "FEB 17, 2026",
            time = "02:00 PM",
            imageGradientColors = listOf(Color.Blue, Color.Cyan)
        ),
        Course(
            title = "Quantum Physics Basics",
            tutorName = "Emma Williams",
            category = "SCIENCE",
            enrolledCount = 0,
            price = 49.0,
            date = "FEB 20, 2026",
            time = "11:00 AM",
            imageGradientColors = listOf(Color.Green, Color.Yellow)
        )
    )
    StudentDiscoveryScreen(
        onNavigateBack = {},
        publishedCourses = sampleCourses,
        onEnroll = {},
        onNavigateToHome = {},
        onNavigateToAccount = {}
    )
}
