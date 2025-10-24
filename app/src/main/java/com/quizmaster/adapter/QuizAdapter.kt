package com.quizmaster.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.quizmaster.data.Quiz
import com.quizmaster.databinding.ItemQuizBinding

class QuizAdapter(
    private val onEdit: (Quiz) -> Unit
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    private var quizzes = listOf<Quiz>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(quizzes[position])
    }

    override fun getItemCount() = quizzes.size

    fun submitList(quizzes: List<Quiz>) {
        this.quizzes = quizzes
        notifyDataSetChanged()
    }

    inner class QuizViewHolder(private val binding: ItemQuizBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quiz: Quiz) {
            binding.quizTitle.text = quiz.title
            binding.quizCategory.text = quiz.category

            binding.editBtn.setOnClickListener { onEdit(quiz) }

            binding.deleteBtn.setOnClickListener {
                FirebaseDatabase.getInstance().reference.child("Quizzes").child(quiz.id).removeValue()

            }
        }
    }
}
