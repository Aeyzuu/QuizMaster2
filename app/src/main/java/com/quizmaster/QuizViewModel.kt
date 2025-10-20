package com.quizmaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizmaster.api.RetrofitClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import com.quizmaster.api.Question
import com.quizmaster.data.Quiz

class QuizViewModel : ViewModel() {

    private val _fetchedQuestions = MutableLiveData<List<Question>>()
    val fetchedQuestions: LiveData<List<Question>> = _fetchedQuestions

    // LiveData for the student to observe available quizzes
    private val _availableQuizzes = MutableLiveData<List<Quiz>>()
    val availableQuizzes: LiveData<List<Quiz>> = _availableQuizzes

    // Firebase reference pointing to the root "Quizzes" node
    private val databaseRef = FirebaseDatabase.getInstance().getReference("Quizzes")

    /**
     * Clears the list of questions currently being held in the ViewModel.
     * Use after a quiz is successfully saved.
     */
    fun clearQuestions() {
        _fetchedQuestions.value = emptyList()
    }

    /**
     * Fetches trivia questions from the OpenTDB API.
     */
    fun fetchTriviaQuestions(amount: Int = 10, categoryId: Int? = null, difficulty: String? = null) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getQuestions(
                    amount = amount,
                    category = categoryId,
                    difficulty = difficulty
                )

                if (response.isSuccessful) {
                    val triviaResponse = response.body()
                    if (triviaResponse != null && triviaResponse.responseCode == 0) {
                        // Success: responseCode is 0
                        _fetchedQuestions.value = triviaResponse.results
                    } else {
                        // API returned an error code (1, 2, 3, etc. or a null body)
                        // You can use a Toast or Log to display the error code for debugging
                        _fetchedQuestions.value = emptyList()
                    }
                } else {
                    // HTTP failure (e.g., 404, 500)
                    _fetchedQuestions.value = emptyList()
                }
            } catch (e: Exception) {
                // Log network error and clear questions
                _fetchedQuestions.value = emptyList()
            }
        }
    }

    /**
     * Saves a new quiz to the Firebase Realtime Database.
     */
    fun saveQuiz(quizName: String, teacherId: String, questions: List<Question>) {
        val quizId = databaseRef.push().key ?: return // Generate unique ID

        val quizData = Quiz(
            id = quizId,
            name = quizName,
            teacherId = teacherId,
            questions = questions,
            isPublished = true // Publish quiz immediately
        )

        databaseRef.child(quizId).setValue(quizData)
            .addOnSuccessListener {
                // Success: Quiz saved. You can add logging or success handling here.
            }
            .addOnFailureListener {
                // Failure: Handle save error (e.g., show Toast)
            }
    }

    /**
     * Fetches all quizzes marked as 'isPublished=true' for student viewing.
     */
    fun fetchAvailableQuizzes() {
        // Query Firebase for Quizzes where isPublished == true
        databaseRef.orderByChild("isPublished").equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val quizzes = mutableListOf<Quiz>()
                    for (quizSnapshot in snapshot.children) {
                        // Use the Quiz data class to deserialize the data
                        val quiz = quizSnapshot.getValue(Quiz::class.java)
                        if (quiz != null) {
                            quizzes.add(quiz)
                        }
                    }
                    _availableQuizzes.value = quizzes
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error (e.g., log error.message)
                    _availableQuizzes.value = emptyList()
                }
            })
    }
}