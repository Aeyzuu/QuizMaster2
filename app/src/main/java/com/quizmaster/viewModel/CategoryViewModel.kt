package com.quizmaster.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.quizmaster.data.Category

class CategoryViewModel : ViewModel() {

    private val dbRef by lazy { FirebaseDatabase.getInstance().reference.child("Categories") }

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Category>()
                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.getValue(Category::class.java)
                    category?.let { list.add(it) }
                }
                _categories.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
