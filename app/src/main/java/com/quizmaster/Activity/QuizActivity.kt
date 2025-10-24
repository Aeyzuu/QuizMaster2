package com.quizmaster.Activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.quizmaster.R
import com.quizmaster.constants.Constants
import com.quizmaster.databinding.ActivityQuizBinding
import com.quizmaster.Models.QuizResult
import com.quizmaster.Models.ResultModel

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var questionList: ArrayList<QuizResult>
    private var position = 0
    private var allowPlaying = true
    private var timer: CountDownTimer? = null
    private val resultList = ArrayList<ResultModel>()
    private var timeLeft = 0
    private var score = 0.0
    private var selectedOption: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        questionList = intent.getSerializableExtra("questionList",) as? ArrayList<QuizResult> ?: arrayListOf()

        if (questionList.isEmpty()) {
            // Handle the case where the question list is empty
            finish()
            return
        }

        binding.pbProgress.max = questionList.size
        setQuestion()
        setOptions()
        startTimer()
        binding.tvProgress.text = "1/${questionList.size}"
        binding.btnNext.setOnClickListener {
            onNext()
        }

        val optionClickListener = View.OnClickListener { view ->
            if (allowPlaying) {
                timer?.cancel()
                selectedOption = view as Button
                view.background = ContextCompat.getDrawable(this, R.drawable.red_button_bg)
                showCorrectAnswer()
                allowPlaying = false
            }
        }

        binding.option1.setOnClickListener(optionClickListener)
        binding.option2.setOnClickListener(optionClickListener)
        binding.option3.setOnClickListener(optionClickListener)
        binding.option4.setOnClickListener(optionClickListener)
    }

    private fun onNext() {
        setScore(selectedOption)
        val resultModel = ResultModel(
            20 - timeLeft,
            questionList[position].type,
            questionList[position].difficulty,
            score
        )
        resultList.add(resultModel)
        score = 0.0
        selectedOption = null

        if (position < questionList.size - 1) {
            timer?.cancel()
            position++
            setQuestion()
            setOptions()
            binding.pbProgress.progress = position + 1
            binding.tvProgress.text = "${position + 1}/${questionList.size}"
            resetButtonBackground()
            allowPlaying = true
            startTimer()
        } else {
            endGame()
        }
    }

    private fun setQuestion() {
        val decodedQuestion = Constants.decodeHtmlString(questionList[position].question)
        binding.tvQuestion.text = decodedQuestion
    }

    private lateinit var correctAnswer: String
    private lateinit var optionList: List<String>

    private fun setOptions() {
        val question = questionList[position]
        val temp = Constants.getRandomOptions(question.correct_answer, question.incorrect_answers)
        optionList = temp.second
        correctAnswer = temp.first
        binding.option1.text = optionList[0]
        binding.option2.text = optionList[1]
        if (question.type == "multiple") {
            binding.option3.visibility = View.VISIBLE
            binding.option4.visibility = View.VISIBLE
            binding.option3.text = optionList[2]
            binding.option4.text = optionList[3]
        } else {
            binding.option3.visibility = View.GONE
            binding.option4.visibility = View.GONE
        }
    }

    private fun setScore(button: Button?) {
        if (correctAnswer == button?.text) {
            score = getScore()
        }
    }

    private fun getScore(): Double {
        val score1 = when (questionList[position].type) {
            "boolean" -> 0.5
            else -> 1.0
        }

        val score2 = (timeLeft.toDouble()) / 20.0
        val score3 = when (questionList[position].difficulty) {
            "easy" -> 1.0
            "medium" -> 2.0
            else -> 3.0
        }

        return score1 + score2 + score3
    }

    private fun showCorrectAnswer() {
        val blueBg = ContextCompat.getDrawable(this, R.drawable.blue_button_bg)
        when (correctAnswer) {
            optionList[0] -> binding.option1.background = blueBg
            optionList[1] -> binding.option2.background = blueBg
            optionList.getOrNull(2) -> binding.option3.background = blueBg
            else -> binding.option4.background = blueBg
        }
    }

    private fun resetButtonBackground() {
        val grayBg = ContextCompat.getDrawable(this, R.drawable.gray_button_bg)
        binding.option1.background = grayBg
        binding.option2.background = grayBg
        binding.option3.background = grayBg
        binding.option4.background = grayBg
    }

    private fun startTimer() {
        binding.circularProgressBar.max = 20
        binding.circularProgressBar.progress = 20
        timer = object : CountDownTimer(20000, 1000) {
            override fun onTick(remaining: Long) {
                binding.circularProgressBar.incrementProgressBy(-1)
                binding.tvTimer.text = (remaining / 1000).toString()
                timeLeft = (remaining / 1000).toInt()
            }

            override fun onFinish() {
                showCorrectAnswer()
                allowPlaying = false
            }
        }.start()
    }

    private fun endGame() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("resultList", resultList)
        startActivity(intent)
        finish()
    }
}
