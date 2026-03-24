package com.example.azuretutorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.CheckCircle
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

@Composable
fun PaymentReceiptScreen(
    transaction: BillingTransaction,
    onClose: () -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
    )
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(40.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close Button
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(40.dp)
                            .background(Color(0xFFF3F4F6), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = textSecondary)
                    }
                }

                // Receipt Icon
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "PAYMENT RECEIPT",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = textPrimary
                )
                Text(
                    text = "TRANSACTION SUCCESS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textSecondary,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Course and Date
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ENROLLED COURSE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                        Text(transaction.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("DATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                        Text(transaction.date, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color(0xFFF3F4F6))
                Spacer(modifier = Modifier.height(24.dp))

                // Breakdown
                PriceRow("Base Price", transaction.amount)
                Spacer(modifier = Modifier.height(12.dp))
                PriceRow("Platform Fee (5%)", "$2.50") // Simulated fee

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL PAID", fontSize = 18.sp, fontWeight = FontWeight.Black, color = textPrimary)
                    Text(transaction.amount, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF6366F1))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Payment Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color.White) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = textSecondary, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("PAYMENT METHOD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                                Text("Visa •••• 4242", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color.White) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("REFERENCE ID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                                Text(transaction.transactionId, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Share Button
                TextButton(
                    onClick = { /* Handle Share */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "SHARE RECEIPT",
                        color = Color(0xFF6366F1),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PriceRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF9CA3AF))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
    }
}

@Preview
@Composable
fun PaymentReceiptScreenPreview() {
    PaymentReceiptScreen(
        transaction = BillingTransaction("Physics for Engineers", "FEB 16, 2026", "TRX-82910", "$49.99", TransactionStatus.SUCCESS),
        onClose = {}
    )
}
