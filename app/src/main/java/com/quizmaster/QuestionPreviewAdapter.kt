package com.quizmaster.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quizmaster.R
import com.quizmaster.api.Question

class QuestionPreviewAdapter :
    ListAdapter<Question, QuestionPreviewAdapter.QuestionViewHolder>(QuestionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question_preview, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = getItem(position)
        holder.bind(question, position)
    }

    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionNumberTextView: TextView = itemView.findViewById(R.id.questionNumberTextView)
        private val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)
        private val correctAnswerTextView: TextView = itemView.findViewById(R.id.correctAnswerTextView)

        fun bind(question: Question, position: Int) {
            // Display question number, category, and difficulty
            // Note: .capitalize() is deprecated, but left here to match your original code pattern
            questionNumberTextView.text = "Question ${position + 1} (${question.difficulty.capitalize()} | ${question.category})"

            // Display the question text
            questionTextView.text = question.question

            // Display the correct answer (This property now exists due to @SerializedName and the correct import!)
            correctAnswerTextView.text = "Correct Answer: ${question.correctAnswer}"
        }
    }
}

class QuestionDiffCallback : DiffUtil.ItemCallback<Question>() {
    override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
        return oldItem.question == newItem.question
    }

    override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
        return oldItem == newItem
    }
}