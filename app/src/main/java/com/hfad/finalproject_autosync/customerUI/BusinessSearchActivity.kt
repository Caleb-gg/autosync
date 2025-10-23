package com.hfad.finalproject_autosync.customerUI

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.finalproject_autosync.R
import com.hfad.finalproject_autosync.models.Business

class BusinessSearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: TextInputEditText
    private lateinit var businessRecyclerView: RecyclerView
    private lateinit var progressBar: View
    private val db = FirebaseFirestore.getInstance()
    private val businesses = mutableListOf<Business>()
    private lateinit var adapter: BusinessAdapter
    private var isSearching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_search)

        // Initialize views
        searchEditText = findViewById(R.id.searchEditText)
        businessRecyclerView = findViewById(R.id.businessRecyclerView)
        progressBar = findViewById(R.id.progressBar)

        // Setup RecyclerView
        adapter = BusinessAdapter(businesses) { business ->
            // Handle business selection
            val intent = Intent(this, VehicleDetailsActivity::class.java)
            intent.putExtra("businessId", business.id)
            intent.putExtra("businessName", business.name)
            startActivity(intent)
        }
        businessRecyclerView.layoutManager = LinearLayoutManager(this)
        businessRecyclerView.adapter = adapter

        // Setup search functionality
        searchEditText.setOnEditorActionListener { _, _, _ ->
            performSearch()
            true
        }
    }

    private fun performSearch() {
        if (isSearching) return  // prevent double call
        isSearching = true

        val searchQuery = searchEditText.text.toString().trim()
        if (searchQuery.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            isSearching = false
            return
        }

        progressBar.visibility = View.VISIBLE
        businesses.clear()

        db.collection("businesses")
            .whereGreaterThanOrEqualTo("name", searchQuery)
            .whereLessThanOrEqualTo("name", searchQuery + '\uf8ff')
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val business = document.toObject(Business::class.java)
                    business.id = document.id
                    businesses.add(business)
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
                isSearching = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error searching businesses: ${e.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                isSearching = false
            }
    }
} 