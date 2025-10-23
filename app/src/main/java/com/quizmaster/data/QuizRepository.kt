package com.quizmaster.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*

class QuizRepository {

    private val quizzesRef = FirebaseDatabase.getInstance().reference.child("Quizzes")

    // LiveData for questions fetched (teacher)
    val fetchedQuestions = MutableLiveData<List<Question>>()

    // LiveData for published quizzes (student)
    private val _availableQuizzes = MutableLiveData<List<Quiz>>()
    val availableQuizzes: LiveData<List<Quiz>> = _availableQuizzes

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> get() = _statusMessage

    init {
        setupQuizListener()
    }

    fun getPublishedQuizzes(): LiveData<List<Quiz>> = availableQuizzes

    fun fetchQuestions(amount: Int, categoryId: Int, difficulty: String) {
        val mockQuestions = listOf(
            Question(
                category = "Geography",
                difficulty = difficulty,
                type = "multiple",
                question = "What is the capital of France?",
                correctAnswer = "Paris",
                incorrectAnswers = listOf("London", "Berlin", "Rome"),
                allAnswers = listOf("Paris", "London", "Berlin", "Rome").shuffled()
            ),
            Question(
                category = "Geography",
                difficulty = difficulty,
                type = "multiple",
                question = "Which country contains the Great Barrier Reef?",
                correctAnswer = "Australia",
                incorrectAnswers = listOf("Mexico", "Brazil", "Indonesia"),
                allAnswers = listOf("Australia", "Mexico", "Brazil", "Indonesia").shuffled()
            )
        )
        fetchedQuestions.postValue(mockQuestions)
    }

    fun saveQuiz(title: String, teacherId: String, questions: List<Question>) {
        val newQuizId = quizzesRef.push().key ?: return
        val newQuiz = Quiz(
            id = newQuizId,
            name = title,
            teacherId = teacherId,
            questions = questions,
            isPublished = true
        )
        quizzesRef.child(newQuizId).setValue(newQuiz)
        _statusMessage.postValue("Quiz \"$title\" published successfully!")
    }

    private fun setupQuizListener() {
        quizzesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val quizList = mutableListOf<Quiz>()
                for (quizSnapshot in snapshot.children) {
                    val quiz = quizSnapshot.getValue(Quiz::class.java)
                    if (quiz != null && quiz.isPublished) quizList.add(quiz)
                }
                _availableQuizzes.postValue(quizList)
            }

            override fun onCancelled(error: DatabaseError) {
                _availableQuizzes.postValue(emptyList())
                _statusMessage.postValue("Error fetching quizzes: ${error.message}")
            }
        })
    }
}
