package com.quizmaster.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// NOTE: You must have your Quiz data class defined in com.quizmaster.data
// data class Quiz(val id: String = "", val name: String = "", ...)

class QuizRepository {
    private val quizzesRef = FirebaseDatabase.getInstance().getReference("Quizzes")

    fun getPublishedQuizzes(): LiveData<List<Quiz>> {
        val liveData = MutableLiveData<List<Quiz>>()

        // Query Firebase for quizzes where isPublished is true
        quizzesRef.orderByChild("isPublished").equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val quizzes = mutableListOf<Quiz>()
                    for (quizSnapshot in snapshot.children) {
                        val quiz = quizSnapshot.getValue(Quiz::class.java)
                        quiz?.let { quizzes.add(it) }
                    }
                    liveData.value = quizzes
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors, e.g., permission denied
                    liveData.value = emptyList()
                    // Log.e("QuizRepository", "Failed to load quizzes: ${error.message}")
                }
            })
        return liveData
    }
}