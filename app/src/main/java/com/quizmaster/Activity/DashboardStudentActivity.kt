package com.quizmaster.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.quizmaster.R
import com.quizmaster.databinding.ActivityDashboardStudentBinding

class DashboardStudentActivity: AppCompatActivity() {

    private lateinit var binding: ActivityDashboardStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find the NavHostFragment from the layout
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.student_nav_host_fragment) as NavHostFragment

        // Get the NavController from the NavHostFragment
        val navController = navHostFragment.navController

        // Set up the BottomNavigationView with the NavController
        binding.bottomNavigationStudent.setupWithNavController(navController)
    }
}
