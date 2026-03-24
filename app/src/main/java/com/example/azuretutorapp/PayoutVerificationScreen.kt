package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PayoutVerificationScreen(
    onNavigateBack: () -> Unit,
    onRazorpayConnected: () -> Unit,
    isRazorpayConnected: Boolean,
    fullName: String,
    onFullNameChange: (String) -> Unit,
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    routingNumber: String,
    onRoutingChange: (String) -> Unit,
    panTaxId: String,
    onPanChange: (String) -> Unit
) {
    val backgroundGray = Color(0xFFF9FAFB)
    val primaryPurple = Color(0xFF6366F1)

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
                PayoutHeader(onNavigateBack)
            }

            // Account Status Card
            item {
                AccountStatusCard(isRazorpayConnected)
            }

            // Identity & Banking Section
            item {
                IdentityBankingForm(
                    isVerified = isRazorpayConnected,
                    fullName = fullName,
                    onFullNameChange = onFullNameChange,
                    accountNumber = accountNumber,
                    onAccountNumberChange = onAccountNumberChange,
                    routingNumber = routingNumber,
                    onRoutingChange = onRoutingChange,
                    panTaxId = panTaxId,
                    onPanChange = onPanChange
                )
            }


            // Action Button
            if (!isRazorpayConnected) {
                item {
                    Button(
                        onClick = onRazorpayConnected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                            .height(64.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5046E5))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "CONNECT WITH RAZORPAY",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // Encryption Footer
            item {
                EncryptionFooter()
            }
            
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun PayoutHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
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
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Payout Verification",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Complete your bank details verification to receive secure payments via Razorpay.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun AccountStatusCard(isVerified: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .offset(y = (-30).dp),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Account Status",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AZURE SENTINEL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isVerified) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isVerified) Color(0xFFD1FAE5) else Color(0xFFFEF3C7))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(
                        if (isVerified) Icons.Outlined.CheckCircle else Icons.Outlined.Warning, 
                        contentDescription = null, 
                        tint = if (isVerified) Color(0xFF10B981) else Color(0xFFF59E0B),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        val statusLabel = if (isVerified) "Verified" else "Not Verified"
                        val statusSubLabel = if (isVerified) "IDENTITY & PAYOUT ACTIVE" else "ACTION REQUIRED"
                        
                        Text(
                            text = statusSubLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isVerified) Color(0xFF065F46) else Color(0xFFB45309),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = statusLabel,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isVerified) Color(0xFF065F46) else Color(0xFFB45309)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isVerified) 
                                "Your professional identity and payout channel are active. You can now publish paid courses." 
                                else "Payout setup incomplete. You cannot publish paid courses yet.",
                            fontSize = 13.sp,
                            color = if (isVerified) Color(0xFF047857) else Color(0xFF92400E),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IdentityBankingForm(
    isVerified: Boolean,
    fullName: String, onFullNameChange: (String) -> Unit,
    accountNumber: String, onAccountNumberChange: (String) -> Unit,
    routingNumber: String, onRoutingChange: (String) -> Unit,
    panTaxId: String, onPanChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = Color(0xFFEEF2FF)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Fingerprint, 
                        contentDescription = null, 
                        tint = Color(0xFF6366F1), 
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Identity & Banking",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (isVerified) {
            // New, conditionally displayed details when verified
            VerificationDetailsCard(
                bankName = "Standard Chartered Bank",
                accountHolder = if (fullName.isNotEmpty()) fullName else "Prof. Anderson",
                referenceId = "STRPE-82910-VX"
            )
        } else {
            PayoutTextField(label = "FULL LEGAL NAME", value = fullName, onValueChange = onFullNameChange, placeholder = "As per bank records")
            Spacer(modifier = Modifier.height(20.dp))
            PayoutTextField(label = "BANK ACCOUNT NUMBER", value = accountNumber, onValueChange = onAccountNumberChange, placeholder = "0000 0000 0000")
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PayoutTextField(
                    label = "IFSC / ROUTING", 
                    value = routingNumber, 
                    onValueChange = onRoutingChange, 
                    placeholder = "STRPE001",
                    modifier = Modifier.weight(1f)
                )
                PayoutTextField(
                    label = "PAN / TAX ID", 
                    value = panTaxId, 
                    onValueChange = onPanChange, 
                    placeholder = "ABCDE1234F",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun VerificationDetailsCard(bankName: String, accountHolder: String, referenceId: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF9FAFB),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            DetailItem(label = "VERIFIED BANK", value = bankName)
            Spacer(modifier = Modifier.height(16.dp))
            DetailItem(label = "ACCOUNT HOLDER", value = accountHolder)
            Spacer(modifier = Modifier.height(16.dp))
            DetailItem(label = "REFERENCE ID", value = referenceId)
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Gray,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
    }
}

@Composable
fun PayoutTextField(
    label: String, 
    value: String, 
    onValueChange: (String) -> Unit, 
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label, 
            fontSize = 11.sp, 
            fontWeight = FontWeight.Bold, 
            color = Color.Gray, 
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}


@Composable
fun EncryptionFooter() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        color = Color(0xFFF0F4FF), // Very light blue
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp), 
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF4F46E5)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Security, 
                        contentDescription = null, 
                        tint = Color.White, 
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = "ENCRYPTED INFRASTRUCTURE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E1B4B),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your sensitive data is encrypted using AES-256 and stored in Azure Key Vault. We never store raw bank credentials on our servers.",
                    fontSize = 13.sp,
                    color = Color(0xFF4F46E5),
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview
@Composable
fun PayoutVerificationPreview() {
    PayoutVerificationScreen(
        onNavigateBack = {}, 
        onRazorpayConnected = {},
        isRazorpayConnected = false,
        fullName = "",
        onFullNameChange = {},
        accountNumber = "",
        onAccountNumberChange = {},
        routingNumber = "",
        onRoutingChange = {},
        panTaxId = "",
        onPanChange = {}
    )
}
