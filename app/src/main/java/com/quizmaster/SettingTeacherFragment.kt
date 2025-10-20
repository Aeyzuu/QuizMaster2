package com.quizmaster

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.quizmaster.databinding.FragmentSettingTeacherBinding
import com.google.firebase.auth.FirebaseAuth

class SettingTeacherFragment : Fragment() {

    private var _binding: FragmentSettingTeacherBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingTeacherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logoutBtn.setOnClickListener { logout() }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val ctx = requireContext()
        val intent = Intent(ctx, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
