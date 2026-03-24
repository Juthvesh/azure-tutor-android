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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    fullName: String,
    email: String,
    currentUserRole: UserRole,
    onUpdateProfile: (String) -> Unit
) {
    val backgroundGray = Color(0xFFF9FAFB)
    val primaryPurple = Color(0xFF6366F1)
    
    var localFullName by remember { mutableStateOf(fullName) }
    var localEmail by remember { mutableStateOf(email) }
    var tagline by remember { mutableStateOf("") }
    var aboutMe by remember { mutableStateOf("") }
    val phoneNumber = "7075747045"

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
                EditProfileHeader(onNavigateBack)
            }

            // Form Content
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-30).dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Profile Picture and Badge
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box {
                            Surface(
                                modifier = Modifier.size(110.dp),
                                shape = RoundedCornerShape(28.dp),
                                color = primaryPurple
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(localFullName.take(1).uppercase(), color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            // Edit Icon
                            Surface(
                                modifier = Modifier
                                    .size(34.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 10.dp, y = 10.dp),
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 4.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF1E293B), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Surface(
                            color = if (currentUserRole == UserRole.STUDENT) Color(0xFFFEF9C3) else Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = if (currentUserRole == UserRole.STUDENT) "📚 Student" else "👨‍🏫 Tutor",
                                color = if (currentUserRole == UserRole.STUDENT) Color(0xFF854D0E) else Color(0xFF166534),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Personal Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            ProfileInputField(
                                label = "Full Name",
                                value = localFullName,
                                onValueChange = { localFullName = it },
                                placeholder = "James Anderson"
                            )
                            
                            Column {
                                ProfileInputField(
                                    label = "Email Address",
                                    value = localEmail,
                                    onValueChange = { },
                                    placeholder = localEmail,
                                    readOnly = true
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Email cannot be changed",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            
                            ProfileInputField(
                                label = "Tagline",
                                value = tagline,
                                onValueChange = { tagline = it },
                                placeholder = "e.g., Passionate educator specializing i"
                            )
                            
                            Column {
                                Text(
                                    text = "About Me",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = aboutMe,
                                    onValueChange = { aboutMe = it },
                                    placeholder = { 
                                        Text(
                                            "Tell students about yourself, your teaching style, and experience...",
                                            color = Color.LightGray,
                                            fontSize = 14.sp
                                        ) 
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = Color(0xFFF3F4F6),
                                        focusedBorderColor = primaryPurple,
                                        unfocusedContainerColor = Color.White,
                                        focusedContainerColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Phone Verification
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = Color(0xFFDCFCE7)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.CheckCircle, 
                                        contentDescription = null, 
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Phone Verified", 
                                    fontWeight = FontWeight.Bold, 
                                    color = Color(0xFF166534),
                                    fontSize = 15.sp
                                )
                                Text(
                                    phoneNumber, 
                                    color = Color(0xFF16A34A), 
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(50),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "✓ Verified", 
                                        color = Color(0xFF16A34A), 
                                        fontSize = 12.sp, 
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }

                    // Action Buttons
                    Button(
                        onClick = { 
                            onUpdateProfile(localFullName)
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryPurple)
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }

                    // Danger Zone
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
                            Text(
                                "DANGER ZONE", 
                                modifier = Modifier.padding(horizontal = 16.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                letterSpacing = 1.sp
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        OutlinedButton(
                            onClick = { /* TODO */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.Red),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delete Account", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            }
                        }
                    }

                    // Footer Credits
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Your profile information helps others learn more about you.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "All changes are saved securely.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(Color(0xFF6366F1)) // Solid Purple as in "Tutor Hub"
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Edit Profile",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    readOnly: Boolean = false
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFF3F4F6),
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedContainerColor = if (readOnly) Color(0xFFF9FAFB) else Color.White,
                focusedContainerColor = if (readOnly) Color(0xFFF9FAFB) else Color.White,
                focusedTextColor = if (readOnly) Color.Gray else Color(0xFF1E293B),
                unfocusedTextColor = if (readOnly) Color.Gray else Color(0xFF1E293B)
            )
        )
    }
}

@Preview
@Composable
fun EditProfilePreview() {
    EditProfileScreen(
        onNavigateBack = {},
        fullName = "James Anderson",
        email = "james@gmail.com",
        currentUserRole = UserRole.STUDENT,
        onUpdateProfile = {}
    )
}
