package com.quizmaster.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.quizmaster.R
import com.quizmaster.data.Question
import com.quizmaster.data.Quiz
import com.quizmaster.databinding.FragmentEditQuizBinding
import com.quizmaster.databinding.ItemQuestionBinding
import com.quizmaster.viewModel.CategoryViewModel
import com.quizmaster.viewModel.QuizViewModel

class EditQuizFragment : Fragment() {

    private lateinit var binding: FragmentEditQuizBinding
    private val args: EditQuizFragmentArgs by navArgs()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private val quizViewModel: QuizViewModel by viewModels()
    private val questionViews = mutableListOf<ItemQuestionBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditQuizBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.addQuestionBtn.setOnClickListener { addQuestionView(null) }
        binding.updateQuizBtn.setOnClickListener { updateQuiz() }
        observeCategories()
        observeUpdateStatus()
        populateQuizData()
    }

    private fun observeCategories() {
        categoryViewModel.categories.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
            binding.categorySpinner.setSelection(it.map { it.name }.indexOf(args.quiz.category))
        }
    }

    private fun observeUpdateStatus() {
        quizViewModel.updateStatus.observe(viewLifecycleOwner) {
            binding.progress.visibility = View.GONE
            if (it) {
                Toast.makeText(requireContext(), "Quiz updated successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Failed to update quiz", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateQuizData() {
        binding.quizTitleInput.setText(args.quiz.title)
        args.quiz.questions.forEach { addQuestionView(it) }
    }

    private fun addQuestionView(question: Question?) {
        val questionBinding = ItemQuestionBinding.inflate(LayoutInflater.from(requireContext()), binding.questionsContainer, true)
        questionViews.add(questionBinding)

        if (question != null) {
            questionBinding.questionInput.setText(question.question)
            questionBinding.option1Input.setText(question.option1)
            questionBinding.option2Input.setText(question.option2)
            questionBinding.option3Input.setText(question.option3)
            questionBinding.option4Input.setText(question.option4)
            val answerIndex = question.answer.last().toString().toInt() - 1
            questionBinding.answerSpinner.setSelection(answerIndex)
        }

        questionBinding.removeQuestionBtn.setOnClickListener {
            binding.questionsContainer.removeView(questionBinding.root)
            questionViews.remove(questionBinding)
        }
    }

    private fun updateQuiz() {
        val title = binding.quizTitleInput.text.toString()
        val category = binding.categorySpinner.selectedItem.toString()
        val questions = mutableListOf<Question>()

        for (questionBinding in questionViews) {
            val question = questionBinding.questionInput.text.toString()
            val option1 = questionBinding.option1Input.text.toString()
            val option2 = questionBinding.option2Input.text.toString()
            val option3 = questionBinding.option3Input.text.toString()
            val option4 = questionBinding.option4Input.text.toString()
            val answer = questionBinding.answerSpinner.selectedItemPosition + 1

            if (question.isNotBlank() && option1.isNotBlank() && option2.isNotBlank() && option3.isNotBlank() && option4.isNotBlank()) {
                questions.add(Question(question, option1, option2, option3, option4, "Option $answer"))
            } else {
                Toast.makeText(requireContext(), "Please fill all fields for each question", Toast.LENGTH_SHORT).show()
                return
            }
        }

        if (title.isNotBlank() && questions.isNotEmpty()) {
            binding.progress.visibility = View.VISIBLE
            quizViewModel.updateQuiz(Quiz(args.quiz.id, title, category, questions))
        } else {
            Toast.makeText(requireContext(), "Please fill quiz title and add at least one question", Toast.LENGTH_SHORT).show()
        }
    }
}
