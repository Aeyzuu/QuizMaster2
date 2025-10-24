package com.quizmaster.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quizmaster.data.Question
import com.quizmaster.data.Quiz
import com.quizmaster.data.QuizRepository
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

    // For HomeTeacherFragment (API-based quiz creation)
    private val _fetchedQuestions = MutableLiveData<List<Question>>()
    val fetchedQuestions: LiveData<List<Question>> = _fetchedQuestions

    private val _fetchedCategory = MutableLiveData<String>()
    val fetchedCategory: LiveData<String> = _fetchedCategory

    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?> = _statusMessage

    // For AddQuizFragment (manual quiz creation)
    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    // For fragments that need to observe update status
    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> = _updateStatus

    val quizzes: LiveData<List<Quiz>> = repository.availableQuizzes

    fun fetchTriviaQuestions(amount: Int, categoryId: Int, difficulty: String) {
        viewModelScope.launch {
            try {
                val (category, questions) = repository.getTriviaQuestions(amount, categoryId, difficulty)
                _fetchedCategory.postValue(category)
                _fetchedQuestions.postValue(questions)
            } catch (e: Exception) {
                _statusMessage.postValue("Failed to fetch questions: ${e.message}")
            }
        }
    }

    fun saveQuiz(quiz: Quiz) {
        viewModelScope.launch {
            val success = repository.saveQuiz(quiz)
            _saveStatus.postValue(success)
            if (success) {
                _statusMessage.postValue("Quiz saved successfully!")
                clearFetchedData()
            } else {
                _statusMessage.postValue("Failed to save quiz.")
            }
        }
    }

    fun updateQuiz(quiz: Quiz) {
        viewModelScope.launch {
            val success = repository.updateQuiz(quiz)
            _updateStatus.postValue(success)
        }
    }

    fun clearFetchedData() {
        _fetchedQuestions.value = emptyList()
        _fetchedCategory.value = ""
    }

    class QuizViewModelFactory(private val repository: QuizRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return QuizViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
