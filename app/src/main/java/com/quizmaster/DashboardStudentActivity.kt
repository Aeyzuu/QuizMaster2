package com.quizmaster


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.quizmaster.databinding.ActivityDashboardStudentBinding
import com.google.firebase.database.R

class DashboardStudentActivity: AppCompatActivity() {

    private lateinit var binding: ActivityDashboardStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(com.quizmaster.R.id.nav_host_fragment_student) as NavHostFragment
        binding.bottomNavigationStudent.setupWithNavController(navHostFragment.navController)

    }
}