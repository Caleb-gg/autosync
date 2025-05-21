package com.hfad.finalproject_autosync.customerUI

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.finalproject_autosync.MainActivity
import com.hfad.finalproject_autosync.R

class CustomerHomeActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_home)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid

        uid?.let { id ->
            db.collection("Customers").document(id).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        findViewById<TextView>(R.id.CustomerNameText).text = name
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Couldn't load Customer name", Toast.LENGTH_SHORT).show()
                }
        }

        uid?.let { id ->
            db.collection("Customers").document(id).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("customerID")
                        findViewById<TextView>(R.id.customerIdText).text = name
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Couldn't load Customer ID", Toast.LENGTH_SHORT).show()
                }
        }
    }


    public fun signOut(view: View){
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    public fun searchBusiness(view: View) {
        startActivity(Intent(this, BusinessSearchActivity::class.java))
    }
}