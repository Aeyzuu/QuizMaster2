package com.quizmaster.data

data class Quiz(
    val id: String = "",
    val name: String = "",
    val teacherId: String = "",
    val questions: List<Question> = emptyList(),
    val isPublished: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
