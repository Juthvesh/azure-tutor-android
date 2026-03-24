package com.example.azuretutorapp

data class EditCourseRequest(
    val id: Int,
    val title: String,
    val subject: String,
    val start_date: String,
    val start_time: String,
    val duration_minutes: Int,
    val meeting_url: String,
    val class_type: String,
    val frequency: String,
    val price: String,
    val curriculum: String,
    val professor_message: String
)
