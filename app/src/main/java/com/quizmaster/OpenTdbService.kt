package com.quizmaster.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName
// --- 1. Data Classes for OpenTDB Response ---

data class TriviaResponse(
    @SerializedName("response_code")
    val responseCode: Int,

    @SerializedName("results")
    val results: List<Question>
)

data class Question(
    @SerializedName("category")
    val category: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("difficulty")
    val difficulty: String,

    @SerializedName("question")
    val question: String,

    // 💡 THE FIX: Maps API's "correct_answer" to Kotlin's "correctAnswer"
    @SerializedName("correct_answer")
    val correctAnswer: String,

    @SerializedName("incorrect_answers")
    val incorrectAnswers: List<String>
)

// --- 2. Retrofit Interface ---

interface OpenTdbService {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int? = null, // Optional category ID
        @Query("difficulty") difficulty: String? = null, // Optional: easy, medium, hard
        @Query("type") type: String? = "multiple" // Optional: multiple, boolean
    ): Response<TriviaResponse>
}