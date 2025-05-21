package com.hfad.finalproject_autosync.accountcreation

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.hfad.finalproject_autosync.R

class AccountCreationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_creation)
        val BusinessSignupFragment = BusinessSignupFragment()
        val CustomerSignupFragment = CustomerSignupFragment()

        //start with Customer signup Fragment
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fContainer,CustomerSignupFragment)
            commit()
        }

        //change to BusinessFragment
        findViewById<Button>(R.id.businessBtn).setOnClickListener{
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fContainer, BusinessSignupFragment)
                commit()
            }
        }

        //change to CustomerFragment
        findViewById<Button>(R.id.customerBtn).setOnClickListener{
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fContainer,CustomerSignupFragment)
                commit()
            }
        }

    }
}