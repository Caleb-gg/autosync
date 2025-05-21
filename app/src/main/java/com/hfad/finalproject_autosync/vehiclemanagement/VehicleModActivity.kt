package com.hfad.finalproject_autosync.vehiclemanagement

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.hfad.finalproject_autosync.R

class VehicleModActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vehicle_mod)

        val AddVehicleFragment = AddVehicleFragment()


        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView,AddVehicleFragment)
            commit()
        }
    }
}