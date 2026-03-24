package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Preview
@Composable
fun SignInScreenPreview() {
    SignInScreen(
        onNavigateToCreateAccount = {},
        onNavigateToTutorDashboard = { _, _ -> },
        onNavigateToStudentDashboard = { _, _ -> },
        onNavigateToForgotPassword = {}
    )
}

@Composable
fun SignInScreen(
    onNavigateToCreateAccount: () -> Unit,
    onNavigateToTutorDashboard: (String, Int) -> Unit,
    onNavigateToStudentDashboard: (String, Int) -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    // Define Colors
    val primaryPurple = Color(0xFF6246EA)
    val lightPurple = Color(0xFF8B5CF6)
    val backgroundWhite = Color(0xFFFFFFFF)
    val surfaceColor = Color(0xFFF3F4F6) // Light gray background for screen
    val inputBackground = Color(0xFFF9FAFB)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val placeholderColor = Color(0xFF9CA3AF)
    
    val purpleGradient = Brush.horizontalGradient(
        colors = listOf(primaryPurple, lightPurple)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor), // Background for the whole screen
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundWhite)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF7C3AED), Color(0xFF4F46E5))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Book,
                        contentDescription = "Logo", // Use a generic book icon as placeholder
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Title and Subtitle
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome Back",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "SIGN IN TO TUTORHUB",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary,
                        letterSpacing = 1.sp
                    )
                }

                // Toggle Switch (Student / Tutor)
                var selectedRole by remember { mutableStateOf("Student") }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val roles = listOf("Student", "Tutor")
                    roles.forEach { role ->
                        val isSelected = selectedRole == role
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) primaryPurple else Color.Transparent)
                                .clickable { selectedRole = role }
                                .then(if (isSelected) Modifier.shadow(4.dp, RoundedCornerShape(12.dp), spotColor = primaryPurple.copy(alpha = 0.5f)) else Modifier),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = role,
                                color = if (isSelected) Color.White else textSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // Input Fields
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                StyledTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email Address",
                    icon = Icons.Outlined.Email 
                )
                
                StyledTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    icon = Icons.Outlined.Lock,
                    isPassword = true
                )

                Text(
                    text = "Forgot Password?",
                    color = primaryPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { onNavigateToForgotPassword() }
                )

                var isLoading by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()
                val context = LocalContext.current

                Button(
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            android.widget.Toast.makeText(context, "Please enter email and password", android.widget.Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        scope.launch {
                            try {
                                val response = RetrofitClient.apiService.login(LoginRequest(email, password))
                                isLoading = false
                                
                                if (response.isSuccessful) {
                                    val loginData = response.body()
                                    if (loginData != null) {
                                        android.widget.Toast.makeText(context, "Login Successful: ${loginData.message}", android.widget.Toast.LENGTH_SHORT).show()
                                        
                                        // Navigate based on role from DB
                                        if (loginData.role.uppercase() == "TUTOR") {
                                            onNavigateToTutorDashboard(email, loginData.user_id)
                                        } else {
                                            onNavigateToStudentDashboard(email, loginData.user_id)
                                        }
                                    } else {
                                        android.widget.Toast.makeText(context, "Login failed: Empty response", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    android.widget.Toast.makeText(context, "Login failed: Invalid credentials", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = primaryPurple.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(purpleGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = "Sign In",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Create Account Section
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Don't have an account?",
                        color = textSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onNavigateToCreateAccount,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                         Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF9F7AEA), Color(0xFF6366F1)) // Lighter purple gradient
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Create Account",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = ">", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Footer Security Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = "Shield",
                        tint = textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ENTERPRISE SECURED ENVIRONMENT",
                        fontSize = 10.sp,
                        color = textSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}


