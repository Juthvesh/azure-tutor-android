package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VerifyPhoneScreen(
    onNavigateBack: () -> Unit,
    onVerifySuccess: () -> Unit
) {
    // Colors
    val primaryPurple = Color(0xFF6246EA)
    val lightPurple = Color(0xFF8B5CF6)
    val backgroundWhite = Color(0xFFFFFFFF)
    val surfaceColor = Color(0xFFF3F4F6)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val successGreen = Color(0xFF10B981) // Green for icon background

    val purpleGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFC084FC), Color(0xFF8B5CF6)) // Lighter purple gradient like in image
    )

    var otp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header: Back
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clickable { onNavigateBack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back",
                    fontSize = 16.sp,
                    color = textPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Fill remaining space but allow bottom padding
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundWhite)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Phone Icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = successGreen,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = successGreen.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                         Icon(
                             imageVector = Icons.Outlined.Smartphone,
                             contentDescription = "Phone",
                             tint = Color.White,
                             modifier = Modifier.size(40.dp)
                         )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title
                    Text(
                        text = "Verify Your Phone",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle
                    Text(
                        text = "We've sent a 4-digit code to",
                        fontSize = 14.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "9090909090", // Placeholder
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

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
                                OTPDigitInput(
                                    char = if (index < otp.length) otp[index].toString() else "",
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // Verify Button
                    Button(
                        onClick = { 
                            if (otp == "1234" || otp.length == 4) { // Allow any length 4 for demo/flexibility
                                onVerifySuccess()
                            } else {
                                errorMessage = "Please enter a valid 4-digit code"
                            }
                        },
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.VerifiedUser, // Using closest match
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Verify OTP",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Resend Text
                    Text(
                        text = "Didn't receive the code?",
                        fontSize = 14.sp,
                        color = textSecondary
                    )
                    Text(
                        text = "Resend in 22s",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary.copy(alpha = 0.7f), // Grayed out style
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.weight(1f)) // Push Demo box to bottom if space permits

                    // Demo Mode Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFDBEAFE), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Outlined.VerifiedUser, // Shield icon
                                contentDescription = null,
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Demo Mode",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1E3A8A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val demoText = buildAnnotatedString {
                                    append("For testing, use code ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, background = Color.White)) {
                                        append(" 1234 ")
                                    }
                                    append(" or any 4-digit number")
                                }
                                Text(
                                    text = demoText,
                                    fontSize = 13.sp,
                                    color = Color(0xFF2563EB),
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun OTPDigitInput(char: String, isFocused: Boolean) {
    val borderColor = if (isFocused) Color(0xFF8B5CF6) else Color(0xFFE5E7EB)
    val borderWidth = if (isFocused) 2.dp else 1.dp
    
    Box(
        modifier = Modifier
            .width(48.dp)
            .height(56.dp)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp)),
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
fun VerifyPhonePreview() {
    VerifyPhoneScreen(onNavigateBack = {}, onVerifySuccess = {})
}
