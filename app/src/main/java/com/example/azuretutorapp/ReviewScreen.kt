package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    role: UserRole,
    currentUserEmail: String,
    courses: List<Course>,
    enrollments: List<Enrollment>,
    reviews: List<CourseReview>,
    onNavigateBack: () -> Unit,
    onAddReview: (CourseReview) -> Unit
) {
    val primaryPurple = Color(0xFF6366F1)
    val backgroundGray = Color(0xFFF9FAFB)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)

    var selectedCourseForReview by remember { mutableStateOf<Course?>(null) }
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Reviews", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = backgroundGray
    ) { paddingValues ->
        if (role == UserRole.STUDENT) {
            val completedEnrollments = enrollments.filter { it.studentEmail == currentUserEmail && it.isCompleted }
            val completedCourses = courses.filter { course -> completedEnrollments.any { it.courseId == course.id } }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("COMPLETED COURSES", fontSize = 12.sp, fontWeight = FontWeight.Black, color = textSecondary, letterSpacing = 1.sp)
                }

                if (completedCourses.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No completed courses found.", color = textSecondary)
                        }
                    }
                }

                items(completedCourses) { course ->
                    val existingReview = reviews.find { it.studentEmail == currentUserEmail && it.courseId == course.id }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(course.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                            Text(course.tutorName, fontSize = 14.sp, color = textSecondary)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (existingReview != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = if (index < existingReview.rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                                            contentDescription = null,
                                            tint = if (index < existingReview.rating) Color(0xFFFFB800) else Color.LightGray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reviewed", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryPurple)
                                }
                                if (existingReview.comment.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(existingReview.comment, fontSize = 14.sp, color = textPrimary)
                                }
                            } else {
                                Button(
                                    onClick = { 
                                        selectedCourseForReview = course
                                        showDialog = true
                                        rating = 5
                                        comment = ""
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryPurple)
                                ) {
                                    Text("POST REVIEW", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Tutor Perspective: View reviews for their courses
            val tutorCourses = courses.filter { it.tutorEmail == currentUserEmail }
            val relevantReviews = reviews.filter { review -> tutorCourses.any { it.id == review.courseId } }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("STUDENT FEEDBACK", fontSize = 12.sp, fontWeight = FontWeight.Black, color = textSecondary, letterSpacing = 1.sp)
                }

                if (relevantReviews.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No reviews received yet.", color = textSecondary)
                        }
                    }
                }

                items(relevantReviews) { review ->
                    val courseName = courses.find { it.id == review.courseId }?.title ?: "Unknown Course"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color(0xFFEEF2FF)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(review.studentName.take(1), fontWeight = FontWeight.Bold, color = primaryPurple)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(review.studentName, fontWeight = FontWeight.Bold, color = textPrimary)
                                    Text(courseName, fontSize = 12.sp, color = textSecondary)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Text(review.date, fontSize = 11.sp, color = textSecondary)
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = if (index < review.rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                                        contentDescription = null,
                                        tint = if (index < review.rating) Color(0xFFFFB800) else Color.LightGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            
                            if (review.comment.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(review.comment, fontSize = 14.sp, color = textPrimary)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedCourseForReview != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Rate this course") },
            text = {
                Column {
                    Text(selectedCourseForReview?.title ?: "", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                                contentDescription = null,
                                tint = if (index < rating) Color(0xFFFFB800) else Color.LightGray,
                                modifier = Modifier.size(32.dp).clickable { rating = index + 1 }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Your thoughts (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onAddReview(
                        CourseReview(
                            studentEmail = currentUserEmail,
                            studentName = if (currentUserEmail == "student@gmail.com") "James Anderson" else currentUserEmail.split("@").firstOrNull()?.capitalize() ?: "Student",
                            courseId = selectedCourseForReview!!.id,
                            rating = rating,
                            comment = comment,
                            date = java.time.LocalDate.now().toString()
                        )
                    )
                    showDialog = false
                }) {
                    Text("SUBMIT", fontWeight = FontWeight.Bold, color = primaryPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("CANCEL", color = textSecondary)
                }
            }
        )
    }
}
