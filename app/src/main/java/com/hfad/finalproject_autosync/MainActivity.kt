// src/main/java/com/hfad/finalproject_autosync/MainActivity.kt
package com.hfad.finalproject_autosync

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.finalproject_autosync.accountcreation.AccountCreationActivity
import com.hfad.finalproject_autosync.accountcreation.ForgotPasswordFragment
import com.hfad.finalproject_autosync.businessUI.BusinessHomeActivity
import com.hfad.finalproject_autosync.customerUI.CustomerHomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize Firebase App & App Check
        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance()
            .installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())

        // Whenever we pop back to the login screen, re‑show the Login/Create buttons
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                findViewById<View>(R.id.loginButton).visibility = View.VISIBLE
                findViewById<View>(R.id.button2)      .visibility = View.VISIBLE
            }
        }
    }

    fun loginUser(view: View) {
        auth = FirebaseAuth.getInstance()
        val email = findViewById<EditText>(R.id.loginEmail).text.toString()
        val password = findViewById<EditText>(R.id.loginPassword).text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val db = FirebaseFirestore.getInstance()

                    // Check if user is a customer
                    db.collection("Customers").document(uid).get()
                        .addOnSuccessListener { cust ->
                            if (cust.exists()) {
                                startActivity(Intent(this, CustomerHomeActivity::class.java))
                                Toast.makeText(this, "Logged in as Customer", Toast.LENGTH_SHORT).show()
                            } else {
                                // Otherwise check businesses
                                db.collection("businesses").document(uid).get()
                                    .addOnSuccessListener { biz ->
                                        if (biz.exists()) {
                                            startActivity(Intent(this, BusinessHomeActivity::class.java))
                                            Toast.makeText(this, "Logged in as Business", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this, "User not found in any role", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        }
                } else {
                    Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun createAccount(view: View) {
        startActivity(Intent(this, AccountCreationActivity::class.java))
    }

    /** Hide the login/Create buttons and load the ForgotPasswordFragment **/
    fun onForgotPassword(view: View) {
        // Hide original buttons
        findViewById<View>(R.id.loginButton).visibility = View.GONE
        findViewById<View>(R.id.button2)      .visibility = View.GONE

        // Replace the login layout with the reset‑password fragment
        supportFragmentManager.commit {
            replace(R.id.main, ForgotPasswordFragment())
            addToBackStack(null)
        }
    }
}
