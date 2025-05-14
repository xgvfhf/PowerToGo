package com.example.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class PowerBankAdapter(private val powerBankList: List<PowerBank>) :
    RecyclerView.Adapter<PowerBankAdapter.PowerBankViewHolder>() {

    class PowerBankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stationIdTextView: TextView = itemView.findViewById(R.id.stationIdTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PowerBankViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_powerbank, parent, false)
        return PowerBankViewHolder(view)
    }

    override fun onBindViewHolder(holder: PowerBankViewHolder, position: Int) {
        val powerBank = powerBankList[position]
        holder.stationIdTextView.text = "Location: ${powerBank.location}\nRented At: ${powerBank.rentedAt}"
    }

    override fun getItemCount(): Int = powerBankList.size
}
