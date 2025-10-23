package com.quizmaster.data

import com.google.gson.annotations.SerializedName

/**
 * Data class for a single question.
 * Uses @SerializedName for API fetching (e.g., OpenTDB) and defaults for Firebase.
 */
data class Question(
    @SerializedName("category")
    val category: String = "",

    @SerializedName("type")
    val type: String = "",

    @SerializedName("difficulty")
    val difficulty: String = "",

    @SerializedName("question")
    val question: String = "",

    @SerializedName("correct_answer")
    val correctAnswer: String = "",

    @SerializedName("incorrect_answers")
    val incorrectAnswers: List<String> = emptyList(),

    // Utility field for holding all answers (shuffled)
    val allAnswers: List<String> = emptyList()
)