package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Savings
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
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RevenueHubScreen(
    tutorId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToAccount: () -> Unit,
    availableBalance: Double,
    fullName: String,
    onFullNameChange: (String) -> Unit,
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    routingNumber: String,
    onRoutingChange: (String) -> Unit,
    isRazorpayConnected: Boolean = false,
    transactions: List<BillingTransaction> = emptyList()
) {
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showBankInfoDialog by remember { mutableStateOf(false) }
    var totalEarnings by remember { mutableStateOf(0.0) }
    var earningTransactions by remember { mutableStateOf<List<EarningTransaction>>(emptyList()) }
    LaunchedEffect(tutorId) {

        try {

            val response = RetrofitClient.apiService.getTutorEarnings(tutorId)

            if (response.isSuccessful) {

                val data = response.body()

                totalEarnings = data?.total_earnings ?: 0.0
                earningTransactions = data?.transactions ?: emptyList()

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    
    val backgroundGray = Color(0xFFF9FAFB)
    val primaryPurple = Color(0xFF4F46E5)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)

    Scaffold(
        bottomBar = {
            UnifiedBottomNavigation(
                currentScreen = "Earnings",
                onNavigateToDashboard = onNavigateToDashboard,
                onNavigateToEarnings = {},
                onNavigateToAccount = onNavigateToAccount
            )
        },
        containerColor = backgroundGray
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Section with Gradient
            item {
                RevenueHeader(
                    availableBalance = totalEarnings,
                    isRazorpayConnected = isRazorpayConnected,
                    onBack = onNavigateBack,
                    onWithdrawClick = { showWithdrawDialog = true },
                    onBankInfoClick = { showBankInfoDialog = true }
                )
            }


            // Transaction Logs Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TRANSACTION LOGS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF10B981), CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AUDITED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (earningTransactions.isEmpty()) {
                        // Empty State Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color.LightGray.copy(alpha = 0.5f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = RoundedCornerShape(32.dp)
                                )
                                .background(
                                    Color.White.copy(alpha = 0.3f),
                                    RoundedCornerShape(32.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = Color(0xFFE5E7EB),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "NO TRANSACTION HISTORY",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9CA3AF),
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            earningTransactions.reversed().forEach { transaction ->

                                val billingTransaction = BillingTransaction(
                                    title = "Course #${transaction.course_id} Enrollment",
                                    date = transaction.created_at,
                                    transactionId = "TXN-${transaction.course_id}",
                                    amount = "₹${transaction.tutor_earning}",
                                    status = TransactionStatus.SUCCESS
                                )

                                TransactionLogRow(billingTransaction)
                            }
                        }
                    }
                }
            }

            // Footer
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PCI-DSS COMPLIANT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9CA3AF),
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "TUTORHUB RETAINS 20% PLATFORM\nCOMMISSION. STANDARD SETTLEMENT CYCLE IS\n3-5 BUSINESS DAYS.",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9CA3AF),
                        letterSpacing = 1.sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(horizontal = 48.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }

    if (showWithdrawDialog) {
        WithdrawalDialog(
            balance = availableBalance,
            lastFour = if (accountNumber.length >= 4) accountNumber.takeLast(4) else accountNumber,
            onDismiss = { showWithdrawDialog = false },
            onConfirm = { /* TODO: Process withdrawal */ showWithdrawDialog = false }
        )
    }

    if (showBankInfoDialog) {
        SettlementDetailsDialog(
            fullName = fullName,
            onFullNameChange = onFullNameChange,
            accountNumber = accountNumber,
            onAccountNumberChange = onAccountNumberChange,
            routingNumber = routingNumber,
            onRoutingChange = onRoutingChange,
            onDismiss = { showBankInfoDialog = false },
            onSave = {

                val request = BankDetailsRequest(
                    tutor_id = tutorId,
                    account_holder_name = fullName,
                    account_number = accountNumber,
                    ifsc_code = routingNumber
                )

                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    try {
                        RetrofitClient.apiService.saveBankDetails(request)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                showBankInfoDialog = false
            }
        )
    }
}

@Composable
fun RevenueHeader(
    availableBalance: Double,
    isRazorpayConnected: Boolean,
    onBack: () -> Unit,
    onWithdrawClick: () -> Unit,
    onBankInfoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
                )
            )
            .padding(24.dp)
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)) {
                    Text(
                        text = "REVENUE",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp
                    )
                    Text(
                        text = "HUB",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp
                    )
                }

                // Live Sync Badge / Connection Status
                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier
                            .size(6.dp)
                            .background(if (isRazorpayConnected) Color(0xFF10B981) else Color(0xFFEF4444), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRazorpayConnected) "LIVE SYNC\nACTIVE" else "IDENTITY\nREQUIRED",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            lineHeight = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Balance Section
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "AVAILABLE BALANCE",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${"%,.2f".format(availableBalance)}",
                    color = Color.White,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onWithdrawClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowOutward, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WITHDRAW", color = Color(0xFF4F46E5), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                
                OutlinedButton(
                    onClick = onBankInfoClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BANK INFO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun WithdrawalDialog(balance: Double, lastFour: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    var amountText by remember { mutableStateOf("0.00") }
    val amount = amountText.toDoubleOrNull() ?: 0.0
    val isValid = amount > 0 && amount <= balance

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(48.dp), // More rounded like the image
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(top = 40.dp, bottom = 32.dp, start = 32.dp, end = 32.dp), 
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CONFIRM WITHDRAWAL",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF111827),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF9FAFB),
                    shape = RoundedCornerShape(32.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "AMOUNT TO TRANSFER", 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Black, 
                            color = Color(0xFF9CA3AF), 
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "₹", 
                                fontSize = 36.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color(0xFF9CA3AF)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            androidx.compose.foundation.text.BasicTextField(
                                value = amountText,
                                onValueChange = { input -> 
                                    val inputVal = input.toDoubleOrNull() ?: 0.0
                                    if (inputVal <= balance) {
                                        amountText = input
                                    } else {
                                        amountText = "%.2f".format(balance)
                                    }
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 48.sp, 
                                    fontWeight = FontWeight.ExtraBold, 
                                    color = Color(0xFF818CF8) // Lighter purple like image
                                ),
                                modifier = Modifier.width(IntrinsicSize.Min),
                                singleLine = true
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.SpaceBetween, 
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "MAX: ₹${"%,.2f".format(balance)}", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color(0xFF9CA3AF)
                            )
                            Text(
                                "USE ALL", 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.ExtraBold, 
                                color = Color(0xFF6366F1),
                                modifier = Modifier.clickable { amountText = "%.2f".format(balance) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF5F7FF), // Subtle blue tint from image
                    shape = RoundedCornerShape(32.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E7FF))
                ) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(56.dp), 
                            shape = RoundedCornerShape(16.dp), 
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(28.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "DESTINATION BANK", 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Black, 
                                color = Color(0xFF818CF8), 
                                letterSpacing = 1.sp
                            )
                            Text(
                                "RAZORPAY CONNECTED BANK", 
                                fontSize = 15.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color(0xFF111827)
                            )
                            Text(
                                ".... ${if (lastFour.isNotEmpty()) lastFour.uppercase() else "F"}", 
                                fontSize = 13.sp, 
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onConfirm,
                    enabled = isValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isValid) Color(0xFF6366F1) else Color(0xFFA1A1AA).copy(alpha = 0.6f)
                    )
                ) {
                    Text(
                        "INITIATE TRANSFER", 
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold, 
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SettlementDetailsDialog(
    fullName: String, onFullNameChange: (String) -> Unit,
    accountNumber: String, onAccountNumberChange: (String) -> Unit,
    routingNumber: String, onRoutingChange: (String) -> Unit,
    onDismiss: () -> Unit, onSave: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(40.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(32.dp)) {
                Text(
                    text = "SETTLEMENT DETAILS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF111827)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                SettlementTextField(label = "ACCOUNT HOLDER NAME", value = fullName, onValueChange = onFullNameChange, placeholder = "f")
                Spacer(modifier = Modifier.height(20.dp))
                SettlementTextField(label = "BANK NAME", value = "Razorpay Connected Bank", onValueChange = {}, placeholder = "", enabled = false)
                Spacer(modifier = Modifier.height(20.dp))
                SettlementTextField(label = "ACCOUNT NUMBER", value = accountNumber, onValueChange = onAccountNumberChange, placeholder = "f")
                Spacer(modifier = Modifier.height(20.dp))
                SettlementTextField(label = "IFSC / SWIFT CODE", value = routingNumber, onValueChange = onRoutingChange, placeholder = "d")
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5046E5))
                ) {
                    Text("SECURE SAVE DETAILS", fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("CANCEL", fontWeight = FontWeight.ExtraBold, color = Color(0xFF9CA3AF), letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
fun SettlementTextField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, enabled: Boolean = true) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9CA3AF),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF9FAFB),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
        ) {
            Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 24.dp)) {
                if (value.isEmpty()) {
                    Text(placeholder, color = Color.LightGray, fontSize = 16.sp)
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827)),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
fun RevenueStatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconColor: Color, modifier: Modifier = Modifier, valueColor: Color = Color(0xFF111827)) {
    Card(
        modifier = modifier.height(170.dp),
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
        }
    }
}

@Composable
fun TransactionLogRow(transaction: BillingTransaction) {
    val isRefund = transaction.title.contains("REFUND", ignoreCase = true)
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isRefund) Color(0xFFFEF2F2) else Color(0xFFECFDF5),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRefund) Icons.Default.ReceiptLong else Icons.Default.ArrowOutward,
                    contentDescription = null,
                    tint = if (isRefund) Color(0xFFEF4444) else Color(0xFF10B981),
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = transaction.date,
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
            
            Text(
                text = transaction.amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = if (isRefund) Color(0xFFEF4444) else Color(0xFF10B981)
            )
        }
    }
}


@Preview
@Composable
fun RevenueHubPreview() {
    RevenueHubScreen(
        tutorId = 1,
        onNavigateBack = {},
        onNavigateToDashboard = {},
        onNavigateToAccount = {},
        availableBalance = 0.0,
        fullName = "John Doe",
        onFullNameChange = {},
        accountNumber = "1234567890",
        onAccountNumberChange = {},
        routingNumber = "IFSC001",
        onRoutingChange = {},
        transactions = emptyList()
    )
}
