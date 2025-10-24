package com.quizmaster.retrofit

import com.quizmaster.Models.QuestionStats
import retrofit2.Call
import retrofit2.http.GET

interface QuestionStatsService{
    @GET("api_count_global.php")
    fun getData():Call<QuestionStats>
}