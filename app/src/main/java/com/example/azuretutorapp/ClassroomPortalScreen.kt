package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomPortalScreen(
    course: Course,
    onNavigateBack: () -> Unit
) {
    val primaryBlue = Color(0xFF2E3192) // Deep blue from photo
    val cardBg = Color(0xFFF9FAFB)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val accentBlue = Color(0xFF4F46E5)

    val uriHandler = LocalUriHandler.current

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    
    // Update current time every minute to refresh button state
    LaunchedEffect(Unit) {
        while(true) {
            currentTime = LocalDateTime.now()
            delay(60000)
        }
    }

    // Logic for button activation and session status
    val sessionStatus = remember(currentTime, course.date, course.time, course.durationMins) {
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a", Locale.US)
            val sessionStart = LocalDateTime.parse("${course.date} ${course.time}", formatter)
            val sessionEnd = sessionStart.plusMinutes(course.durationMins.toLong())
            
            when {
                currentTime.isBefore(sessionStart) -> "SCHEDULED"
                currentTime.isAfter(sessionEnd) -> "ENDED"
                else -> "ACTIVE"
            }
        } catch (e: Exception) {
            "ERROR"
        }
    }

    val isClassActive = sessionStatus == "ACTIVE"
    val isSessionEnded = sessionStatus == "ENDED"

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(primaryBlue)
                    .padding(24.dp)
            ) {
                Column {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = course.title,
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 44.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = course.tutorName,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Cards Section
            Column(
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .padding(horizontal = 24.dp)
            ) {
                // Date and Time Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    PortalCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.CalendarToday,
                        label = "DATE",
                        value = course.date
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    PortalCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.Schedule,
                        label = "TIME",
                        value = course.time
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Professor's Message
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF3B82F6), // Vibrant Blue from photo
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "PROFESSOR'S MESSAGE",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "\"${course.professorMessage}\"",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Curriculum Details
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "CURRICULUM DETAILS",
                                color = Color(0xFF3B82F6),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = course.curriculum,
                            color = textSecondary,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Meeting Activation Banner (If inactive or ended)
                if (!isClassActive) {
                    val bannerColor = if (isSessionEnded) Color(0xFFFEF2F2) else Color(0xFFFFFBEB)
                    val bannerTextTint = if (isSessionEnded) Color(0xFFDC2626) else Color(0xFFD97706)
                    val bannerIcon = if (isSessionEnded) Icons.Default.EventBusy else Icons.Default.Lock
                    val bannerText = if (isSessionEnded) "SESSION HAS ENDED" else "MEETING LINK ACTIVATES AT ${course.time}"

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = bannerColor,
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(bannerIcon, contentDescription = null, tint = bannerTextTint, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                bannerText,
                                color = bannerTextTint,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Join Class Button
                Button(
                    onClick = { 
                        if (course.meetingUrl.isNotEmpty()) {
                            try {
                                uriHandler.openUri(course.meetingUrl)
                            } catch (e: Exception) {
                                // Handle invalid URI
                            }
                        }
                    },
                    enabled = isClassActive,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    shape = RoundedCornerShape(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isClassActive) Color(0xFF3B82F6) else Color(0xFFF3F4F6),
                        contentColor = if (isClassActive) Color.White else Color(0xFF9CA3AF),
                        disabledContainerColor = Color(0xFFF3F4F6),
                        disabledContentColor = Color(0xFF9CA3AF)
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                isClassActive -> Icons.Default.VideoCall
                                isSessionEnded -> Icons.Default.Block
                                else -> Icons.Default.VideocamOff
                            },
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = when {
                                isClassActive -> "JOIN CLASS"
                                isSessionEnded -> "SESSION ENDED"
                                else -> "CLASS SCHEDULED"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun PortalCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF9FAFB),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = Color(0xFF3B82F6), 
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                label, 
                color = Color(0xFF3B82F6), 
                fontSize = 10.sp, 
                fontWeight = FontWeight.Black, 
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value, 
                color = Color(0xFF111827), 
                fontSize = 14.sp, 
                fontWeight = FontWeight.Bold
            )
        }
    }
}
