package com.quizmaster.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quizmaster.R // Ensure this points to your R class
import com.quizmaster.data.Quiz
// This adapter handles displaying the list of available quizzes for the student
class AvailableQuizzesAdapter(private val clickListener: (Quiz) -> Unit = {}) :
    ListAdapter<Quiz, AvailableQuizzesAdapter.QuizViewHolder>(QuizDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_available_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = getItem(position)
        holder.bind(quiz, clickListener)
    }

    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val quizTitleTextView: TextView = itemView.findViewById(R.id.quizTitleTextView)
        private val quizDetailsTextView: TextView = itemView.findViewById(R.id.quizDetailsTextView)

        fun bind(quiz: Quiz, clickListener: (Quiz) -> Unit) {
            quizTitleTextView.text = quiz.name

            // Display details (number of questions and teacher ID)
            val details = "${quiz.questions.size} Questions | Teacher ID: ${quiz.teacherId.substring(0, 8)}..."
            quizDetailsTextView.text = details

            // Set up click listener for starting the quiz
            itemView.setOnClickListener {
                clickListener(quiz)
            }
        }
    }
}

class QuizDiffCallback : DiffUtil.ItemCallback<Quiz>() {
    override fun areItemsTheSame(oldItem: Quiz, newItem: Quiz): Boolean {
        // Quizzes are the same if their IDs match
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Quiz, newItem: Quiz): Boolean {
        // Check if all contents (name, number of questions) are the same
        return oldItem == newItem
    }
}