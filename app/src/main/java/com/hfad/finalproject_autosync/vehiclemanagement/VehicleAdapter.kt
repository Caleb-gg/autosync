package com.hfad.finalproject_autosync.vehiclemanagement

import VehicleDiffCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hfad.finalproject_autosync.databinding.ItemVehicleBinding
import androidx.recyclerview.widget.DiffUtil


// Adapter for displaying a list of vehicles in a RecyclerView
class VehicleAdapter(
    private val onStatusChange: (String, String) -> Unit // Callback function when status changes
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    private var vehicles = listOf<VehicleInfo>() // Current list of vehicles

    // Updates the list using DiffUtil for efficient UI changes
    fun submitList(list: List<VehicleInfo>) {
        val diffCallback = VehicleDiffCallback(vehicles, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        vehicles = list
        diffResult.dispatchUpdatesTo(this) // Notify RecyclerView of changes
    }

    // Creates a new ViewHolder for a vehicle item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val binding = ItemVehicleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VehicleViewHolder(binding)
    }

    // Binds vehicle data to a ViewHolder
    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.bind(vehicles[position])
    }

    // Returns the number of vehicle items
    override fun getItemCount() = vehicles.size

    // ViewHolder that binds a single vehicle item to the UI
    inner class VehicleViewHolder(private val binding: ItemVehicleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(vehicle: VehicleInfo) {
            // Display make, model, and year
            binding.textViewMakeModel.text = "${vehicle.make} ${vehicle.model} (${vehicle.year})"

            // Display VIN number
            binding.textViewVIN.text = "VIN: ${vehicle.vin}"

            // Define possible status options for the dropdown
            val statusOptions = arrayOf("Waiting for Parts", "In Progress", "Completed")

            // Set up adapter for spinner (dropdown)
            val spinnerAdapter = ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_item, statusOptions)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerStatus.adapter = spinnerAdapter

            // Set the spinner's selection to match the current vehicle status
            val currentIndex = statusOptions.indexOf(vehicle.repairStatus)
            if (currentIndex != -1) {
                binding.spinnerStatus.setSelection(currentIndex)
            }

            // Handle spinner selection change
            binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedStatus = statusOptions[position]
                    // Trigger callback only if the selected status is different
                    if (selectedStatus != vehicle.repairStatus) {
                        onStatusChange(vehicle.id!!, selectedStatus)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // No action needed
                }
            }
        }
    }
}
