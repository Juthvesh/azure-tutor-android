package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.*
import java.util.UUID
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * Checks if a course is in the past based on its date and time strings.
 * Supports multiple formats to handle both user-input and mock data.
 */
fun isPastCourse(date: String, time: String): Boolean {
    val now = LocalDateTime.now()
    
    val dateParsers = listOf(
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),        // ISO/Mock - Priority
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),        // UI Input
        DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH), // Preview mock
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    )
    
    val timeParsers = listOf(
        DateTimeFormatter.ofPattern("HH:mm"),             // 24h Created
        DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH), // AM/PM Mock
        DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)   // Single digit hour
    )
    
    var parsedDate: java.time.LocalDate? = null
    for (parser in dateParsers) {
        try {
            parsedDate = java.time.LocalDate.parse(date.trim(), parser)
            break
        } catch (e: DateTimeParseException) { continue }
    }
    
    var parsedTime: java.time.LocalTime? = null
    for (parser in timeParsers) {
        try {
            parsedTime = java.time.LocalTime.parse(time.trim().uppercase(), parser)
            break
        } catch (e: DateTimeParseException) { continue }
    }
    
    // If we can't parse even the date, we assume it's valid to show (safety first)
    if (parsedDate == null) return false
    
    // If time is missing or unparseable, just compare dates
    if (parsedTime == null) {
        return parsedDate.isBefore(now.toLocalDate())
    }
    
    val courseDateTime = LocalDateTime.of(parsedDate, parsedTime)
    return courseDateTime.isBefore(now)
}

// User Roles
enum class UserRole {
    STUDENT,
    TUTOR
}

// Consolidated Data Model for Course
data class Course(
    val id: String = UUID.randomUUID().toString(),
    val backendId: Int? = null, // Integer ID from the backend database (used for API calls)
    val title: String,
    val tutorName: String = "Unknown Tutor",
    val tutorEmail: String = "", // Added for ownership tracking
    val category: String,
    val enrolledCount: Int,
    val price: Double,
    val date: String,
    val time: String,
    val imageGradientColors: List<Color>,
    val durationMins: Int = 60,
    val meetingUrl: String = "",
    val classType: String = "1-ON-1",
    val frequency: String = "WEEKLY",
    val maxStudents: Int = 1,
    val curriculum: String = "Master core concepts and advanced techniques in this comprehensive session.",
    val professorMessage: String = "Welcome to the session!",
    val imageUrl: String? = null
)

// Enrollment tracking
data class Enrollment(
    val studentEmail: String,
    val courseId: String,
    val enrollmentDate: String,
    val isCompleted: Boolean = false // Track if the course is finished
)

// Review model
data class CourseReview(
    val id: String = UUID.randomUUID().toString(),
    val studentEmail: String,
    val studentName: String,
    val courseId: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val date: String
)

// Shared Bottom Navigation for Tutors
@Composable
fun UnifiedBottomNavigation(
    currentScreen: String,
    onNavigateToDashboard: () -> Unit,
    onNavigateToEarnings: () -> Unit,
    onNavigateToAccount: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Dashboard", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            selected = currentScreen == "Dashboard",
            onClick = onNavigateToDashboard,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6366F1),
                selectedTextColor = Color(0xFF6366F1),
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
            label = { Text("Earnings", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            selected = currentScreen == "Earnings",
            onClick = onNavigateToEarnings,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6366F1),
                selectedTextColor = Color(0xFF6366F1),
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Account", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            selected = currentScreen == "Account",
            onClick = onNavigateToAccount,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6366F1),
                selectedTextColor = Color(0xFF6366F1),
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}

// Shared Bottom Navigation for Students
@Composable
fun StudentBottomNavigation(
    currentScreen: String,
    onHomeClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(90.dp),
        color = Color.White,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StudentNavItem("Home", Icons.Default.Home, currentScreen == "Home", onHomeClick)
            StudentNavItem("Schedule", Icons.Default.CalendarToday, currentScreen == "Schedule", onScheduleClick)
            StudentNavItem("Account", Icons.Default.PersonOutline, currentScreen == "Account", onAccountClick)
        }
    }
}

@Composable
fun StudentNavItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val primaryPurple = Color(0xFF6366F1)
    val textSecondary = Color(0xFF6B7280)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(3.dp)
                    .background(primaryPurple, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) primaryPurple else textSecondary,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) primaryPurple else textSecondary
        )
    }
}

@Composable
fun DeleteCourseDialog(
    courseTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon Header
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFFEF2F2), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Delete Course?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Are you sure you want to delete \"$courseTitle\"? This action cannot be undone and will remove all associated student data.",
                    fontSize = 15.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Delete Button
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    elevation = ButtonDefaults.buttonColors().let { ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp) }
                ) {
                    Text("Yes, Delete Course", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Cancel Button
                Surface(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF3F4F6)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("No, Keep Course", color = Color(0xFF374151), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomTimePickerPopup(
    selectedTime: String,
    onTimeSelected: (String) -> Unit
) {
    val currentParts = selectedTime.split(":")
    val initialHour = try { currentParts[0].toInt() } catch (e: Exception) { 10 }
    val initialMin = try { currentParts[1].toInt() } catch (e: Exception) { 0 }
    
    var tempHour by remember { mutableIntStateOf(initialHour) }
    var tempMin by remember { mutableIntStateOf(initialMin) }
    
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = initialHour)
    val minState = rememberLazyListState(initialFirstVisibleItemIndex = initialMin)

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shadowElevation = 12.dp,
        modifier = Modifier.width(220.dp)
    ) {
        Column {
            Row(modifier = Modifier.height(260.dp).padding(horizontal = 8.dp)) {
                // Hours (00-23)
                Box(modifier = Modifier.weight(1f)) {
                    androidx.compose.foundation.lazy.LazyColumn(state = hourState, horizontalAlignment = Alignment.CenterHorizontally) {
                        items(24) { h ->
                            val hourStr = h.toString().padStart(2, '0')
                            val isSelected = h == tempHour
                            TimeItemComp(hourStr, isSelected) { 
                                tempHour = h
                            }
                        }
                    }
                }
                
                // Divider
                Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFFF3F4F6)))
                
                // Minutes (00-59)
                Box(modifier = Modifier.weight(1f)) {
                    androidx.compose.foundation.lazy.LazyColumn(state = minState, horizontalAlignment = Alignment.CenterHorizontally) {
                        items(60) { m ->
                            val minStr = m.toString().padStart(2, '0')
                            val isSelected = m == tempMin
                            TimeItemComp(minStr, isSelected) { 
                                tempMin = m
                            }
                        }
                    }
                }
            }
            
            // Set Time Button
            Button(
                onClick = { 
                    onTimeSelected("${tempHour.toString().padStart(2, '0')}:${tempMin.toString().padStart(2, '0')}") 
                },
                modifier = Modifier.fillMaxWidth().padding(12.dp).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
            ) {
                Text("SET TIME", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            
            // 24H Label
            Box(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF9FAFB)).padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("24-HOUR FORMAT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun TimeItemComp(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
        shape = RoundedCornerShape(4.dp),
        color = if (isSelected) Color(0xFF1D78FF) else Color.Transparent,
        onClick = onClick
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 18.sp
        )
    }
}

@Composable
fun VerificationTextField(
    label: String, 
    value: String, 
    onValueChange: (String) -> Unit, 
    placeholder: String,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    leadingIcon: ImageVector? = null,
    suffixRow: @Composable (RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label, 
                fontSize = 11.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color.Gray, 
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827) // Darkest gray/black
            ),
            placeholder = { Text(placeholder, color = Color(0xFF9CA3AF)) },
            leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null, tint = Color(0xFF6366F1).copy(alpha = 0.5f), modifier = Modifier.size(18.dp)) } },
            trailingIcon = trailingIcon?.let { { 
                Icon(
                    it, 
                    contentDescription = null, 
                    tint = Color.Black, 
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(enabled = onTrailingIconClick != null) { onTrailingIconClick?.invoke() }
                ) 
            } },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(20.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF9FAFB),
                unfocusedContainerColor = Color(0xFFF9FAFB),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(20.dp),
            suffix = suffixRow?.let { { Row { it() } } }
        )
    }
}

@Composable
fun SubjectPopupMenu(
    selectedSubject: String,
    onSubjectSelected: (String) -> Unit
) {
    val subjects = listOf("Mathematics", "Physics", "Chemistry", "English", "Biology", "Computer Science", "Music", "Arts", "OTHER (TYPE CUSTOM)")
    
    Surface(
        color = Color(0xFF4B5563), // Dark gray background matching image
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp,
        modifier = Modifier.width(280.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            subjects.forEach { subject ->
                val isSelected = subject == selectedSubject || (subject == "OTHER (TYPE CUSTOM)" && !subjects.dropLast(1).contains(selectedSubject))
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { 
                            if (subject == "OTHER (TYPE CUSTOM)") {
                                onSubjectSelected("") // Clear to let them type
                            } else {
                                onSubjectSelected(subject) 
                            }
                        },
                    color = if (isSelected) Color(0xFF3B82F6) else Color.Transparent, // Blue highlight
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Outlined.CheckCircle, 
                                contentDescription = null, 
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Text(
                            text = subject,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text
) {
    val containerColor = Color(0xFFF9FAFB)
    val contentColor = Color(0xFF6B7280)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        ),
        placeholder = { Text(text = placeholder, color = contentColor) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor
            )
        },
        trailingIcon = if (isPassword && onPasswordToggle != null) {
            {
                IconButton(onClick = onPasswordToggle) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = contentColor
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
        ),
        singleLine = true
    )
}
