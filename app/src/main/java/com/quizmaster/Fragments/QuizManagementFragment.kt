package com.quizmaster.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.quizmaster.R
import com.quizmaster.adapter.QuizAdapter
import com.quizmaster.data.Quiz
import com.quizmaster.databinding.FragmentQuizManagementBinding
import com.quizmaster.viewModel.QuizViewModel

class QuizManagementFragment : Fragment() {

    private lateinit var binding: FragmentQuizManagementBinding
    private val quizViewModel: QuizViewModel by viewModels()
    private lateinit var adapter: QuizAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizManagementBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        adapter = QuizAdapter { quiz ->
            val action = QuizManagementFragmentDirections.actionQuizManagementFragmentToEditQuizFragment(quiz)
            findNavController().navigate(action)
        }

        binding.quizzesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.quizzesRecyclerView.adapter = adapter

        quizViewModel.quizzes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.addQuizFab.setOnClickListener {
            findNavController().navigate(R.id.action_quizManagementFragment_to_addQuizFragment)
        }
    }
}
