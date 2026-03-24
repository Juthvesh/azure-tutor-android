package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*

@Composable
fun CreateAccountScreen(
    onNavigateToSignIn: () -> Unit,
    onNavigateToVerify: (String, String, String, String, String, String) -> Unit
) {

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var selectedRole by remember { mutableStateOf("Student") }
    var expanded by remember { mutableStateOf(false) }
    val grades = listOf("Grade 1-5", "Grade 6-8", "Grade 9-10", "Grade 11-12", "Undergraduate", "Graduate", "Professional")
    var selectedGrade by remember { mutableStateOf("") }
    // Colors
    val primaryPurple = Color(0xFF6246EA)
    val lightPurple = Color(0xFF8B5CF6)
    val backgroundWhite = Color(0xFFFFFFFF)
    val surfaceColor = Color(0xFFF3F4F6)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)

    val purpleGradient = Brush.horizontalGradient(
        colors = listOf(primaryPurple, lightPurple)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header: Back to Login
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clickable { onNavigateToSignIn() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back to Login",
                    fontSize = 16.sp,
                    color = textPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Main Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundWhite)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Logo
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF7C3AED), Color(0xFF4F46E5))
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Book,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Title
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Create Account",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "JOIN TUTORHUB TODAY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = textSecondary,
                            letterSpacing = 1.sp
                        )
                    }

                    // Role Toggle
                    Text(
                        text = "I am a",
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RoleOption(
                            text = "Student",
                            icon = Icons.Outlined.School,
                            isSelected = selectedRole == "Student",
                            onClick = { selectedRole = "Student" },
                            modifier = Modifier.weight(1f)
                        )
                        RoleOption(
                            text = "Tutor",
                            icon = Icons.Outlined.Person,
                            isSelected = selectedRole == "Tutor",
                            onClick = { selectedRole = "Tutor" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Form Fields
                    FormSection(title = "Full Name") {
                        StyledTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = "Enter your full name",
                            icon = Icons.Outlined.Person
                        )
                    }

                    FormSection(title = "Email Address") {
                        StyledTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "your.email@example.com",
                            icon = Icons.Outlined.Email,
                            keyboardType = KeyboardType.Email
                        )
                    }

                    FormSection(title = "Phone Number", isRequired = true) {
                        StyledTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            placeholder = "+1 (555) 000-0000",
                            icon = Icons.Outlined.Phone,
                            keyboardType = KeyboardType.Phone
                        )
                        Text(
                            text = "OTP will be sent to your email address",
                            fontSize = 12.sp,
                            color = textSecondary,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }

                    // Role-specific fields
                    if (selectedRole == "Student") {
                        FormSection(title = "Grade Level") {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .background(Color(0xFFF9FAFB), RoundedCornerShape(16.dp))
                                    .clickable { expanded = true }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (selectedGrade.isEmpty()) "Select your grade level" else selectedGrade,
                                        color = if (selectedGrade.isEmpty()) textSecondary else textPrimary                                    )
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = textSecondary
                                    )
                                }
                                
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    grades.forEach { grade ->
                                        DropdownMenuItem(
                                            text = { Text(text = grade) },
                                            onClick = {
                                                selectedGrade = grade
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Tutor Specific: Subjects Selection
                        FormSection(title = "Subjects You Can Teach") {
                            val subjects = listOf(
                                "Mathematics", "Physics", 
                                "Chemistry", "Biology", 
                                "English", "Computer Science", 
                                "History", "Geography"
                            )
                            // Multi-selection state
                            val selectedSubjects = remember { mutableStateListOf<String>() }
                            
                            // Simple FlowRow-like implementation using Column and Rows since FlowRow is experimental/needs Accompanist in some versions
                            // or distinct Rows for the grid layout shown in image (2 columns)
                            
                            // Using a Grid-like layout with 2 columns
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                subjects.chunked(2).forEach { rowSubjects ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowSubjects.forEach { subject ->
                                            val isSelected = selectedSubjects.contains(subject)
                                            val borderColor = if (isSelected) primaryPurple else Color(0xFFE5E7EB)
                                            val containerColor = if (isSelected) primaryPurple.copy(alpha = 0.05f) else Color.Transparent
                                            val textColor = if (isSelected) primaryPurple else textPrimary

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .clip(RoundedCornerShape(24.dp))
                                                    .background(containerColor)
                                                    .clickable {
                                                        if (isSelected) {
                                                            selectedSubjects.remove(subject)
                                                        } else {
                                                            selectedSubjects.add(subject)
                                                        }
                                                    }
                                                    .border(
                                                        width = 1.dp,
                                                        color = borderColor,
                                                        shape = RoundedCornerShape(24.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = subject,
                                                    color = textColor,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                        // If odd number of items, fill the empty space to keep alignment
                                        if (rowSubjects.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }



                    FormSection(title = "Password") {

                        var passwordVisible by remember { mutableStateOf(false) }

                        StyledTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "Create a strong password",
                            icon = Icons.Outlined.Lock,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onPasswordToggle = { passwordVisible = !passwordVisible }
                        )
                    }

                    FormSection(title = "Confirm Password") {

                        var confirmPasswordVisible by remember { mutableStateOf(false) }

                        StyledTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = "Re-enter your password",
                            icon = Icons.Outlined.Lock,
                            isPassword = true,
                            passwordVisible = confirmPasswordVisible,
                            onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible }
                        )
                    }

                    // Terms and Conditions
                    var checked by remember { mutableStateOf(false) }
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { checked = !checked }
                            .padding(4.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = null,
                            colors = CheckboxDefaults.colors(
                                checkedColor = primaryPurple,
                                uncheckedColor = textSecondary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        val annotatedString = buildAnnotatedString {
                            append("I agree to the ")
                            pushStyle(SpanStyle(color = primaryPurple, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline))
                            append("Terms & Conditions")
                            pop()
                            append(" and ")
                            pushStyle(SpanStyle(color = primaryPurple, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline))
                            append("Privacy Policy")
                            pop()
                        }
                        
                        Text(
                            text = annotatedString,
                            fontSize = 13.sp,
                            color = textPrimary,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(top = 12.dp) // Align with checkbox visually
                        )
                    }

                    // Continue Button
                    Button(
                        onClick = {
                            if (email.isNotEmpty()) {

                                val request = SendOtpRequest(email)

                                CoroutineScope(Dispatchers.IO).launch {
                                    try {

                                        val response = RetrofitClient.apiService.sendOtp(request)

                                        if (response.isSuccessful) {

                                            withContext(Dispatchers.Main) {
                                                onNavigateToVerify(
                                                    fullName,
                                                    email,
                                                    phoneNumber,
                                                    password,
                                                    selectedRole,
                                                    selectedGrade
                                                )
                                            }

                                        }

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(purpleGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Continue to Verification",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Footer Links
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavigateToSignIn() }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Already have an account? ", color = textSecondary)
                        Text(
                            text = "Sign In",
                            color = primaryPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Secured Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SECURED WITH AZURE AD B2C",
                            fontSize = 10.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun RoleOption(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryPurple = Color(0xFF6246EA)
    val textSecondary = Color(0xFF6B7280)
    val selectedBg = Color(0xFFF5F3FF)
    val unselectedBg = Color(0xFFF9FAFB)
    val borderColor = if (isSelected) primaryPurple else Color(0xFFE5E7EB)
    val contentColor = if (isSelected) primaryPurple else textSecondary

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .shadow(
                elevation = if (isSelected) 4.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = primaryPurple.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) selectedBg else unselectedBg,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun FormSection(
    title: String,
    isRequired: Boolean = false,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            if (isRequired) {
                Text(
                    text = " *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}


@Preview
@Composable
fun CreateAccountPreview() {
    CreateAccountScreen(
        onNavigateToSignIn = {},
        onNavigateToVerify = { _, _, _, _, _, _ -> }

    )
}
