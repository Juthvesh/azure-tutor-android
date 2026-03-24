package com.example.azuretutorapp

data class CreateCourseRequest(
    val user_id: Int,
    val title: String,
    val subject: String,
    val price: String,
    val date: String,
    val time: String,
    val meeting_url: String,
    val curriculum: String,
    val professor_message: String,
    val tutor_email: String,
    val duration: Int,
    val class_type: String,
    val frequency: String,
    val max_students: Int
)