package com.quizmaster.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Data class to model the JSON response from the API
data class TriviaApiResponse(
    val response_code: Int,
    val results: List<TriviaQuestion>
)

// Data class to model a single question from the API
data class TriviaQuestion(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)

interface TriviaApiService {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String,
        @Query("type") type: String = "multiple"
    ): Response<TriviaApiResponse>
}
