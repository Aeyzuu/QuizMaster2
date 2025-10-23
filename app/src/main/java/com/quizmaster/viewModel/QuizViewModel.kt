package com.quizmaster.viewModel

import androidx.lifecycle.*
import com.quizmaster.data.*

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

    val fetchedQuestions: LiveData<List<Question>> = repository.fetchedQuestions
    val availableQuizzes: LiveData<List<Quiz>> = repository.getPublishedQuizzes()
    val statusMessage: LiveData<String> = repository.statusMessage

    fun fetchTriviaQuestions(amount: Int, categoryId: Int, difficulty: String) =
        repository.fetchQuestions(amount, categoryId, difficulty)

    fun saveQuiz(title: String, teacherId: String, questions: List<Question>) =
        repository.saveQuiz(title, teacherId, questions)

    fun clearQuestions() {
        (repository.fetchedQuestions as? MutableLiveData<List<Question>>)?.value = emptyList()
    }

    class Factory(private val repository: QuizRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return QuizViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
