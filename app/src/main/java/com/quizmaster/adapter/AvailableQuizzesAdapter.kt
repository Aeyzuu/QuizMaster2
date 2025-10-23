package com.quizmaster.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quizmaster.data.Quiz
import com.quizmaster.databinding.ItemAvailableQuizBinding

class AvailableQuizzesAdapter(
    private val onQuizClick: (Quiz) -> Unit
) : ListAdapter<Quiz, AvailableQuizzesAdapter.QuizViewHolder>(QuizDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = ItemAvailableQuizBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuizViewHolder(binding, onQuizClick)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = getItem(position)
        holder.bind(quiz)
    }

    class QuizViewHolder(
        private val binding: ItemAvailableQuizBinding,
        private val onQuizClick: (Quiz) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(quiz: Quiz) {
            binding.quiz = quiz
            binding.root.setOnClickListener { onQuizClick(quiz) }
            binding.executePendingBindings()
        }
    }

    class QuizDiffCallback : DiffUtil.ItemCallback<Quiz>() {
        override fun areItemsTheSame(oldItem: Quiz, newItem: Quiz): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Quiz, newItem: Quiz): Boolean = oldItem == newItem
    }
}
