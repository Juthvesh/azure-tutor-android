package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class PaymentStatus {
    INITIAL,
    PROCESSING,
    VERIFYING,
    SUCCESS
}

@Composable
fun CoursePaymentScreen(
    course: Course,
    studentId: Int,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: (Course) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }
    var isConfirmed by remember { mutableStateOf(false) }
    var paymentStatus by remember { mutableStateOf(PaymentStatus.INITIAL) }
    
    // Process payment simulation
 LaunchedEffect(paymentStatus) {
    when (paymentStatus) {

        PaymentStatus.PROCESSING -> {
            delay(2000)
            paymentStatus = PaymentStatus.VERIFYING
        }

        PaymentStatus.VERIFYING -> {
            delay(2000)

            try {

                val api = RetrofitClient.apiService
                Log.d("PAYMENT_DEBUG", "Course title: ${course.title}")
                Log.d("PAYMENT_DEBUG", "Course backendId: ${course.backendId}")
                Log.d("PAYMENT_DEBUG", "Course id: ${course.id}")

                val courseBackendId = course.backendId!!


                val request = VerifyPaymentRequest(
                    student_id = studentId,
                    course_id = courseBackendId
                )

                Log.d("PAYMENT_TRACE", "Sending VerifyPaymentRequest: student_id=$studentId, course_id=$courseBackendId, course_title=${course.title}, course_price=${course.price}")

                val response = withContext(Dispatchers.IO) {
                    api.verifyPayment(request)
                }

                if (response.isSuccessful) {
                    Log.d("PAYMENT", "Verify payment success")
                    paymentStatus = PaymentStatus.SUCCESS
                } else {
                    Log.e("PAYMENT", "Verify payment failed")
                }

            } catch (e: Exception) {
                Log.e("PAYMENT", "Error: ${e.message}")
            }
        }

        else -> {}
    }
}
    
    val backgroundPurple = Color(0xFF2E1065) // Darker purple from image
    val surfaceWhite = Color.White
    val primaryPurple = Color(0xFF6366F1)
    
    Scaffold(
        containerColor = backgroundPurple
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "SECURE PAYMENT",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (paymentStatus == PaymentStatus.SUCCESS) {
                    item {
                        SuccessScreen(onGoToCourses = { onPaymentSuccess(course) })
                    }
                } else {
                    // Order Summary Card
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(32.dp),
                            color = surfaceWhite
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ReceiptLong,
                                            contentDescription = null,
                                            tint = primaryPurple,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "ORDER SUMMARY",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black,
                                        letterSpacing = 1.sp
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            course.title,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Text(
                                            course.category,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = primaryPurple,
                                            letterSpacing = 1.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                    Text(
                                        "₹${course.price.toInt()}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                Divider(color = Color(0xFFF3F4F6))
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "TOTAL PAYABLE",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Gray,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        "₹${course.price.toInt()}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black,
                                        color = primaryPurple
                                    )
                                }
                            }
                        }
                    }

                    // Payment Methods Selection
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(32.dp),
                            color = surfaceWhite
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    "SELECT PAYMENT METHOD",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    letterSpacing = 1.sp
                                )
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                PaymentMethodItem(
                                    title = "UPI ID / VPA",
                                    icon = Icons.Default.AccountBalanceWallet,
                                    isSelected = selectedMethod == "UPI",
                                    onClick = { selectedMethod = "UPI" }
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                PaymentMethodItem(
                                    title = "PHONEPE",
                                    icon = Icons.Default.Smartphone,
                                    isSelected = selectedMethod == "PhonePe",
                                    onClick = { selectedMethod = "PhonePe" }
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                PaymentMethodItem(
                                    title = "GOOGLE PAY",
                                    icon = Icons.Default.Payment,
                                    isSelected = selectedMethod == "GPay",
                                    onClick = { selectedMethod = "GPay" }
                                )

                                if (selectedMethod.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(24.dp))

                                    // UPI ID Entry
                                    Text(
                                        "ENTER UPI ID",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OutlinedTextField(
                                        value = upiId,
                                        onValueChange = { upiId = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = { Text("example@okaxis", fontSize = 14.sp) },
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = primaryPurple,
                                            unfocusedBorderColor = Color(0xFFE5E7EB)
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Confirmation Checkbox
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { isConfirmed = !isConfirmed }
                                    ) {
                                        Checkbox(
                                            checked = isConfirmed,
                                            onCheckedChange = { isConfirmed = it },
                                            colors = CheckboxDefaults.colors(checkedColor = primaryPurple)
                                        )
                                        Text(
                                            text = "I confirm that the UPI ID is correct and I authorize this transaction",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            lineHeight = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Action Area
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val isReady = selectedMethod.isNotEmpty() && upiId.isNotBlank() && isConfirmed
                            
                            Button(
                                onClick = { paymentStatus = PaymentStatus.PROCESSING },
                                enabled = paymentStatus == PaymentStatus.INITIAL && isReady,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryPurple,
                                    disabledContainerColor = primaryPurple.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (paymentStatus == PaymentStatus.PROCESSING) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("PROCESSING...", fontSize = 14.sp, fontWeight = FontWeight.Black)
                                    } else if (paymentStatus == PaymentStatus.VERIFYING) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("VERIFYING WITH UPI...", fontSize = 14.sp, fontWeight = FontWeight.Black)
                                    } else {
                                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            "PAY ₹${course.price.toInt()} SECURELY",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                               Text("razorpay", color = Color.White.copy(alpha = 0.4f), fontWeight = FontWeight.Black, fontSize = 18.sp)
                               Spacer(modifier = Modifier.width(16.dp))
                               Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "PCI-DSS COMPLIANT HUB",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessScreen(onGoToCourses: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(48.dp), // Very rounded like in image
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success Icon Circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF10B981), Color(0xFF059669))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "ACCESS UNLOCKED",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "ENROLLMENT CONFIRMED",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF10B981),
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onGoToCourses,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
            ) {
                Text(
                    "GO TO MY COURSES",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun PaymentMethodItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF6366F1) else Color(0xFFF3F4F6)
    val bgColor = if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF9FAFB)
    val primaryPurple = Color(0xFF6366F1)

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) primaryPurple else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) primaryPurple else Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(
                        width = if (isSelected) 6.dp else 2.dp,
                        color = if (isSelected) primaryPurple else Color(0xFFE5E7EB),
                        shape = CircleShape
                    )
            )
        }
    }
}

