package com.example.azuretutorapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// ================= COURSES RESPONSE =================

data class CoursesResponse(
    val courses: List<ApiCourse>
)

data class ApiCourse(
    val id: Int,
    val title: String,
    val tutor_email: String,
    val tutor_name: String? = null,
    val subject: String,
    val price: String,
    val start_date: String,
    val start_time: String,
    val meeting_url: String,
    val curriculum: String,
    val professor_message: String,
    val duration_minutes: Int? = null,
    val frequency: String? = null,
    val class_type: String? = null,
    val max_students: Int? = null,
    val enrolled_count: Int = 0
)

// ================= LOGIN =================

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val email: String,
    val message: String,
    val role: String,
    val user_id: Int
)

// ================= VERIFY PAYMENT =================

data class VerifyPaymentRequest(
    val student_id: Int,
    val course_id: Int
)

data class VerifyPaymentResponse(
    val message: String
)

// ================= ENROLL =================

data class EnrollRequest(
    val student_id: Int,
    val course_id: Int
)

// ================= BANK DETAILS =================

data class BankDetailsRequest(
    val tutor_id: Int,
    val account_holder_name: String,
    val account_number: String,
    val ifsc_code: String,
    val bank_name: String = "",
    val upi_id: String = ""
)

data class BankDetailsResponse(
    val account_holder_name: String,
    val account_number: String,
    val ifsc_code: String,
    val pan_number: String? = null
)

// ================= OTP =================

data class SendOtpRequest(
    val email: String
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

// ================= COURSE RESPONSE =================

data class CreateCourseResponse(
    val message: String,
    val course_id: Int? = null
)

data class GeneralResponse(
    val message: String
)

// ================= API SERVICE =================

interface ApiService {

    @GET("get_all_courses")
    suspend fun getCourses(): Response<CoursesResponse>

    @POST("create_course")
    suspend fun createCourse(
        @Body request: CreateCourseRequest
    ): Response<CreateCourseResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("verify_payment")
    suspend fun verifyPayment(
        @Body request: VerifyPaymentRequest
    ): Response<VerifyPaymentResponse>

    @PUT("edit_course")
    suspend fun editCourse(
        @Body request: EditCourseRequest
    ): Response<GeneralResponse>

    @DELETE("delete_course/{course_id}")
    suspend fun deleteCourse(
        @Path("course_id") courseId: Int
    ): Response<GeneralResponse>

    @POST("enroll_student")
    suspend fun enrollStudent(
        @Body request: EnrollRequest
    ): Response<GeneralResponse>

    @GET("explore_courses/{student_id}")
    suspend fun exploreCourses(
        @Path("student_id") studentId: Int
    ): Response<CoursesResponse>

    @GET("available_courses/{student_id}")
    suspend fun availableCourses(
        @Path("student_id") studentId: Int
    ): Response<CoursesResponse>

    @GET("tutor_earnings/{tutor_id}")
    suspend fun getTutorEarnings(
        @Path("tutor_id") tutorId: Int
    ): Response<TutorEarningsResponse>

    @POST("save_bank_details")
    suspend fun saveBankDetails(
        @Body request: BankDetailsRequest
    ): Response<GeneralResponse>

    @GET("get_bank_details/{tutor_id}")
    suspend fun getBankDetails(
        @Path("tutor_id") tutorId: Int
    ): Response<BankDetailsResponse>

    @POST("send_otp")
    suspend fun sendOtp(
        @Body request: SendOtpRequest
    ): Response<GeneralResponse>

    @POST("verify_otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<GeneralResponse>

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<GeneralResponse>
}