package com.quizmaster.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quizmaster.data.Question
import com.quizmaster.databinding.ItemQuestionPreviewBinding

class QuestionPreviewAdapter :
    ListAdapter<Question, QuestionPreviewAdapter.QuestionViewHolder>(QuestionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuestionPreviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class QuestionViewHolder(private val binding: ItemQuestionPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: Question, position: Int) {
            binding.question = question
            binding.position = position + 1 // 1-based numbering
            binding.executePendingBindings()
        }
    }

    class QuestionDiffCallback : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean =
            oldItem.question == newItem.question

        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean =
            oldItem == newItem
    }
}
