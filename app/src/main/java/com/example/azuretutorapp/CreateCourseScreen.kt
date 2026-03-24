package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseScreen(
    onNavigateBack: () -> Unit,
    isRazorpayConnected: Boolean,
    onNavigateToVerify: () -> Unit,
    onCourseDeployed: (Course) -> Unit
) {
    val backgroundGray = Color(0xFFF9FAFB)
    val primaryPurple = Color(0xFF6366F1)
    
    var courseTitle by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var startTime by remember { mutableStateOf("10:00") }
    var subjectArea by remember { mutableStateOf("Physics") }
    var durationMins by remember { mutableStateOf("60") }
    var meetingUrl by remember { mutableStateOf("") }
    var isMonetizationEnabled by remember { mutableStateOf(false) }
    var sessionPrice by remember { mutableStateOf("0") }
    var selectedCurrency by remember { mutableStateOf("INR") }
    var selectedClassType by remember { mutableStateOf("1-ON-1") }
    var selectedFrequency by remember { mutableStateOf("WEEKLY") } // DB ENUM: ONE_TIME, WEEKLY, BI_WEEKLY, MONTHLY
    var curriculum by remember { mutableStateOf("") }
    var professorMessage by remember { mutableStateOf("") }
    
    // Automatic enablement when verified
    LaunchedEffect(isRazorpayConnected) {
        if (isRazorpayConnected) {
            isMonetizationEnabled = true
        }
    }

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
    
    val subjects = listOf("Mathematics", "Physics", "Chemistry", "English", "Biology", "Computer Science", "Music", "Arts")

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
                CreateCourseHeader(onNavigateBack)
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
                                        // Don't auto-close if they are typing
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
                            
                            // Refined Duration Box (matching the provided image)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = " ", // Placeholder for alignment
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
                        
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            mainAxisSpacing = 12.dp,
                            crossAxisSpacing = 12.dp
                        ) {
                            FrequencyBadge("ONE TIME SESSION", selectedFrequency == "ONE_TIME", { selectedFrequency = "ONE_TIME" })
                            FrequencyBadge("WEEKLY", selectedFrequency == "WEEKLY", { selectedFrequency = "WEEKLY" })
                            FrequencyBadge("BI-WEEKLY", selectedFrequency == "BI_WEEKLY", { selectedFrequency = "BI_WEEKLY" })
                            FrequencyBadge("MONTHLY", selectedFrequency == "MONTHLY", { selectedFrequency = "MONTHLY" })
                        }
                    }
                }
            }

            // Monetization Section
            item {
                MonetizationSection(
                    isEnabled = isMonetizationEnabled && isRazorpayConnected, 
                    onToggle = { isMonetizationEnabled = it },
                    isRazorpayConnected = isRazorpayConnected,
                    onNavigateToVerify = onNavigateToVerify,
                    sessionPrice = sessionPrice,
                    onPriceChange = { sessionPrice = it },
                    selectedCurrency = selectedCurrency,
                    onCurrencyChange = { selectedCurrency = it }
                )
            }

            // Action Button
            item {
                Button(
                    onClick = { 
                        if (isRazorpayConnected) {
                            // Convert 24h time to 12h AM/PM for storage and display consistency
                            val formattedTime = try {
                                val time24h = java.time.LocalTime.parse(startTime)
                                time24h.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
                            } catch (e: Exception) {
                                startTime // Fallback
                            }

                            val newCourse = Course(
                                title = courseTitle.ifBlank { "Untitled Course" },
                                tutorName = "Dr. Sarah Smith",
                                category = subjectArea.uppercase(),
                                enrolledCount = 0,
                                price = sessionPrice.toDoubleOrNull() ?: 0.0,
                                date = startDate,
                                time = formattedTime,
                                imageGradientColors = listOf(
                                    Color((0..255).random(), (0..255).random(), (0..255).random()),
                                    Color((0..255).random(), (0..255).random(), (0..255).random())
                                ),
                                durationMins = durationMins.toIntOrNull() ?: 60,
                                meetingUrl = meetingUrl,
                                classType = selectedClassType,
                                frequency = selectedFrequency,
                                maxStudents = if (selectedClassType == "1-ON-1") 1 else 20,
                                curriculum = curriculum.ifBlank { "Master core concepts and advanced techniques in this comprehensive session." },
                                professorMessage = professorMessage.ifBlank { "Welcome to the session!" }
                            )
                            onCourseDeployed(newCourse)
                        }
                    },
                    enabled = isRazorpayConnected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .height(64.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRazorpayConnected) primaryPurple else Color.LightGray
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                                text = "DEPLOY COURSE TO AZURE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                        }
                        if (!isRazorpayConnected) {
                            Text(
                                "RAZORPAY CONNECTION REQUIRED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Footer
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Simulated Razorpay Logo
                        Icon(Icons.Default.Group, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("razorpay", color = Color.LightGray, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "ENTERPRISE SAAS ARCHITECTURE V4.0",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CreateCourseHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
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
                text = "Design New Course",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50),
                modifier = Modifier.wrapContentSize()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Security, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AZURE ENCRYPTED FORM",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .offset(y = (-20).dp),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFEEF2FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}

@Composable
fun ClassTypeButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) Color(0xFF6366F1) else Color(0xFFF9FAFB),
        shadowElevation = if (isSelected) 8.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                color = if (isSelected) Color.White else Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FrequencyBadge(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF9FAFB),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF6366F1).copy(alpha = 0.3f) else Color(0xFFF3F4F6))
    ) {
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF6366F1) else Color.Gray,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun MonetizationSection(
    isEnabled: Boolean, 
    onToggle: (Boolean) -> Unit,
    isRazorpayConnected: Boolean,
    onNavigateToVerify: () -> Unit,
    sessionPrice: String,
    onPriceChange: (String) -> Unit,
    selectedCurrency: String,
    onCurrencyChange: (String) -> Unit
) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFECFDF5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("₹", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Monetization",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        if (!isRazorpayConnected) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Color(0xFFFFF7ED),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5))
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("SETUP REQUIRED", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFF59E0B))
                                }
                            }
                        }
                    }
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = if (isRazorpayConnected) onToggle else null,
                    enabled = isRazorpayConnected,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF10B981),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                )
            }
            
            if (isEnabled) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SESSION PRICE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF9FAFB),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "₹", // Standardized to INR as per requirements
                                    color = Color.Gray,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                androidx.compose.foundation.text.BasicTextField(
                                    value = sessionPrice,
                                    onValueChange = onPriceChange,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF111827)
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CURRENCY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF9FAFB),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            onClick = { /* TODO: Dropdown */ }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedCurrency,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            if (!isRazorpayConnected) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEF3C7))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "COMPLETE PAYOUT SETUP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFB45309),
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Verification is required to create paid courses. Your payments are handled via Razorpay Connect on Azure infrastructure.",
                            fontSize = 13.sp,
                            color = Color(0xFF92400E),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "SETUP NOW →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6366F1),
                            modifier = Modifier.clickable { onNavigateToVerify() }
                        )
                    }
                }
            }
        }
    }
}

// FlowRow implementation (Simplified for brevity)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeholders = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        val layoutWidth = constraints.maxWidth
        
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0
        
        placeholders.forEach { placeable ->
            if (currentRowWidth + placeable.width + mainAxisSpacing.toPx() > layoutWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                currentRowWidth = 0
            }
            currentRow.add(placeable)
            currentRowWidth += (placeable.width + mainAxisSpacing.toPx()).toInt()
        }
        rows.add(currentRow)
        
        val totalHeight = rows.sumOf { it.maxOf { p -> p.height } } + (rows.size - 1) * crossAxisSpacing.toPx()
        
        layout(layoutWidth, totalHeight.toInt()) {
            var yOffset = 0
            rows.forEach { row ->
                var xOffset = 0
                val rowHeight = row.maxOf { it.height }
                row.forEach { placeable ->
                    placeable.placeRelative(xOffset, yOffset)
                    xOffset += (placeable.width + mainAxisSpacing.toPx()).toInt()
                }
                yOffset += (rowHeight + crossAxisSpacing.toPx()).toInt()
            }
        }
    }
}



data class CalendarDayData(val day: Int, val isCurrentMonth: Boolean)



@Preview
@Composable
fun CreateCoursePreview() {
    CreateCourseScreen(
        onNavigateBack = {},
        isRazorpayConnected = false,
        onNavigateToVerify = {},
        onCourseDeployed = {}
    )
}
