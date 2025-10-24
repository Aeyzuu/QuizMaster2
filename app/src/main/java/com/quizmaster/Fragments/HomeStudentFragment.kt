package com.quizmaster.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.quizmaster.R
import com.quizmaster.adapter.AvailableQuizzesAdapter
import com.quizmaster.data.QuizRepository
import com.quizmaster.databinding.FragmentHomeStudentBinding
import com.quizmaster.viewModel.QuizViewModel

class HomeStudentFragment : Fragment() {

    // ViewModel with injected Repository
    private val quizViewModel: QuizViewModel by viewModels {
        QuizViewModel.QuizViewModelFactory(QuizRepository())
    }

    // DataBinding reference
    private var _binding: FragmentHomeStudentBinding? = null
    private val binding get() = _binding!!

    // Adapter for quizzes
    private val quizAdapter by lazy {
        AvailableQuizzesAdapter { quiz ->
            // TODO: Handle quiz click (e.g., navigate to quiz details)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout with DataBinding
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_student, container, false)

        // Connect ViewModel and adapter to the layout
        binding.viewModel = quizViewModel
        binding.adapter = quizAdapter
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use requireContext() to avoid nullable context issue
        binding.availableQuizzesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Optional: Manual observation (if you haven’t set up BindingAdapter for items)
        quizViewModel.quizzes.observe(viewLifecycleOwner) { quizzes ->
            quizAdapter.submitList(quizzes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
