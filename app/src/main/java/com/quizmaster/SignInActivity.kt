package com.quizmaster

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.quizmaster.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val usersRef by lazy { FirebaseDatabase.getInstance().reference.child("Users") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Go to sign-up screens
        binding.signupStudentBtn.setOnClickListener {
            startActivity(Intent(this, SignupStudentActivity::class.java))
            finish()
        }
        binding.signupTeacherBtn.setOnClickListener {
            startActivity(Intent(this, SignupTeacherActivity::class.java))
            finish()
        }

        // Login
        binding.loginBtn.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val email = binding.emailInput.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInput.text?.toString()?.trim().orEmpty()

        // Basic validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Invalid email"
            return
        } else binding.emailLayout.error = null

        if (password.isEmpty()) {
            binding.passwordLayout.error = "Required"
            return
        } else binding.passwordLayout.error = null

        setLoading(true)

        // 1) Auth sign-in
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    setLoading(false)
                    Toast.makeText(
                        this,
                        task.exception?.localizedMessage ?: "Sign in failed",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnCompleteListener
                }

                val uid = auth.currentUser?.uid
                if (uid.isNullOrEmpty()) {
                    setLoading(false)
                    Toast.makeText(this, "No user id", Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener
                }

                // 2) Read Users/{uid}
                usersRef.child(uid).get()
                    .addOnSuccessListener { snap ->
                        if (!snap.exists()) {
                            setLoading(false)
                            Toast.makeText(this, "User profile not found", Toast.LENGTH_LONG).show()
                            return@addOnSuccessListener
                        }

                        val dbEmail = snap.child("email").getValue(String::class.java)?.trim().orEmpty()
                        val role = snap.child("role").getValue(String::class.java)?.trim().orEmpty()

                        // Optional: ensure stored email matches the one used
                        if (!dbEmail.equals(email, ignoreCase = true)) {
                            setLoading(false)
                            Toast.makeText(this, "Email mismatch in profile", Toast.LENGTH_LONG).show()
                            return@addOnSuccessListener
                        }

                        setLoading(false)

                        when (role.lowercase()) {
                            "admin" -> {
                                startActivity(Intent(this, DashboardTeacherActivity::class.java))
                                finish()
                            }
                            "student" -> {
                                startActivity(Intent(this, DashboardStudentActivity::class.java))
                                finish()
                            }
                            else -> {
                                Toast.makeText(this, "Unknown role: $role", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        setLoading(false)
                        Toast.makeText(this, e.localizedMessage ?: "Failed to read user", Toast.LENGTH_LONG).show()
                    }
            }
    }

    private fun setLoading(loading: Boolean) {
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.loginBtn.isEnabled = !loading
        binding.emailInput.isEnabled = !loading
        binding.passwordInput.isEnabled = !loading
        binding.signupStudentBtn.isEnabled = !loading
        binding.signupTeacherBtn.isEnabled = !loading
    }
}
