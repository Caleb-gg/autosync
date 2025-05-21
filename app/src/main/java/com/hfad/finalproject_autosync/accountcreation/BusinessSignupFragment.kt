package com.hfad.finalproject_autosync.accountcreation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.finalproject_autosync.businessUI.BusinessHomeActivity
import com.hfad.finalproject_autosync.customerUI.CustomerHomeActivity
import com.hfad.finalproject_autosync.databinding.FragmentBusinessSignupBinding


class BusinessSignupFragment : Fragment() {

    private var _binding: FragmentBusinessSignupBinding ?= null
    private val binding get() = _binding!!

    //used for email Authentication
    private lateinit var auth: FirebaseAuth
    //used for firebase database
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBusinessSignupBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.businessSignupBtn.setOnClickListener {
            createBusinessAccount()
        }
        return binding.root
    }

    private fun createBusinessAccount(){
        //get input from Edit Text
        val email = binding.signupBusinessEmail.text.toString()
        val password = binding.signupBusinessPassword.text.toString()
        val confirmPassword = binding.signupConfirmPassword.text.toString()
        val businessName = binding.signupBusinessName.text.toString()
        val address = binding.businessAddress.text.toString()
        val city = binding.city.text.toString()
        val state = binding.state.text.toString()
        val zipCode = binding.zipCode.text.toString()

        //make sure password and confirm password edit text match
        if (password != confirmPassword)
        {
            Toast.makeText(requireContext(), "Password must Match", Toast.LENGTH_SHORT).show()
            return
        }

        // fields can not be left blank
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || businessName.isEmpty() ||
            address.isEmpty() || city.isEmpty() || state.isEmpty() || zipCode.isEmpty()
        ) {
            Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        // create new user with inputted email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->

                if (task.isSuccessful) {
                    //grab the created business's UID
                    val uid = auth.currentUser?.uid

                    //
                    val businessInfo = hashMapOf(
                        "name" to businessName,
                        "email" to email,
                        "address" to address,
                        "city" to city,
                        "state" to state,
                        "zipCode" to zipCode
                    )

                    //save info to database using the uid as the document id
                    uid?.let { id ->
                        db.collection("businesses").document(id)
                            .set(businessInfo)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Account Creation Successful",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(requireContext(), BusinessHomeActivity::class.java))
                            }
                            .addOnFailureListener { e ->
                                Log.e("BusinessSignup", "Failed to save business info to Firestore: ${e.message}", e)
                                Toast.makeText(requireContext(), "Saving Business Info Failed", Toast.LENGTH_SHORT).show()
                            }
                    }

                }else{
                    Toast.makeText(requireContext(), "Account Creation Failed: ${task.exception?.message} ", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
