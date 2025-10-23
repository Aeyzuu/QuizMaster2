package com.quizmaster.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment // Correctly imported
import androidx.navigation.ui.setupWithNavController
import com.quizmaster.R
import com.quizmaster.databinding.ActivityDashboardStudentBinding

class DashboardStudentActivity: AppCompatActivity() {

    private lateinit var binding: ActivityDashboardStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // The NavHostFragment casting will now work because the XML name attribute is corrected.
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.student_nav_host_fragment) as NavHostFragment

        binding.bottomNavigationStudent.setupWithNavController(navHostFragment.navController)

    }
}