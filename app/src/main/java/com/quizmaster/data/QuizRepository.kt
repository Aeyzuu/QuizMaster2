package com.quizmaster.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import java.net.URLDecoder

class QuizRepository {

    private val quizzesRef = FirebaseDatabase.getInstance().reference.child("Quizzes")
    private val triviaService = RetrofitInstance.api

    private val _availableQuizzes = MutableLiveData<List<Quiz>>()
    val availableQuizzes: LiveData<List<Quiz>> = _availableQuizzes

    init {
        setupQuizListener()
    }

    suspend fun getTriviaQuestions(amount: Int, categoryId: Int, difficulty: String): Pair<String, List<Question>> {
        val response = triviaService.getQuestions(amount, categoryId, difficulty)
        if (response.isSuccessful && response.body() != null) {
            val triviaQuestions = response.body()!!.results
            if (triviaQuestions.isNotEmpty()) {
                val categoryName = URLDecoder.decode(triviaQuestions.first().category, "UTF-8")
                val questions = triviaQuestions.map { triviaQuestion ->
                    val decodedQuestion = URLDecoder.decode(triviaQuestion.question, "UTF-8")
                    val correctAnswer = URLDecoder.decode(triviaQuestion.correct_answer, "UTF-8")
                    val incorrectAnswers = triviaQuestion.incorrect_answers.map { URLDecoder.decode(it, "UTF-8") }

                    val allAnswers = (incorrectAnswers + correctAnswer).shuffled()
                    Question(
                        question = decodedQuestion,
                        option1 = allAnswers.getOrElse(0) { "" },
                        option2 = allAnswers.getOrElse(1) { "" },
                        option3 = allAnswers.getOrElse(2) { "" },
                        option4 = allAnswers.getOrElse(3) { "" },
                        answer = correctAnswer
                    )
                }
                return Pair(categoryName, questions)
            }
        }
        throw Exception("API Call failed or returned no questions. Code: ${response.code()}")
    }

    suspend fun saveQuiz(quiz: Quiz): Boolean {
        val newQuizId = quiz.id.ifBlank { quizzesRef.push().key ?: return false }
        val quizToSave = quiz.copy(id = newQuizId)

        return try {
            quizzesRef.child(newQuizId).setValue(quizToSave).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateQuiz(quiz: Quiz): Boolean {
        return try {
            quizzesRef.child(quiz.id).setValue(quiz).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun setupQuizListener() {
        quizzesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val quizList = mutableListOf<Quiz>()
                for (quizSnapshot in snapshot.children) {
                    val quiz = quizSnapshot.getValue(Quiz::class.java)
                    if (quiz != null) quizList.add(quiz)
                }
                _availableQuizzes.postValue(quizList)
            }

            override fun onCancelled(error: DatabaseError) {
                _availableQuizzes.postValue(emptyList())
            }
        })
    }
}
