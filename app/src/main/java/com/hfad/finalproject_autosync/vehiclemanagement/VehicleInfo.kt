package com.hfad.finalproject_autosync.vehiclemanagement

// Data class representing the structure of a vehicle document in Firestore
data class VehicleInfo(
    val make: String = "",              // Vehicle manufacturer (e.g., Toyota, Ford)
    val model: String = "",             // Vehicle model (e.g., Camry, F-150)
    val year: String = "",              // Vehicle year; kept as String to match EditText input
    val vin: String = "",               // Vehicle Identification Number (unique identifier)
    val reportedIssues: String = "",    // Description of reported mechanical issues
    val repairStatus: String = "Waiting for Parts", // Default status when vehicle is added
    val mechanicId: String? = null,     // Optional: ID of mechanic assigned to the vehicle
    val customerID: String? = "",        // ID of vehicle owner
    val id: String? = null,             // Firestore document ID, set during retrieval
    val licensePlate: String? = null,
    val color : String? = null,
)
