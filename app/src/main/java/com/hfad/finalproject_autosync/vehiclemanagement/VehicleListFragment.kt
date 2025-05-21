package com.hfad.finalproject_autosync.vehiclemanagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.hfad.finalproject_autosync.databinding.FragmentVehicleListBinding

// This Fragment displays a list of vehicles fetched from Firebase Firestore.
class VehicleListFragment : Fragment() {

    // ViewBinding object for the fragment layout
    private var _binding: FragmentVehicleListBinding? = null
    private val binding get() = _binding!!

    // Firebase Firestore instance
    private lateinit var db: FirebaseFirestore

    // Adapter for the RecyclerView
    private lateinit var adapter: VehicleAdapter

    // Store listener registration
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVehicleListBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        listenForVehicleUpdates()

        return binding.root
    }

    // Configure the RecyclerView and handle status update callbacks
    private fun setupRecyclerView() {
        adapter = VehicleAdapter { vehicleId, newStatus ->
            updateVehicleStatus(vehicleId, newStatus)
        }
        binding.recyclerViewVehicles.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewVehicles.adapter = adapter
    }

    // Listen to real-time updates from Firestore's "Vehicles" collection
    private fun listenForVehicleUpdates() {
        val mechanicId = FirebaseAuth.getInstance().currentUser?.uid
        if (mechanicId != null) {
            listenerRegistration = db.collection("Vehicles")
                .whereEqualTo("mechanicId", mechanicId)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                        return@addSnapshotListener
                    }

                    val vehicleList = mutableListOf<VehicleInfo>()
                    for (doc in snapshots!!) {
                        val vehicle = doc.toObject(VehicleInfo::class.java).copy(id = doc.id)
                        vehicleList.add(vehicle)
                    }

                    adapter.submitList(vehicleList)
                }
        }
    }

    // Update a vehicle's repair status in Firestore
    private fun updateVehicleStatus(vehicleId: String, newStatus: String) {
        db.collection("Vehicles").document(vehicleId)
            .update("repairStatus", newStatus)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Status Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Delete all vehicles whose repairStatus == "Completed"
     */
    fun removeCompletedVehicles() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Not signed in", Toast.LENGTH_SHORT).show()
            return
        }

        // Query all docs where repairStatus == "Done"
        db.collection("Vehicles")
            .whereEqualTo("repairStatus", "Completed")
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                for (doc in snapshot.documents) {
                    batch.delete(doc.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Removed completed vehicles", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to remove: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching completed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
        listenerRegistration = null
        _binding = null
    }
}
