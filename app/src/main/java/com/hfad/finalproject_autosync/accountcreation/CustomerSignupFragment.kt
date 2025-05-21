package com.hfad.finalproject_autosync.accountcreation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.finalproject_autosync.customerUI.CustomerHomeActivity
import com.hfad.finalproject_autosync.databinding.FragmentCustomerSignupBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random


/**
 * A simple [Fragment] subclass.
 * Use the [CustomerSignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CustomerSignupFragment : Fragment() {

    private var _binding : FragmentCustomerSignupBinding ?= null
    private val binding get() = _binding!!

    //used for email Authentication
    private lateinit var auth: FirebaseAuth
    //used for firebase database
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomerSignupBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.customerSignupBtn.setOnClickListener {
            createCustomerAccount()
        }
        return binding.root
    }

    private fun createCustomerAccount(){
        val email = binding.signupCustomerEmail.text.toString()
        val phone = binding.signupCustomerPhone.text.toString()
        val password = binding.signupCustomerPassword.text.toString()
        val confirmPassword = binding.signupConfirmPassword.text.toString()
        val customerName = binding.signupCustomerName.text.toString()

        //make sure password and confirm password edit text match
        if (password != confirmPassword)
        {
            Toast.makeText(requireContext(), "Password must Match", Toast.LENGTH_SHORT).show()
            return
        }

        // fields can not be left blank
        if (email.isEmpty() || password.isEmpty() ||confirmPassword.isEmpty() || customerName.isEmpty() || phone.isEmpty())
        {
            Toast.makeText(requireContext(), "Nothing must be left blank", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        // Launch a coroutine
                        lifecycleScope.launch {
                            try {
                                val customerID = ensureUnqiueCustomerID()

                                val customerInfo = hashMapOf(
                                    "name" to customerName,
                                    "email" to email,
                                    "phone number" to phone,
                                    "customerID" to customerID
                                )

                                db.collection("Customers").document(uid)
                                    .set(customerInfo)
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "Account Creation Successful", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(requireContext(), CustomerHomeActivity::class.java))
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("CustomerSignup", "Failed to save customer info: ${e.message}", e)
                                        Toast.makeText(requireContext(), "Saving Customer Info Failed", Toast.LENGTH_SHORT).show()
                                    }

                            } catch (e: Exception) {
                                Log.e("CustomerSignup", "Error generating unique ID: ${e.message}", e)
                                Toast.makeText(requireContext(), "Failed to generate customer ID", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Account Creation Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun createCustomerID(): String{
        val prefix = "CUST"
        val random = Random.nextInt(0, 100000)
        return "$prefix${String.format("%05d", random)}"
    }

    private suspend fun ensureUnqiueCustomerID(): String{
        val maxAttempts = 10
        repeat(maxAttempts){
            val candidateID = createCustomerID()
            val snapshot = db.collection("Customers")
                .whereEqualTo("customerID", candidateID)
                .get()
                .await()
            if (snapshot.isEmpty){
                return  candidateID
            }
        }
        throw Exception("Can't generate Unique ID after 10 tries")
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}