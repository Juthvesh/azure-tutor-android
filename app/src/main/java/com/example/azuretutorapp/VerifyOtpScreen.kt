package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import android.util.Log
@Composable
fun VerifyOtpScreen(
    fullName: String,
    email: String,
    phoneNumber: String,
    password: String,
    role: String,
    grade: String,
    otpFlow: String = "register", // Default to register for backward compatibility
    onNavigateBack: () -> Unit,
    onVerifySuccess: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }
    var isVerifying by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF7C3AED), Color(0xFF4F46E5))
    )
    val textSecondary = Color(0xFF6B7280)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "T",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF7C3AED)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Verify Your Email",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "We sent a code to $email",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Main Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Back Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateBack() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Back",
                            color = textSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Enter 4-Digit Code",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF374151)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // OTP Input Area with boxes and invisible text field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { focusRequester.requestFocus() },
                        contentAlignment = Alignment.Center
                    ) {
                        // OTP Input Boxes (Visual Layer)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                        ) {
                            repeat(4) { index ->
                                val char = if (index < otp.length) otp[index].toString() else ""
                                OtpBox(
                                    char = char,
                                    isFocused = index == otp.length
                                )
                            }
                        }

                        // Invisible TextField for input handling (Input Layer)
                        TextField(
                            value = otp,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    otp = it
                                    errorMessage = null
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .focusRequester(focusRequester),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = Color.Transparent,
                                focusedTextColor = Color.Transparent,
                                unfocusedTextColor = Color.Transparent
                            )
                        )
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row {
                        Text(
                            text = "Resend code in ",
                            color = textSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "50s",
                            color = Color(0xFF7C3AED),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        enabled = !isVerifying,
                        onClick = {

                            if (otp.length != 4) {
                                errorMessage = "Please enter the complete 4-digit code."
                                return@Button
                            }

                            isVerifying = true
                            errorMessage = null

                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {

                                try {
                                    Log.d("OTP_DEBUG", "EMAIL SENT = $email")
                                    Log.d("OTP_DEBUG", "OTP SENT = $otp")

                                    val response = RetrofitClient.apiService.verifyOtp(
                                        VerifyOtpRequest(
                                            email = email,
                                            otp = otp
                                        )
                                    )

                                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                        if (response.isSuccessful) {
                                            if (otpFlow == "register") {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    try {
                                                        val registerResponse = RetrofitClient.apiService.register(
                                                            RegisterRequest(
                                                                fullName = fullName,
                                                                email = email,
                                                                phone = phoneNumber,
                                                                password = password,
                                                                role = role,
                                                                grade = grade
                                                            )
                                                        )
                                                        withContext(Dispatchers.Main) {
                                                            isVerifying = false
                                                            if (registerResponse.isSuccessful) {
                                                                onVerifySuccess()
                                                            } else {
                                                                errorMessage = "Registration failed"
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        withContext(Dispatchers.Main) {
                                                            errorMessage = "Network error"
                                                        }
                                                    }
                                                }
                                            } else {
                                                // For forgot password or other flows, just signal success
                                                onVerifySuccess()
                                            }
                                        } else {
                                            errorMessage = "Invalid OTP"
                                        }
                                    }

                                } catch (e: Exception) {

                                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                        errorMessage = "Network error"
                                    }

                                }

                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Verify OTP",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OtpBox(char: String, isFocused: Boolean) {
    Box(
        modifier = Modifier
            .size(width = 45.dp, height = 56.dp)
            .border(
                width = 1.dp,
                color = if (isFocused) Color(0xFF7C3AED) else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
    }
}

@Preview
@Composable
fun VerifyOtpScreenPreview() {
    VerifyOtpScreen(
        fullName = "Test User",
        email = "test@example.com",
        phoneNumber = "9876543210",
        password = "123456",
        role = "student",
        grade = "Grade 10",
        onNavigateBack = {},
        onVerifySuccess = {}
    )
}
