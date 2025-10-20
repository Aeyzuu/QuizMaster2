package com.quizmaster

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.quizmaster.databinding.ActivitySignupTeacherBinding
import com.quizmaster.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupTeacherActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupTeacherBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val dbRef by lazy { FirebaseDatabase.getInstance().reference.child("Users") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginLinkBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        binding.signUpBtn.setOnClickListener { signUpTeacher() }
    }

    private fun signUpTeacher() {
        val name = binding.nameInput.text?.toString()?.trim().orEmpty()
        val email = binding.emailInput.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInput.text?.toString()?.trim().orEmpty()

        // Simple validation
        if (name.isEmpty()) {
            binding.nameLayout.error = "Required"
            return
        } else binding.nameLayout.error = null

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Invalid email"
            return
        } else binding.emailLayout.error = null

        if (password.length < 6) {
            binding.passwordLayout.error = "At least 6 characters"
            return
        } else binding.passwordLayout.error = null

        setLoading(true)

        // Create auth user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    setLoading(false)
                    val msg = task.exception?.localizedMessage ?: "Sign up failed"
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener
                }

                val uid = auth.currentUser?.uid.orEmpty()
                val user = User(
                    uid = uid,
                    name = name,
                    email = email,
                    role = "admin" // teacher = admin per your requirement
                )

                // Write to Realtime DB
                dbRef.child(uid).setValue(user)
                    .addOnSuccessListener {
                        setLoading(false)
                        Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                        // go to sign-in (or your home screen)
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
        binding.nameInput.isEnabled = !loading
        binding.emailInput.isEnabled = !loading
        binding.passwordInput.isEnabled = !loading
    }
}