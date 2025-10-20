package com.quizmaster


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.quizmaster.databinding.ActivityDashboardTeacherBinding

class DashboardTeacherActivity: AppCompatActivity() {

    private lateinit var binding: ActivityDashboardTeacherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(com.quizmaster.R.id.nav_host_fragment_teacher) as NavHostFragment
        binding.bottomNavigationTeacher.setupWithNavController(navHostFragment.navController)

    }
}