package com.hfad.finalproject_autosync.customerUI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfad.finalproject_autosync.R
import com.hfad.finalproject_autosync.models.Business

class BusinessAdapter(
    private val businesses: List<Business>,
    private val onBusinessSelected: (Business) -> Unit
) : RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder>() {

    class BusinessViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.businessNameText)
        val addressText: TextView = view.findViewById(R.id.businessAddressText)
        val phoneText: TextView = view.findViewById(R.id.businessPhoneText)
        val selectButton: Button = view.findViewById(R.id.selectBusinessButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_business, parent, false)
        return BusinessViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusinessViewHolder, position: Int) {
        val business = businesses[position]
        holder.nameText.text = business.name
        holder.addressText.text = business.address
        holder.phoneText.text = business.phone

        holder.selectButton.setOnClickListener {
            onBusinessSelected(business)
        }
    }

    override fun getItemCount() = businesses.size
} 