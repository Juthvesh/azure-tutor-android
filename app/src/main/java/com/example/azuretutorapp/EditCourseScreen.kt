package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCourseScreen(
    initialCourse: Course,
    onNavigateBack: () -> Unit,
    onCourseUpdated: (Course) -> Unit
) {
    val backgroundGray = Color(0xFFF9FAFB)
    val primaryPurple = Color(0xFF6366F1)

    var courseTitle by remember { mutableStateOf(initialCourse.title) }
    var startDate by remember { mutableStateOf(initialCourse.date) }
    // Convert expected 12h format back to 24h for the time picker if needed, 
    // or just leave it as string. We'll leave it as string for simplicity to match CreateCourseScreen.
    var startTime by remember { mutableStateOf(initialCourse.time) }
    var subjectArea by remember { mutableStateOf(initialCourse.category.lowercase().replaceFirstChar { it.uppercase() }) }
    var durationMins by remember { mutableStateOf(initialCourse.durationMins.toString()) }
    var meetingUrl by remember { mutableStateOf(initialCourse.meetingUrl) }
    var sessionPrice by remember { mutableStateOf(initialCourse.price.toInt().toString()) }
    var selectedClassType by remember { mutableStateOf(initialCourse.classType) }
    var selectedFrequency by remember { mutableStateOf(initialCourse.frequency) }
    var curriculum by remember { mutableStateOf(initialCourse.curriculum) }
    var professorMessage by remember { mutableStateOf(initialCourse.professorMessage) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showSubjectDropdown by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis() - 86400000 // Today or future
            }
        }
    )

    Scaffold(
        containerColor = backgroundGray
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            item {
                EditCourseHeader(onNavigateBack)
            }

            // Basic Details Card
            item {
                SectionCard(
                    icon = Icons.Default.MenuBook,
                    title = "Basic Details"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        VerificationTextField(
                            label = "COURSE TITLE",
                            value = courseTitle,
                            onValueChange = { courseTitle = it },
                            placeholder = "e.g. Quantum Physics 101"
                        )
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                VerificationTextField(
                                    label = "START DATE",
                                    value = startDate,
                                    onValueChange = { startDate = it },
                                    placeholder = "YYYY-MM-DD",
                                    trailingIcon = Icons.Default.CalendarMonth,
                                    onTrailingIconClick = { showDatePicker = !showDatePicker },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (showDatePicker) {
                                    DatePickerDialog(
                                        onDismissRequest = { showDatePicker = false },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                datePickerState.selectedDateMillis?.let { mills ->
                                                    val selectedDate = Instant.ofEpochMilli(mills)
                                                        .atZone(ZoneId.systemDefault())
                                                        .toLocalDate()
                                                    startDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                }
                                                showDatePicker = false
                                            }) {
                                                Text("OK", color = primaryPurple, fontWeight = FontWeight.Bold)
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showDatePicker = false }) {
                                                Text("CANCEL", color = Color.Gray)
                                            }
                                        }
                                    ) {
                                        DatePicker(state = datePickerState)
                                    }
                                }
                            }
                            
                            Box(modifier = Modifier.weight(1f)) {
                                VerificationTextField(
                                    label = "START TIME",
                                    value = startTime,
                                    onValueChange = { startTime = it },
                                    placeholder = "00:00",
                                    trailingIcon = Icons.Default.Schedule,
                                    onTrailingIconClick = { showTimePicker = !showTimePicker },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (showTimePicker) {
                                    Popup(
                                        onDismissRequest = { showTimePicker = false },
                                        offset = androidx.compose.ui.unit.IntOffset(0, 150)
                                    ) {
                                        CustomTimePickerPopup(
                                            selectedTime = startTime,
                                            onTimeSelected = { 
                                                startTime = it
                                                showTimePicker = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                VerificationTextField(
                                    label = "SUBJECT AREA",
                                    value = subjectArea,
                                    onValueChange = { 
                                        subjectArea = it
                                        // Don't auto-close if typing
                                    },
                                    placeholder = "Select or Type Subject",
                                    trailingIcon = if (showSubjectDropdown) Icons.Default.ArrowUpward else Icons.Default.ArrowDropDown,
                                    onTrailingIconClick = { showSubjectDropdown = !showSubjectDropdown },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (showSubjectDropdown) {
                                    Popup(
                                        onDismissRequest = { showSubjectDropdown = false },
                                        offset = androidx.compose.ui.unit.IntOffset(0, 150)
                                    ) {
                                        SubjectPopupMenu(
                                            selectedSubject = subjectArea,
                                            onSubjectSelected = {
                                                subjectArea = it
                                                showSubjectDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = " ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Transparent,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(20.dp))
                                        .background(Color.White, RoundedCornerShape(20.dp))
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Schedule, 
                                        contentDescription = null, 
                                        tint = Color(0xFF818CF8),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    BasicTextField(
                                        value = durationMins,
                                        onValueChange = { if (it.length <= 3) durationMins = it },
                                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                        ),
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            textAlign = TextAlign.Center
                                        ),
                                        singleLine = true,
                                        cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFF818CF8))
                                    )
                                    Text(
                                        "MINS",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Virtual Classroom Card
            item {
                SectionCard(
                    icon = Icons.Default.Language,
                    title = "Virtual Classroom"
                ) {
                    Column {
                        VerificationTextField(
                            label = "MEETING URL (ZOOM / G-MEET)",
                            value = meetingUrl,
                            onValueChange = { meetingUrl = it },
                            placeholder = "https://zoom.us/j/..."
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Students will use this link to join your sessions.",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // Course Content Card
            item {
                SectionCard(
                    icon = Icons.Default.MenuBook,
                    title = "Course Content"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        VerificationTextField(
                            label = "PROFESSOR'S MESSAGE",
                            value = professorMessage,
                            onValueChange = { professorMessage = it },
                            placeholder = "e.g. Welcome to the session! Be ready with your notebooks."
                        )
                        
                        VerificationTextField(
                            label = "CURRICULUM DETAILS",
                            value = curriculum,
                            onValueChange = { curriculum = it },
                            placeholder = "e.g. Master multi-variable calculus, partial derivatives, and advanced integration techniques.",
                            modifier = Modifier.heightIn(min = 100.dp)
                        )
                    }
                }
            }

            // Class Setup Card
            item {
                SectionCard(
                    icon = Icons.Default.Group,
                    title = "Class Setup"
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            ClassTypeButton(
                                icon = Icons.Default.Person,
                                label = "1-ON-1",
                                isSelected = selectedClassType == "1-ON-1",
                                onClick = { selectedClassType = "1-ON-1" },
                                modifier = Modifier.weight(1f)
                            )
                            ClassTypeButton(
                                icon = Icons.Default.Group,
                                label = "GROUP",
                                isSelected = selectedClassType == "GROUP",
                                onClick = { selectedClassType = "GROUP" },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "SESSION FREQUENCY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FrequencyBadge("ONE TIME SESSION", selectedFrequency == "ONE_TIME", { selectedFrequency = "ONE_TIME" })
                            FrequencyBadge("WEEKLY", selectedFrequency == "WEEKLY", { selectedFrequency = "WEEKLY" })
                            FrequencyBadge("BI-WEEKLY", selectedFrequency == "BI_WEEKLY", { selectedFrequency = "BI_WEEKLY" })
                            FrequencyBadge("MONTHLY", selectedFrequency == "MONTHLY", { selectedFrequency = "MONTHLY" })
                        }
                    }
                }
            }

            // Price Section
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "SESSION PRICE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                    Text("LOCKED", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFF3F4F6), // Slightly darker background to show disabled state
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "₹",
                                            color = Color.Gray,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = sessionPrice,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray // Gray text to show disabled state
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Price cannot be changed after course creation for compliance.",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Action Button
            item {
                Button(
                    onClick = { 
                        val newImage = when(subjectArea.uppercase()) {
                            "MATHEMATICS" -> "/Users/juthveshch/.gemini/antigravity/brain/9ec6928b-7e83-4244-99cc-d44fc7d72c71/math_subject_image_1771834744911.png"
                            "PHYSICS" -> "/Users/juthveshch/.gemini/antigravity/brain/9ec6928b-7e83-4244-99cc-d44fc7d72c71/physics_subject_image_1771834771374.png"
                            "PROGRAMMING" -> "/Users/juthveshch/.gemini/antigravity/brain/9ec6928b-7e83-4244-99cc-d44fc7d72c71/programming_subject_image_retry_1771834928726.png"
                            "COMPUTER SCIENCE" -> "/Users/juthveshch/.gemini/antigravity/brain/9ec6928b-7e83-4244-99cc-d44fc7d72c71/computer_science_subject_image_1771834812322.png"
                            "MUSIC" -> "/Users/juthveshch/.gemini/antigravity/brain/9ec6928b-7e83-4244-99cc-d44fc7d72c71/music_subject_image_1771834852253.png"
                            "ARTS" -> "/Users/juthveshch/.gemini/antigravity/brain/9ec6928b-7e83-4244-99cc-d44fc7d72c71/arts_subject_image_1771834884161.png"
                            else -> initialCourse.imageUrl
                        }
                        
                        val updatedCourse = initialCourse.copy(
                            title = courseTitle.ifBlank { "Untitled Course" },
                            category = subjectArea.uppercase(),
                            price = sessionPrice.toDoubleOrNull() ?: 0.0,
                            date = startDate,
                            time = startTime,
                            durationMins = durationMins.toIntOrNull() ?: 60,
                            meetingUrl = meetingUrl,
                            classType = selectedClassType,
                            frequency = selectedFrequency,
                            maxStudents = if (selectedClassType == "1-ON-1") 1 else 20,
                            curriculum = curriculum.ifBlank { "Master core concepts and advanced techniques in this comprehensive session." },
                            professorMessage = professorMessage.ifBlank { "Welcome to the session!" },
                            imageUrl = newImage
                        )
                        onCourseUpdated(updatedCourse)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .height(64.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryPurple)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(28.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "SAVE CHANGES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
            
            // padding bottom
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}




@Composable
fun EditCourseHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4F46E5), Color(0xFF312E81))
                )
            )
            .padding(24.dp)
    ) {
        Column {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Edit Course",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
