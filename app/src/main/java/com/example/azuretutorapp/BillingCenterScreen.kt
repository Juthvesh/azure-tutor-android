package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BillingTransaction(
    val title: String,
    val date: String,
    val transactionId: String,
    val amount: String,
    val status: TransactionStatus,
    val studentEmail: String = "",
    val tutorEmail: String = "",
    val isTutorTransaction: Boolean = false
)

enum class TransactionStatus {
    SUCCESS, FAILED
}

@Composable
fun BillingCenterScreen(
    onNavigateBack: () -> Unit,
    onViewReceipt: (BillingTransaction) -> Unit,
    transactions: List<BillingTransaction> = emptyList() // Added dynamic parameter
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
    )
    val surfaceColor = Color.White
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "Billing Center",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Secured Badge
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = (-20).dp),
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SECURED BY RAZORPAY CONNECT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }

            // White Background Container
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 20.dp),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                color = Color(0xFFF9FAFB)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 32.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(transaction, onViewReceipt)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: BillingTransaction, onViewReceipt: (BillingTransaction) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon
            val statusBgColor = if (transaction.status == TransactionStatus.SUCCESS) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
            val statusIconColor = if (transaction.status == TransactionStatus.SUCCESS) Color(0xFF10B981) else Color(0xFFEF4444)
            val icon = if (transaction.status == TransactionStatus.SUCCESS) Icons.Default.Check else Icons.Default.Close

            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = statusBgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = statusIconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = transaction.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = transaction.amount,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (transaction.status == TransactionStatus.SUCCESS) Color(0xFF111827) else Color(0xFFEF4444)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.date,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9CA3AF)
                    )
                    Text(
                        text = " • ",
                        color = Color(0xFFD1D5DB)
                    )
                    Text(
                        text = transaction.transactionId,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                }

                if (transaction.status == TransactionStatus.SUCCESS) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.clickable { onViewReceipt(transaction) },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEEF2FF)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = Color(0xFF4F46E5),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "RECEIPT",
                                color = Color(0xFF4F46E5),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BillingCenterScreenPreview() {
    BillingCenterScreen(onNavigateBack = {}, onViewReceipt = {})
}
