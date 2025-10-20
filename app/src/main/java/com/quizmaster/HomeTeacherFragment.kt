package com.quizmaster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.quizmaster.databinding.FragmentHomeTeacherBinding // Ensure this import is correct
import com.quizmaster.viewmodel.QuizViewModel
import com.google.firebase.auth.FirebaseAuth // Assuming you have Firebase Auth

class HomeTeacherFragment : Fragment() {

    // Use viewModels() to instantiate the ViewModel
    private val quizViewModel: QuizViewModel by viewModels()

    private var _binding: FragmentHomeTeacherBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeTeacherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Observe the questions fetched from the API
        quizViewModel.fetchedQuestions.observe(viewLifecycleOwner) { questions ->
            if (questions.isNotEmpty()) {
                // TODO: Update your UI (e.g., a RecyclerView) to show the questions 🔄
                Toast.makeText(context, "Fetched ${questions.size} trivia questions! Ready to save.", Toast.LENGTH_LONG).show()
                // Removed the previous auto-save demonstration logic here
            } else if (questions.isEmpty() && quizViewModel.fetchedQuestions.value != null) {
                // Only show this Toast if the value changed to empty (i.e., failed fetch)
                Toast.makeText(context, "Failed to fetch questions or no results.", Toast.LENGTH_LONG).show()
            }
        }

        // 2. Button click to fetch questions (calls OpenTDB API)
        binding.fetchQuestionsButton.setOnClickListener {
            // Fetch 10 multiple-choice questions (e.g., from the 'Geography' category ID 22)
            quizViewModel.fetchTriviaQuestions(amount = 10, categoryId = 22, difficulty = "medium")
        }

        // 3. Button click to SAVE/PUBLISH the quiz (calls Firebase logic)
        binding.saveQuizButton.setOnClickListener {

            val quizTitle = binding.quizTitleInput.text.toString().trim()
            val currentQuestions = quizViewModel.fetchedQuestions.value

            // Validation: Title
            if (quizTitle.isEmpty()) {
                binding.quizTitleInput.error = "Quiz title cannot be empty."
                Toast.makeText(context, "Please enter a quiz title.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validation: Questions
            if (currentQuestions.isNullOrEmpty()) {
                Toast.makeText(context, "Please fetch or add questions before saving.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Validation: Teacher ID (must be logged in)
            val teacherId = FirebaseAuth.getInstance().currentUser?.uid
            if (teacherId.isNullOrEmpty()) {
                Toast.makeText(context, "Error: You must be logged in to create a quiz.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Call ViewModel to Save to Firebase
            quizViewModel.saveQuiz(quizTitle, teacherId, currentQuestions)

            // Success Feedback and UI Cleanup
            Toast.makeText(context, "Quiz '$quizTitle' successfully created and published!", Toast.LENGTH_LONG).show()

            // Clear the input and the questions in the ViewModel for the next quiz
            binding.quizTitleInput.setText("")
            // You will need to ensure this function is implemented in QuizViewModel
            // quizViewModel.clearQuestions()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}