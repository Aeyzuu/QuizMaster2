package com.quizmaster.Fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.quizmaster.R
import com.quizmaster.adapter.QuestionPreviewAdapter
import com.quizmaster.data.QuizRepository
import com.quizmaster.databinding.FragmentHomeTeacherBinding
import com.quizmaster.viewModel.QuizViewModel

class HomeTeacherFragment : Fragment() {

    private val quizViewModel: QuizViewModel by viewModels {
        QuizViewModel.Factory(QuizRepository())
    }

    private lateinit var binding: FragmentHomeTeacherBinding
    private val questionsAdapter = QuestionPreviewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_teacher, container, false)
        binding.viewModel = quizViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.questionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = questionsAdapter
        }

        quizViewModel.fetchedQuestions.observe(viewLifecycleOwner) {
            questionsAdapter.submitList(it)
        }

        quizViewModel.statusMessage.observe(viewLifecycleOwner) {
            if (!it.isNullOrBlank()) Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        binding.fetchQuestionsButton.setOnClickListener {
            quizViewModel.fetchTriviaQuestions(10, 22, "medium")
        }

        binding.saveQuizButton.setOnClickListener {
            val quizTitle = binding.quizTitleInput.text.toString().trim()
            val teacherId = FirebaseAuth.getInstance().currentUser?.uid
            val questions = quizViewModel.fetchedQuestions.value

            if (quizTitle.isEmpty() || questions.isNullOrEmpty() || teacherId.isNullOrEmpty()) {
                Toast.makeText(context, "Fill title, fetch questions, ensure logged in", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            quizViewModel.saveQuiz(quizTitle, teacherId, questions)
            binding.quizTitleInput.setText("")
            quizViewModel.clearQuestions()
        }
    }
}
