package com.hfad.finalproject_autosync.businessUI

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.finalproject_autosync.MainActivity
import com.hfad.finalproject_autosync.R
import com.hfad.finalproject_autosync.vehiclemanagement.VehicleListFragment
import com.hfad.finalproject_autosync.vehiclemanagement.VehicleModActivity

class BusinessHomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_business_home)

        auth = FirebaseAuth.getInstance()
        db   = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid

        // Grab business name and display it
        uid?.let { id ->
            db.collection("businesses").document(id).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        findViewById<TextView>(R.id.BusinessNameText).text = name
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Couldn't load business name", Toast.LENGTH_SHORT).show()
                }
        }

        // Navigate to Add Vehicle screen
        findViewById<Button>(R.id.addVehicleBtn).setOnClickListener {
            startActivity(Intent(this, VehicleModActivity::class.java))
        }

        // Remove Completed button wired to fragment
        findViewById<Button>(R.id.removeCompletedBtn).setOnClickListener {
            val frag = supportFragmentManager
                .findFragmentById(R.id.fragmentContainer) as? VehicleListFragment
            if (frag != null) {
                frag.removeCompletedVehicles()
            } else {
                Toast.makeText(
                    this,
                    "Couldn't find vehicle list to remove from",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Show the mechanic's vehicle list fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, VehicleListFragment())
            .commit()
    }

    fun signOut(view: View) {
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
