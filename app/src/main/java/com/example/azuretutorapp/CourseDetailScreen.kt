package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorCourseDetailScreen(
    course: Course,
    onNavigateBack: () -> Unit,
    onEditCourse: (Course) -> Unit,
    onDeleteCourse: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val backgroundGray = Color(0xFFF9FAFB)
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val primaryPurple = Color(0xFF6366F1)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = course.title, 
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textPrimary)
                    }
                },
                actions = {
                    var menuExpanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = textPrimary)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Course", color = textPrimary) },
                            onClick = {
                                menuExpanded = false
                                onEditCourse(course)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Course", color = Color.Red) },
                            onClick = {
                                menuExpanded = false
                                showDeleteDialog = true
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = backgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Course Header Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
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
                        Image(
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
                
                // Back Button & Actions overlay already handled by Scafford, but we need consistency
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp), // Apply horizontal padding here
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Course Info Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = "COURSE INFO",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // Grid-like layout for info
                            Row(modifier = Modifier.fillMaxWidth()) {
                                InfoBox(
                                    label = "PRICE",
                                    value = "₹${course.price.toInt()}",
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                InfoBox(
                                    label = "MAX STUDENTS",
                                    value = course.maxStudents.toString(),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                InfoBox(
                                    label = "FREQUENCY",
                                    value = course.frequency.lowercase().replaceFirstChar { it.uppercase() },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                InfoBox(
                                    label = "CATEGORY",
                                    value = course.category,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Upcoming Sessions section
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val now = LocalDateTime.now()
                            val upcomingSessionCount = try {
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a", java.util.Locale.US)
                                val sessionDateTime = LocalDateTime.parse("${course.date} ${course.time}", formatter)
                                val endDateTime = sessionDateTime.plusMinutes(course.durationMins.toLong())
                                if (now.isAfter(endDateTime)) 0 else 1
                            } catch (e: DateTimeParseException) {
                                1
                            }
                            
                            Text(
                                text = "$upcomingSessionCount Upcoming Session${if (upcomingSessionCount == 1) "" else "s"}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Surface(
                                onClick = { /* TODO */ },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = "Schedule",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textSecondary
                                )
                            }
                        }
                        
                        // Removed "See past sessions" option as requested
                    }
                }

                // Date Header
                item {
                    Text(
                        text = "February 2026",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary
                    )
                }

                // Session Card with dynamic status
                item {
                    val now = LocalDateTime.now()
                    val sessionStatus = try {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a", java.util.Locale.US)
                        val sessionDateTime = LocalDateTime.parse("${course.date} ${course.time}", formatter)
                        val endDateTime = sessionDateTime.plusMinutes(course.durationMins.toLong())
                        
                        if (now.isAfter(endDateTime)) "Ended" else "Upcoming"
                    } catch (e: DateTimeParseException) {
                        "Upcoming"
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Date Badge
                            val (month, day) = try {
                                val dateParts = course.date.split("-")
                                val m = when(dateParts[1]) {
                                    "01" -> "Jan"; "02" -> "Feb"; "03" -> "Mar"; "04" -> "Apr"
                                    "05" -> "May"; "06" -> "Jun"; "07" -> "Jul"; "08" -> "Aug"
                                    "09" -> "Sep"; "10" -> "Oct"; "11" -> "Nov"; "12" -> "Dec"
                                    else -> "Feb"
                                }
                                Pair(m, dateParts[2])
                            } catch (e: Exception) {
                                Pair("Feb", "16")
                            }

                            Surface(
                                modifier = Modifier.size(64.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF1F5F9)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(month, fontSize = 12.sp, color = textSecondary)
                                    Text(day, fontSize = 24.sp, fontWeight = FontWeight.Black, color = textPrimary)
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = course.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                                Text(
                                    text = "HOSTED BY ${course.tutorName.uppercase()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textSecondary,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = if (sessionStatus == "Ended") Color(0xFFECFDF5) else Color(0xFFEEF2FF),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = sessionStatus,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sessionStatus == "Ended") Color(0xFF10B981) else primaryPurple,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        
                        // 3-dots moved to top bar
                    }
                }
            }
        }

        if (showDeleteDialog) {
            DeleteCourseDialog(
                courseTitle = course.title,
                onConfirm = {
                    showDeleteDialog = false
                    onDeleteCourse(course.id)
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

@Composable
fun InfoBox(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )
        }
    }
}
