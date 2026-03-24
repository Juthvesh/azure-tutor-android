package com.example.azuretutorapp
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.LaunchedEffect
import java.time.format.DateTimeFormatter
import java.util.Locale


data class UserProfile(
    val email: String,
    val role: UserRole,
    var fullName: String = "",
    var availableBalance: Double = 0.0,
    var isRazorpayConnected: Boolean = false,
    var accountNumber: String = "",
    var routingNumber: String = "",
    var panTaxId: String = ""
)

// Helper to convert ApiCourse to Course model
fun mapApiCourseToCourse(course: ApiCourse): Course {
    val formattedTime = try {
        val timeValue = course.start_time
        if (timeValue.contains(":")) {
            val parts = timeValue.split(":")
            val hours = parts[0].padStart(2, '0')
            val minutes = if (parts.size > 1) parts[1].padStart(2, '0') else "00"
            val ldTime = java.time.LocalTime.parse("$hours:$minutes")
            ldTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
        } else {
            timeValue
        }
    } catch (e: Exception) {
        course.start_time
    }

    return Course(
        id = course.id.toString(),
        backendId = course.id,
        title = course.title,
        tutorName = course.tutor_name ?: course.tutor_email,
        tutorEmail = course.tutor_email,
        category = course.subject,
        enrolledCount = course.enrolled_count ?: 0,
        price = course.price.toDoubleOrNull() ?: 0.0,
        date = course.start_date,
        time = formattedTime,
        durationMins = course.duration_minutes ?: 60,
        meetingUrl = course.meeting_url,
        classType = course.class_type ?: "1-ON-1",
        frequency = course.frequency ?: "WEEKLY",
        maxStudents = course.max_students ?: 1,
        curriculum = course.curriculum,
        professorMessage = course.professor_message,
        imageGradientColors = listOf(
            Color(0xFF6366F1),
            Color(0xFF8B5CF6)
        )
    )
}

@Composable
fun AzureTutorApp() {
    var checkScreen by remember { mutableStateOf<NavScreen>(NavScreen.SignIn) }
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedCourseForPayment by remember { mutableStateOf<Course?>(null) }
    var selectedCourseForDetail by remember { mutableStateOf<Course?>(null) }
    var selectedCourseForEdit by remember { mutableStateOf<Course?>(null) }
    var selectedCourseForPortal by remember { mutableStateOf<Course?>(null) }

    // --- Multi-User Global State ---
    var currentUserEmail by remember { mutableStateOf("") }
    var currentUserId by remember { mutableIntStateOf(0) }
    var currentUserRole by remember { mutableStateOf(UserRole.STUDENT) }
    var registerName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPhone by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var registerRole by remember { mutableStateOf("student") }
    var registerGrade by remember { mutableStateOf("") }
    var otpFlow by remember { mutableStateOf("") }
    var resetEmail by remember { mutableStateOf("") }

    val userProfiles = remember {
        mutableStateListOf(
            UserProfile(
                email = "sarah@gmail.com",
                role = UserRole.TUTOR,
                fullName = "Dr. Sarah Smith",
                availableBalance = 3999.0, // Pre-populated to match existing course
                isRazorpayConnected = true,
                accountNumber = "XXXX-XXXX-1234"
            ),
            UserProfile(
                email = "james@gmail.com",
                role = UserRole.TUTOR,
                fullName = "James Anderson",
                availableBalance = 0.0,
                isRazorpayConnected = false
            ),
            UserProfile(
                email = "student@gmail.com",
                role = UserRole.STUDENT,
                fullName = "Student User"
            )
        )
    }

    // Function to get or create a profile
    fun getOrCreateProfile(email: String, role: UserRole): UserProfile {
        var profile = userProfiles.find { it.email == email && it.role == role }

        if (profile == null) {

            val newName = email.substringBefore("@").replaceFirstChar { ch: Char ->
                if (ch.isLowerCase()) {
                    ch.titlecase(java.util.Locale.getDefault())
                } else {
                    ch.toString()
                }
            }

            profile = UserProfile(
                email = email,
                role = role,
                fullName = newName
            )

            userProfiles.add(profile)
        }

        return profile
    }
    // Function to update the active profile
    fun updateActiveProfile(updater: (UserProfile) -> UserProfile) {
        val index = userProfiles.indexOfFirst { it.email == currentUserEmail && it.role == currentUserRole }
        if (index != -1) {
            userProfiles[index] = updater(userProfiles[index])
        }
    }

    // Active Profile Accessor
    val activeProfile = userProfiles.find { it.email == currentUserEmail && it.role == currentUserRole }
        ?: UserProfile(email = "", role = UserRole.STUDENT)

    val publishedCourses = remember {
        mutableStateListOf<Course>()
    }
    LaunchedEffect(Unit) {

        try {

            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.getCourses()
            }

            if (response.isSuccessful) {

                response.body()?.courses?.forEach { apiCourse ->
                    val backendCourse = mapApiCourseToCourse(apiCourse)
                    if (publishedCourses.none { it.id == backendCourse.id }) {
                        publishedCourses.add(backendCourse)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    val enrollments = remember { mutableStateListOf<Enrollment>() }
    val transactions = remember { mutableStateListOf<BillingTransaction>() }
    val reviews = remember { mutableStateListOf<CourseReview>() }

    // Forgot Password Flow State

    // Billing State
    var selectedTransaction by remember { mutableStateOf<BillingTransaction?>(null) }

    LaunchedEffect(currentUserId, currentUserRole) {
        if (currentUserId == 0) return@LaunchedEffect

        if (currentUserRole == UserRole.STUDENT) {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.apiService.availableCourses(currentUserId)
                    if (response.isSuccessful) {
                        val enrolledApiCourses = response.body()?.courses ?: emptyList()
                        val newEnrollments = enrolledApiCourses.map { c ->
                            Enrollment(
                                studentEmail = currentUserEmail,
                                courseId = c.id.toString(),
                                enrollmentDate = c.start_date ?: java.time.LocalDate.now().toString(),
                                isCompleted = false
                            )
                        }
                        
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            // Sync with global publishedCourses to ensure these courses exist in the UI
                            enrolledApiCourses.forEach { apiCourse ->
                                val course = mapApiCourseToCourse(apiCourse)
                                if (publishedCourses.none { it.id == course.id }) {
                                    publishedCourses.add(course)
                                }
                            }
                            
                            enrollments.removeIf { it.studentEmail == currentUserEmail }
                            enrollments.addAll(newEnrollments)
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("API", "Failed to fetch student enrollments: ${e.message}")
                }
            }
        } else if (currentUserRole == UserRole.TUTOR) {
            // Fetch bank details to persist verification status
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.apiService.getBankDetails(currentUserId)
                    if (response.isSuccessful) {
                        val details = response.body()
                        if (details != null) {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                updateActiveProfile { profile ->
                                    profile.copy(
                                        isRazorpayConnected = true,
                                        accountNumber = details.account_number
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("API", "Failed to fetch bank details: ${e.message}")
                }
            }
        }
    }

    androidx.compose.animation.Crossfade(
        targetState = checkScreen,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
    ) { currentScreen ->
        when (currentScreen) {
            NavScreen.SignIn -> {
            SignInScreen(
                onNavigateToCreateAccount = { checkScreen = NavScreen.CreateAccount },
                onNavigateToTutorDashboard = { email, userId ->
                    currentUserEmail = email
                    currentUserId = userId
                    currentUserRole = UserRole.TUTOR
                    checkScreen = NavScreen.TutorDashboard
                },
                onNavigateToStudentDashboard = { email, userId ->
                    currentUserEmail = email
                    currentUserId = userId
                    currentUserRole = UserRole.STUDENT
                    checkScreen = NavScreen.StudentDashboard
                },
                onNavigateToForgotPassword = { checkScreen = NavScreen.ForgotPassword }
            )
        }
            NavScreen.CreateAccount -> {
                CreateAccountScreen(
                    onNavigateToSignIn = { checkScreen = NavScreen.SignIn },
                    onNavigateToVerify = { name, email, phone, password, role, grade ->

                        registerName = name
                        registerEmail = email
                        registerPhone = phone
                        registerPassword = password
                        registerRole = role
                        registerGrade = grade

                        resetEmail = email // Store email for VerifyOtpScreen
                        otpFlow = "register"
                        checkScreen = NavScreen.VerifyOtp
                    }
                )
            }
        NavScreen.VerifyPhone -> {
            // Deprecated - switching to VerifyOtp for registration as well
            VerifyPhoneScreen(
                onNavigateBack = { checkScreen = NavScreen.CreateAccount },
                onVerifySuccess = { checkScreen = NavScreen.SignIn }
            )
        }
        NavScreen.TutorDashboard -> {
            val tutorCourses = publishedCourses.filter { it.tutorEmail.equals(currentUserEmail, ignoreCase = true) }
            val totalEarnings = tutorCourses.sumOf { it.price * it.enrolledCount }

            // Generate mock users and grab active one safely
            val currentTutor = getOrCreateProfile(currentUserEmail, currentUserRole)

            TutorDashboardScreen(
                onNavigateBack = { checkScreen = NavScreen.SignIn },
                onNavigateToRevenue = { checkScreen = NavScreen.RevenueHub },
                onNavigateToAccount = { checkScreen = NavScreen.Account },
                onNavigateToVerifyIdentity = { checkScreen = NavScreen.PayoutVerification },
                onNavigateToCreateCourse = { checkScreen = NavScreen.CreateCourse },
                onCourseClick = { course ->
                    selectedCourseForDetail = course
                    checkScreen = NavScreen.TutorCourseDetail
                },
                userName = currentTutor.fullName,
                publishedCourses = tutorCourses,
                isRazorpayConnected = currentTutor.isRazorpayConnected,
                totalEarnings = totalEarnings
            )
        }
        NavScreen.TutorCourseDetail -> {
            selectedCourseForDetail?.let { course ->
                TutorCourseDetailScreen(
                    course = course,
                    onNavigateBack = { checkScreen = NavScreen.TutorDashboard },
                    onEditCourse = { courseToEdit ->
                        selectedCourseForEdit = courseToEdit
                        checkScreen = NavScreen.EditCourse
                    },
                    onDeleteCourse = { courseId ->
                        val courseToDelete = publishedCourses.find { it.id == courseId }
                        if (courseToDelete != null) {
                            var totalRefunded = 0.0

                            // Logic for refunds: Issue refunds to all enrolled students
                            val enrolledStudents = enrollments.filter { it.courseId == courseId }
                            enrolledStudents.forEach { enrollment ->
                                val refundAmount = courseToDelete.price
                                totalRefunded += refundAmount

                                // Record refund for student
                                transactions.add(
                                    BillingTransaction(
                                        title = "REFUND: ${courseToDelete.title}",
                                        date = java.time.LocalDate.now().toString(),
                                        transactionId = "REF-" + java.util.UUID.randomUUID().toString().take(8).uppercase(),
                                        amount = "+₹${refundAmount.toInt()}",
                                        status = TransactionStatus.SUCCESS,
                                        studentEmail = enrollment.studentEmail
                                    )
                                )
                            }

                            // Deduct from tutor's available balance ONLY if the course was already marked completed
                            val completedRefundsAmount = enrolledStudents.filter { it.isCompleted }.sumOf { courseToDelete.price }
                            if (completedRefundsAmount > 0) {
                                updateActiveProfile {
                                    it.copy(availableBalance = (it.availableBalance - completedRefundsAmount).coerceAtLeast(0.0))
                                }
                            }

                            // Record it
                            if (totalRefunded > 0) {
                                transactions.add(
                                    BillingTransaction(
                                        title = "REFUND PAYOUT: ${courseToDelete.title}",
                                        date = java.time.LocalDate.now().toString(),
                                        transactionId = "TRF-" + java.util.UUID.randomUUID().toString().take(8).uppercase(),
                                        amount = "-₹${totalRefunded.toInt()}",
                                        status = TransactionStatus.SUCCESS,
                                        tutorEmail = currentUserEmail,
                                        isTutorTransaction = true
                                    )
                                )
                            }

                            // Remove course and associated enrollments
                            val bId = courseToDelete.backendId ?: courseId.toIntOrNull()
                            if (bId != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        RetrofitClient.apiService.deleteCourse(bId)
                                    } catch (e: Exception) {
                                        Log.e("API", "Delete course failed: ${e.message}")
                                    }
                                }
                            }
                            publishedCourses.removeIf { it.id == courseId }
                            enrollments.removeIf { it.courseId == courseId }
                        }
                        checkScreen = NavScreen.TutorDashboard
                    }
                )
            }
        }
        NavScreen.RevenueHub -> {

            RevenueHubScreen(
                tutorId = currentUserId,
                onNavigateBack = { checkScreen = NavScreen.TutorDashboard },
                onNavigateToDashboard = { checkScreen = NavScreen.TutorDashboard },
                onNavigateToAccount = { checkScreen = NavScreen.Account },
                availableBalance = activeProfile.availableBalance,
                fullName = activeProfile.fullName,
                onFullNameChange = { newName -> updateActiveProfile { it.copy(fullName = newName) } },
                accountNumber = activeProfile.accountNumber,
                onAccountNumberChange = { acc -> updateActiveProfile { it.copy(accountNumber = acc) } },
                routingNumber = activeProfile.routingNumber,
                onRoutingChange = { rtg -> updateActiveProfile { it.copy(routingNumber = rtg) } },
                isRazorpayConnected = activeProfile.isRazorpayConnected,
                transactions = transactions.filter { it.isTutorTransaction && it.tutorEmail == currentUserEmail }

            )
        }
        NavScreen.Account -> {
            val currentTutor = getOrCreateProfile(currentUserEmail, currentUserRole)
            AccountScreen(
                role = UserRole.TUTOR,
                userName = currentTutor.fullName,
                userEmail = currentUserEmail,
                onNavigateBack = { checkScreen = NavScreen.TutorDashboard },
                onNavigateToDashboard = { checkScreen = NavScreen.TutorDashboard },
                onNavigateToEarnings = { checkScreen = NavScreen.RevenueHub },
                onNavigateToEditProfile = { checkScreen = NavScreen.EditProfile },
                onNavigateToReviews = { checkScreen = NavScreen.Review },
                onLogout = { checkScreen = NavScreen.SignIn }
            )
        }
        NavScreen.Review -> {
            ReviewScreen(
                role = currentUserRole,
                currentUserEmail = currentUserEmail,
                courses = publishedCourses,
                enrollments = enrollments,
                reviews = reviews,
                onNavigateBack = {
                    checkScreen = if (currentUserRole == UserRole.TUTOR) NavScreen.Account else NavScreen.StudentAccount
                },
                onAddReview = { review ->
                    reviews.add(review)
                }
            )
        }
        NavScreen.EditProfile -> {
            EditProfileScreen(
                onNavigateBack = { checkScreen = if (currentUserRole == UserRole.TUTOR) NavScreen.Account else NavScreen.StudentAccount },
                fullName = activeProfile.fullName,
                email = currentUserEmail,
                currentUserRole = currentUserRole,
                onUpdateProfile = { newName ->
                    updateActiveProfile { it.copy(fullName = newName) }
                }
            )
        }
        NavScreen.PayoutVerification -> {
            PayoutVerificationScreen(
                onNavigateBack = { checkScreen = NavScreen.TutorDashboard },
                onRazorpayConnected = {
                    updateActiveProfile { it.copy(isRazorpayConnected = true) }
                    checkScreen = NavScreen.TutorDashboard
                },
                isRazorpayConnected = activeProfile.isRazorpayConnected,
                fullName = activeProfile.fullName,
                onFullNameChange = { newName -> updateActiveProfile { it.copy(fullName = newName) } },
                accountNumber = activeProfile.accountNumber,
                onAccountNumberChange = { acc -> updateActiveProfile { it.copy(accountNumber = acc) } },
                routingNumber = activeProfile.routingNumber,
                onRoutingChange = { rtg -> updateActiveProfile { it.copy(routingNumber = rtg) } },
                panTaxId = activeProfile.panTaxId,
                onPanChange = { pan -> updateActiveProfile { it.copy(panTaxId = pan) } }
            )
        }
        NavScreen.CreateCourse -> {
            CreateCourseScreen(
                onNavigateBack = { checkScreen = NavScreen.TutorDashboard },
                isRazorpayConnected = activeProfile.isRazorpayConnected,
                onNavigateToVerify = { checkScreen = NavScreen.PayoutVerification },
                onCourseDeployed = { newCourse ->

                    val subjectImage = when(newCourse.category.uppercase()) {
                        "MATHEMATICS" -> null
                        "PHYSICS" -> null
                        "PROGRAMMING" -> null
                        "COMPUTER SCIENCE" -> null
                        "MUSIC" -> null
                        "ARTS" -> null
                        else -> null
                    }

                    val courseWithOwner = newCourse.copy(
                        tutorEmail = currentUserEmail,
                        tutorName = activeProfile.fullName.ifBlank { "Tutor" },
                        imageUrl = subjectImage
                    )

                    // Convert to 24h time HH:mm:ss for MySQL
                    val formattedTimeForBackend = try {
                        val timeParsers = listOf(
                            DateTimeFormatter.ofPattern("hh:mm a", Locale.US),
                            DateTimeFormatter.ofPattern("h:mm a", Locale.US),
                            DateTimeFormatter.ofPattern("HH:mm"),
                            DateTimeFormatter.ofPattern("H:mm")
                        )
                        var parsedTime: java.time.LocalTime? = null
                        for (parser in timeParsers) {
                            try {
                                parsedTime = java.time.LocalTime.parse(courseWithOwner.time, parser)
                                break
                            } catch (e: Exception) {}
                        }
                        parsedTime?.format(DateTimeFormatter.ofPattern("HH:mm:ss")) ?: courseWithOwner.time
                    } catch (e: Exception) {
                        courseWithOwner.time
                    }

                    val request = CreateCourseRequest(
                    user_id = currentUserId,
                    title = courseWithOwner.title,
                    tutor_email = courseWithOwner.tutorEmail,
                    subject = courseWithOwner.category,
                    price = courseWithOwner.price.toString(),
                    date = courseWithOwner.date,
                    time = formattedTimeForBackend,
                    duration = courseWithOwner.durationMins,
                    meeting_url = courseWithOwner.meetingUrl,
                    curriculum = courseWithOwner.curriculum,
                    professor_message = courseWithOwner.professorMessage,
                    class_type = courseWithOwner.classType,
                    frequency = courseWithOwner.frequency,
                    max_students = courseWithOwner.maxStudents
                )
                    CoroutineScope(Dispatchers.IO).launch {

                        try {

                            val response = RetrofitClient.apiService.createCourse(request)

                            if (response.isSuccessful) {

                                val responseBody = response.body()

                                withContext(Dispatchers.Main) {

                                    val newCourse = courseWithOwner.copy(
                                        id = responseBody?.course_id?.toString() ?: java.util.UUID.randomUUID().toString(),
                                        backendId = responseBody?.course_id
                                    )

                                    publishedCourses.add(0, newCourse)

                                    android.widget.Toast.makeText(
                                        context,
                                        "Course deployed successfully!",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    android.widget.Toast.makeText(context, "Deployment failed: ${response.message()}", android.widget.Toast.LENGTH_LONG).show()
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                            }
                        }

                    }

                    checkScreen = NavScreen.TutorDashboard
                }
            )
        }
        NavScreen.EditCourse -> {
            selectedCourseForEdit?.let { courseToEdit ->
                EditCourseScreen(
                    initialCourse = courseToEdit,
                    onNavigateBack = { checkScreen = NavScreen.TutorCourseDetail },
                    onCourseUpdated = { updatedCourse ->
                        val bId = updatedCourse.backendId ?: updatedCourse.id.toIntOrNull()
                        if (bId != null) {
                            val editRequest = EditCourseRequest(
                                id = bId,
                                title = updatedCourse.title,
                                subject = updatedCourse.category,
                                start_date = updatedCourse.date,
                                start_time = updatedCourse.time,
                                duration_minutes = updatedCourse.durationMins,
                                meeting_url = updatedCourse.meetingUrl,
                                class_type = updatedCourse.classType,
                                frequency = updatedCourse.frequency,
                                price = updatedCourse.price.toString(),
                                curriculum = updatedCourse.curriculum,
                                professor_message = updatedCourse.professorMessage
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    RetrofitClient.apiService.editCourse(editRequest)
                                } catch (e: Exception) {
                                    Log.e("API", "Edit course failed: ${e.message}")
                                }
                            }
                        }

                        val index = publishedCourses.indexOfFirst { it.id == updatedCourse.id }
                        if (index != -1) {
                            publishedCourses[index] = updatedCourse
                        }

                        // Update the current selected detail to reflect changes immediately
                        selectedCourseForDetail = updatedCourse
                        checkScreen = NavScreen.TutorCourseDetail
                    }
                )
            }
        }
        NavScreen.StudentDashboard -> {
            val enrolledCourseIds = enrollments.filter { it.studentEmail == currentUserEmail }.map { it.courseId }
            val studentCourses = publishedCourses.filter { it.id in enrolledCourseIds }

            StudentDashboardScreen(
                enrolledCourses = studentCourses,
                onNavigateToSchedule = { checkScreen = NavScreen.StudentSchedule },
                onNavigateToAccount = { checkScreen = NavScreen.StudentAccount },
                onNavigateToBilling = { checkScreen = NavScreen.BillingCenter },
                onCourseClick = { course ->
                    selectedCourseForPortal = course
                    checkScreen = NavScreen.ClassroomPortal
                },
                onMarkCompleted = { courseId ->
                    val index = enrollments.indexOfFirst { it.studentEmail == currentUserEmail && it.courseId == courseId }
                    if (index != -1 && !enrollments[index].isCompleted) {
                        enrollments[index] = enrollments[index].copy(isCompleted = true)

                        // Add funds to tutor's available balance now that it's completed
                        val completedCourse = publishedCourses.find { it.id == courseId }
                        if (completedCourse != null) {
                            val tutorIndex = userProfiles.indexOfFirst { it.email == completedCourse.tutorEmail && it.role == UserRole.TUTOR }
                            if (tutorIndex != -1) {
                                userProfiles[tutorIndex] = userProfiles[tutorIndex].copy(
                                    availableBalance = userProfiles[tutorIndex].availableBalance + completedCourse.price
                                )
                            }
                        }

                        // Navigate to Review screen after marking completed
                        checkScreen = NavScreen.Review
                    }
                },
                onLogout = { checkScreen = NavScreen.SignIn }
            )
        }
        NavScreen.ClassroomPortal -> {
            selectedCourseForPortal?.let { course ->
                ClassroomPortalScreen(
                    course = course,
                    onNavigateBack = { checkScreen = NavScreen.StudentDashboard }
                )
            }
        }
        NavScreen.StudentSchedule -> {
            // Fresh list of unenrolled courses - fetched live from backend
            val exploreCoursesState = remember { mutableStateListOf<Course>() }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(currentUserId) {
                try {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.exploreCourses(currentUserId)
                    }
                    if (response.isSuccessful) {
                        val freshCourses = response.body()?.courses?.map { course ->
                            val formattedTime = try {
                                val timeValue = course.start_time
                                if (timeValue.contains(":")) {
                                    val parts = timeValue.split(":")
                                    val hours = parts[0].padStart(2, '0')
                                    val minutes = if (parts.size > 1) parts[1].padStart(2, '0') else "00"
                                    val ldTime = java.time.LocalTime.parse("$hours:$minutes")
                                    ldTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
                                } else timeValue
                            } catch (e: Exception) { course.start_time }

                            Course(
                                id = course.id.toString(),
                                backendId = course.id,
                                title = course.title,
                                tutorName = course.tutor_name ?: course.tutor_email,
                                tutorEmail = course.tutor_email,
                                category = course.subject,
                                enrolledCount = 0,
                                price = course.price.toDoubleOrNull() ?: 0.0,
                                date = course.start_date,
                                time = formattedTime,
                                durationMins = course.duration_minutes ?: 60,
                                meetingUrl = course.meeting_url,
                                classType = course.class_type ?: "1-ON-1",
                                frequency = course.frequency ?: "WEEKLY",
                                maxStudents = course.max_students ?: 1,
                                curriculum = course.curriculum,
                                professorMessage = course.professor_message,
                                imageGradientColors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                            )
                        } ?: emptyList()

                        exploreCoursesState.clear()
                        exploreCoursesState.addAll(freshCourses)
                    }
                } catch (e: Exception) {
                    Log.e("EXPLORE", "Failed to load explore courses: ${e.message}")
                } finally {
                    isLoading = false
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                StudentDiscoveryScreen(
                    onNavigateBack = { checkScreen = NavScreen.StudentDashboard },
                    publishedCourses = exploreCoursesState,
                    onEnroll = { course ->
                        val bId = course.backendId ?: course.id.toIntOrNull()
                        Log.d("PAYMENT_TRACE", "Enrolling: title=${course.title} backendId=${course.backendId} id=${course.id} price=${course.price}")
                        if (bId != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val enrollResponse = RetrofitClient.apiService.enrollStudent(EnrollRequest(currentUserId, bId))
                                    if (enrollResponse.isSuccessful || enrollResponse.code() == 200 || enrollResponse.code() == 201) {
                                        withContext(Dispatchers.Main) {
                                            selectedCourseForPayment = course
                                            checkScreen = NavScreen.CoursePayment
                                        }
                                    } else {
                                        Log.e("API", "Enrollment failed: HTTP ${enrollResponse.code()} for course_id=$bId")
                                    }
                                } catch (e: Exception) {
                                    Log.e("API", "Enrollment exception: ${e.message}")
                                }
                            }
                        }
                    },
                    onNavigateToHome = { checkScreen = NavScreen.StudentDashboard },
                    onNavigateToAccount = { checkScreen = NavScreen.StudentAccount },
                    enrollments = enrollments, 
                    reviews = reviews 
                )
            }
        }
        NavScreen.CoursePayment -> {
            selectedCourseForPayment?.let { course ->
                CoursePaymentScreen(
                    course = course,
                    studentId = currentUserId,
                    onNavigateBack = { checkScreen = NavScreen.StudentSchedule },
                    onPaymentSuccess = { paidCourse ->
                        // Perform actual enrollment after payment success
                        if (!enrollments.any { it.studentEmail == currentUserEmail && it.courseId == paidCourse.id }) {
                            enrollments.add(
                                Enrollment(
                                    studentEmail = currentUserEmail,
                                    courseId = paidCourse.id,
                                    enrollmentDate = java.time.LocalDate.now().toString()
                                )
                            )
                            // Record for student
                            transactions.add(
                                BillingTransaction(
                                    title = paidCourse.title,
                                    date = java.time.LocalDate.now().toString(),
                                    transactionId = "TXN-" + java.util.UUID.randomUUID().toString().take(8).uppercase(),
                                    amount = "₹${paidCourse.price.toInt()}",
                                    status = TransactionStatus.SUCCESS,
                                    studentEmail = currentUserEmail
                                )
                            )

                            // Also record earning for Tutor (total sales will reflect this, but not available balance until completed)
                            transactions.add(
                                BillingTransaction(
                                    title = "SESSIONS: ${paidCourse.title}",
                                    date = java.time.LocalDate.now().toString(),
                                    transactionId = "EARN-" + java.util.UUID.randomUUID().toString().take(8).uppercase(),
                                    amount = "+₹${paidCourse.price.toInt()}",
                                    status = TransactionStatus.SUCCESS,
                                    tutorEmail = paidCourse.tutorEmail,
                                    isTutorTransaction = true
                                )
                            )
                            checkScreen = NavScreen.StudentDashboard
                        }
                    }
                )
            }
        }
        NavScreen.StudentAccount -> {
            val currentStudent = getOrCreateProfile(currentUserEmail, currentUserRole)
            AccountScreen(
                role = UserRole.STUDENT,
                userName = currentStudent.fullName,
                userEmail = currentUserEmail,
                onNavigateBack = { checkScreen = NavScreen.StudentDashboard },
                onNavigateToDashboard = { checkScreen = NavScreen.StudentDashboard },
                onNavigateToEarnings = { checkScreen = NavScreen.StudentSchedule }, // For student, this goes to Schedule
                onNavigateToEditProfile = { checkScreen = NavScreen.EditProfile },
                onNavigateToReviews = { checkScreen = NavScreen.Review },
                onLogout = { checkScreen = NavScreen.SignIn }
            )
        }
        NavScreen.BillingCenter -> {
            BillingCenterScreen(
                onNavigateBack = { checkScreen = NavScreen.StudentDashboard },
                onViewReceipt = { transaction ->
                    selectedTransaction = transaction
                    checkScreen = NavScreen.PaymentReceipt
                },
                // Filtering transactions for the current student
                transactions = transactions.filter { !it.isTutorTransaction && it.studentEmail == currentUserEmail }
            )
        }
        NavScreen.PaymentReceipt -> {
            selectedTransaction?.let { transaction ->
                PaymentReceiptScreen(
                    transaction = transaction,
                    onClose = { checkScreen = NavScreen.BillingCenter }
                )
            }
        }
            NavScreen.ForgotPassword -> {
                ForgotPasswordScreen(
                    onNavigateBack = { checkScreen = NavScreen.SignIn },
                    onNavigateToSignIn = { checkScreen = NavScreen.SignIn },

                    onOtpSent = { email ->

                        resetEmail = email
                        otpFlow = "forgot"
                        checkScreen = NavScreen.VerifyOtp

                    }
                )
            }
            NavScreen.VerifyOtp -> {
                VerifyOtpScreen(
                    fullName = registerName,
                    email = resetEmail,
                    phoneNumber = registerPhone,
                    password = registerPassword,
                    role = registerRole,
                    grade = registerGrade,
                    otpFlow = otpFlow,

                    onNavigateBack = {
                        checkScreen =
                            if (currentUserEmail.isEmpty())
                                NavScreen.CreateAccount
                            else
                                NavScreen.ForgotPassword
                    },

                    onVerifySuccess = {

                        if (otpFlow == "register") {

                            // Registration success
                            checkScreen = NavScreen.SignIn

                            android.widget.Toast.makeText(
                                context,
                                "Registration complete! Please sign in.",
                                android.widget.Toast.LENGTH_LONG
                            ).show()

                        } else if (otpFlow == "forgot") {

                            // Forgot password flow
                            checkScreen = NavScreen.CreateNewPassword

                        }

                    }
                )
            }
            NavScreen.CreateNewPassword -> {
                CreateNewPasswordScreen(
                    email = resetEmail,
                    onNavigateBack = { checkScreen = NavScreen.VerifyOtp },
                    onResetSuccess = {
                        checkScreen = NavScreen.ResetSuccess
                    }
                )
            }
        NavScreen.ResetSuccess -> {
            ResetSuccessScreen(
                onNavigateToSignIn = { checkScreen = NavScreen.SignIn }
            )
        }
    }
    }
}

sealed class NavScreen {
    object SignIn : NavScreen()
    object CreateAccount : NavScreen()
    object VerifyPhone : NavScreen()
    object TutorDashboard : NavScreen()
    object RevenueHub : NavScreen()
    object Account : NavScreen()
    object PayoutVerification : NavScreen()
    object CreateCourse : NavScreen()
    object EditCourse : NavScreen()
    object EditProfile : NavScreen()
    object StudentDashboard : NavScreen()
    object StudentSchedule : NavScreen()
    object StudentAccount : NavScreen()
    object ForgotPassword : NavScreen()
    object VerifyOtp : NavScreen()
    object CreateNewPassword : NavScreen()
    object ResetSuccess : NavScreen()
    object BillingCenter : NavScreen()
    object PaymentReceipt : NavScreen()
    object CoursePayment : NavScreen()
    object Review : NavScreen()
    object TutorCourseDetail : NavScreen()
    object ClassroomPortal : NavScreen()
}
