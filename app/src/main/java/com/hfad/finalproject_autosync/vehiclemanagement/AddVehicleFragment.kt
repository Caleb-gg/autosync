package com.hfad.finalproject_autosync.vehiclemanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.finalproject_autosync.databinding.FragmentAddVehicleBinding
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.hfad.finalproject_autosync.API.ApiService
import com.hfad.finalproject_autosync.API.DataAPIResponse
import com.hfad.finalproject_autosync.businessUI.BusinessHomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


// Fragment for adding a new vehicle to Firestore
class AddVehicleFragment : Fragment() {

    //first part of URL
    private val retrofitBuilder by lazy {
        Retrofit.Builder()
            .baseUrl("https://vpic.nhtsa.dot.gov/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // View binding to access UI elements
    private var _binding: FragmentAddVehicleBinding? = null
    private val binding get() = _binding!!

    // Firebase Firestore and Auth instances
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Inflates the fragment layout and sets up logic
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddVehicleBinding.inflate(inflater, container, false)

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Populate the spinner with repair options
        val statusOptions = listOf("Waiting for Parts", "In Progress", "Completed")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter

        //Handle VIN button
        binding.checkVINBtn.setOnClickListener {
            getJSONdata()
        }

        // Handle Save button click
        binding.buttonSaveVehicle.setOnClickListener {
            addVehicle()
        }

        return binding.root
    }


    private fun getJSONdata(){
        //grabs vin from text
        val vin = binding.editTextVIN.text.toString()
        Log.d("VIN_API", "Getting data for VIN: $vin")

        //makes sure vin text is not empty
        if (vin.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a VIN", Toast.LENGTH_SHORT).show()
            return
        }

        //debugging: makes sure url is correct
        val fullUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/$vin?format=json"
        Log.d("VIN_API", "Full URL: $fullUrl")

        val jsonData = retrofitBuilder.getAPIData(vin)
        jsonData.enqueue(object : Callback<DataAPIResponse> {
            override fun onResponse(
                call: Call<DataAPIResponse>,
                response: Response<DataAPIResponse>
            ) {
                Log.d("VIN_API", "Response received: ${response.code()}")

                if (response.isSuccessful){
                    val results = response.body()?.Results
                    Log.d("VIN_API", "Results size: ${results?.size}")

                    if (results != null) {
                        var make: String? = null
                        var model: String? = null
                        var year: String? = null

                        //loops to find needed data(make, model, year
                        for (data in results) {
                            when (data.Variable?.lowercase()) {
                                "make" -> make = data.Value
                                "model" -> model = data.Value
                                "model year" -> year = data.Value
                            }
                        }

                        make?.let { binding.editTextMake.setText(it) }
                        model?.let { binding.editTextModel.setText(it) }
                        year?.let { binding.editTextYear.setText(it) }
                    }
                }
            }

            override fun onFailure(call: Call<DataAPIResponse>, t: Throwable) {
                Log.d("APIFail", "message: " + t.message)
                Toast.makeText(requireContext(), "API error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    // Collects user input and uploads a new VehicleInfo object to Firestore
    private fun addVehicle() {
        val make = binding.editTextMake.text.toString()
        val model = binding.editTextModel.text.toString()
        val year = binding.editTextYear.text.toString()
        val vin = binding.editTextVIN.text.toString()
        val customerID = binding.editTextCustomerID.text.toString()
        val issues = binding.editTextIssue.text.toString()
        val selectedStatus = binding.spinnerStatus.selectedItem.toString()

        //input validation
        if (make.isEmpty() || model.isEmpty() || year.isEmpty() || vin.isEmpty() || issues.isEmpty() || customerID.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new VehicleInfo object
        val vehicleInfo = VehicleInfo(
            make = make,
            model = model,
            year = year,
            vin = vin,
            reportedIssues = issues,
            repairStatus = selectedStatus,
            mechanicId = auth.currentUser?.uid,
            customerID = customerID
        )

        // Add the vehicle to the "Vehicles" collection in Firestore
        db.collection("Vehicles").add(vehicleInfo)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Vehicle added!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(),BusinessHomeActivity::class.java))
                requireActivity().finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Clear the binding when view is destroyed to prevent memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
