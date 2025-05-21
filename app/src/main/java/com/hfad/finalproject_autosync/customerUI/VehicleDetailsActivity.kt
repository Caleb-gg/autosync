package com.hfad.finalproject_autosync.customerUI

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.finalproject_autosync.R
import com.hfad.finalproject_autosync.models.Vehicle

class VehicleDetailsActivity : AppCompatActivity() {
    private lateinit var makeEditText: TextInputEditText
    private lateinit var modelEditText: TextInputEditText
    private lateinit var yearEditText: TextInputEditText
    private lateinit var vinEditText: TextInputEditText
    private lateinit var licensePlateEditText: TextInputEditText
    private lateinit var colorEditText: TextInputEditText
    private lateinit var submitButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var businessId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_details)

        // Get business ID from intent
        businessId = intent.getStringExtra("businessId")
        Log.d("businessID", "This is the BusinessID $businessId")
        val businessName = intent.getStringExtra("businessName")
        supportActionBar?.title = "Add Vehicle to $businessName"

        // Initialize views
        makeEditText = findViewById(R.id.makeEditText)
        modelEditText = findViewById(R.id.modelEditText)
        yearEditText = findViewById(R.id.yearEditText)
        vinEditText = findViewById(R.id.vinEditText)
        licensePlateEditText = findViewById(R.id.licensePlateEditText)
        colorEditText = findViewById(R.id.colorEditText)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            submitVehicleDetails()
        }
    }

    private fun submitVehicleDetails() {
        val make = makeEditText.text.toString().trim()
        val model = modelEditText.text.toString().trim()
        val year = yearEditText.text.toString().trim()
        val vin = vinEditText.text.toString().trim()
        val licensePlate = licensePlateEditText.text.toString().trim()
        val color = colorEditText.text.toString().trim()

        // Validate input
        if (make.isEmpty() || model.isEmpty() || year.isEmpty() || vin.isEmpty() || 
            licensePlate.isEmpty() || color.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val vehicle = Vehicle(
            make = make,
            model = model,
            year = year,
            vin = vin,
            licensePlate = licensePlate,
            color = color,
            userId = userId,
            mechanicId = businessId ?: return
        )

        // Save to Firestore
        db.collection("Vehicles")
            .add(vehicle)
            .addOnSuccessListener {
                Toast.makeText(this, "Vehicle added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding vehicle: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
} 