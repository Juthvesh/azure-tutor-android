package com.example.azuretutorapp

data class TutorEarningsResponse(
    val total_earnings: Double,
    val transactions: List<EarningTransaction>
)

data class EarningTransaction(
    val course_id: Int,
    val student_id: Int,
    val amount: Double,
    val platform_fee: Double,
    val tutor_earning: Double,
    val created_at: String
)