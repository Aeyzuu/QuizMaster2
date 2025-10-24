package com.quizmaster.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Quiz(
    var id: String = "",
    val title: String = "",
    val category: String = "",
    val questions: List<Question> = emptyList()
) : Parcelable
