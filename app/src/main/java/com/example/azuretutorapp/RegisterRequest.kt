package com.example.azuretutorapp

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String,
    val grade: String
)