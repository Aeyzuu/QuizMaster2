// File: com/quizmaster/data/QuizDataModels.kt

package com.quizmaster.data

import com.quizmaster.api.Question // <-- ADD THIS IMPORT 🚨

data class Quiz(
    val id: String = "",
    val name: String = "",
    val teacherId: String = "",
    val questions: List<Question> = emptyList(), // Now this refers to com.quizmaster.api.Question
    val isPublished: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
// ⚠️ NO other 'data class Question' should exist here.