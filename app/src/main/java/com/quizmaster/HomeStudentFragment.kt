package com.quizmaster
// You might need to adjust this package based on your project structure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager // 💡 NEW IMPORT: For RecyclerView layout
import com.quizmaster.databinding.FragmentHomeStudentBinding
import com.quizmaster.viewmodel.QuizViewModel
import com.quizmaster.adapter.AvailableQuizzesAdapter
import com.quizmaster.data.Quiz
// You might need androidx.navigation.fragment.findNavController if you add navigation logic

class HomeStudentFragment : Fragment() {

    // Use viewModels() to initialize the ViewModel
    private val quizViewModel: QuizViewModel by viewModels()
    private var _binding: FragmentHomeStudentBinding? = null
    private val binding get() = _binding!!

    // 💡 NEW: Initialize the adapter lazily, including the click listener
    private val quizAdapter: AvailableQuizzesAdapter by lazy {
        AvailableQuizzesAdapter { quiz -> handleQuizClick(quiz) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Setup RecyclerView
        binding.availableQuizzesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = quizAdapter // 💡 FIXED: Set the adapter
        }

        // --- 2. SET LOADING STATE & START FETCH ---
        binding.availableQuizzesTextView.text = "Fetching Quizzes..."
        binding.availableQuizzesTextView.visibility = View.VISIBLE
        binding.availableQuizzesRecyclerView.visibility = View.GONE

        // Start the fetch operation (It adds an observer, so data updates automatically)
        quizViewModel.fetchAvailableQuizzes()

        // --- 3. OBSERVE LIVE DATA ---
        quizViewModel.availableQuizzes.observe(viewLifecycleOwner) { quizzes ->
            if (!quizzes.isNullOrEmpty()) {
                // Data received: Hide status, show list
                binding.availableQuizzesTextView.visibility = View.GONE
                binding.availableQuizzesRecyclerView.visibility = View.VISIBLE

                // 💡 FIXED: Pass the quizzes to your RecyclerView Adapter
                quizAdapter.submitList(quizzes)

            } else {
                // No quizzes found: Show status
                binding.availableQuizzesTextView.visibility = View.VISIBLE
                binding.availableQuizzesRecyclerView.visibility = View.GONE
                binding.availableQuizzesTextView.text = "No published quizzes are currently available."
            }
        }
    }

    /**
     * Handles the click event for an individual quiz item.
     * @param quiz The Quiz object that was clicked.
     */
    private fun handleQuizClick(quiz: Quiz) {
        // You would typically use the Navigation Component here to move to the quiz start screen
        // Example:
        //val action = HomeStudentFragmentDirections.actionToQuizStart(quiz.id)
        //findNavController().navigate(action)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}