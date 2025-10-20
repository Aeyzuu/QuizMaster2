package com.quizmaster

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.quizmaster.databinding.ActivitySignupStudentBinding
import com.quizmaster.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupStudentBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val usersRef by lazy { FirebaseDatabase.getInstance().reference.child("Users") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginLinkBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        binding.signUpBtn.setOnClickListener { signUpStudent() }
    }

    private fun signUpStudent() {
        val studentId = binding.studentIdInput.text?.toString()?.trim().orEmpty()
        val studentName = binding.studentNameInput.text?.toString()?.trim().orEmpty()
        val email = binding.emailInput.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInput.text?.toString()?.trim().orEmpty()

        // Basic validation
        if (studentId.isEmpty()) {
            binding.studentIdLayout.error = "Required"
            return
        } else binding.studentIdLayout.error = null

        // Basic validation
        if (studentName.isEmpty()) {
            binding.studentNameInput.error = "Required"
            return
        } else binding.studentNameLayout.error = null

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Invalid email"
            return
        } else binding.emailLayout.error = null

        if (password.length < 6) {
            binding.passwordLayout.error = "At least 6 characters"
            return
        } else binding.passwordLayout.error = null

        setLoading(true)

        // 1) Create Auth user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    setLoading(false)
                    Toast.makeText(
                        this,
                        task.exception?.localizedMessage ?: "Sign up failed",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnCompleteListener
                }

                val uid = auth.currentUser?.uid.orEmpty()

                // 2) Save to Realtime Database: Users/{uid}
                val user = User(
                    uid = uid,
                    name = studentName,
                    email = email,
                    role = "student",
                    studentId = studentId
                )

                usersRef.child(uid).setValue(user)
                    .addOnSuccessListener {
                        setLoading(false)
                        Toast.makeText(this, "Student account created!", Toast.LENGTH_SHORT).show()
                        // Route to SignIn (or your home screen)
                        startActivity(Intent(this, SignInActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        setLoading(false)
                        Toast.makeText(this, e.localizedMessage ?: "DB write failed", Toast.LENGTH_LONG).show()
                    }
            }
    }

    private fun setLoading(loading: Boolean) {
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.signUpBtn.isEnabled = !loading
        binding.studentIdInput.isEnabled = !loading
        binding.emailInput.isEnabled = !loading
        binding.passwordInput.isEnabled = !loading
    }
}