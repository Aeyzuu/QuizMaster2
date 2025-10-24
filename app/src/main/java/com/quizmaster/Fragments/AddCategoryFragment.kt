package com.quizmaster.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.quizmaster.data.Category
import com.quizmaster.databinding.FragmentAddCategoryBinding

class AddCategoryFragment : Fragment() {

    private lateinit var binding: FragmentAddCategoryBinding
    private val dbRef by lazy { FirebaseDatabase.getInstance().reference.child("Categories") }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCategoryBinding.inflate(inflater, container, false)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.addCategoryBtn.setOnClickListener { addCategory() }

        return binding.root
    }

    private fun addCategory() {
        val name = binding.categoryNameInput.text.toString().trim()
        val desc = binding.categoryDescInput.text.toString().trim()

        if (name.isEmpty()) {
            binding.categoryNameLayout.error = "Required"
            return
        } else {
            binding.categoryNameLayout.error = null
        }

        if (desc.isEmpty()) {
            binding.categoryDescLayout.error = "Required"
            return
        } else {
            binding.categoryDescLayout.error = null
        }

        setLoading(true)

        val categoryId = dbRef.push().key ?: ""
        val category = Category(categoryId, name, desc)

        dbRef.child(categoryId).setValue(category)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(requireContext(), "Category added", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(loading: Boolean) {
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.addCategoryBtn.isEnabled = !loading
        binding.categoryNameInput.isEnabled = !loading
        binding.categoryDescInput.isEnabled = !loading
    }
}
