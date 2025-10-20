package com.quizmaster

data class User(
    val uid: String = "",
    val name: String? = null,
    val email: String = "",
    val role: String = "",
    val studentId: String? = null  // null by default → not written for teachers
)