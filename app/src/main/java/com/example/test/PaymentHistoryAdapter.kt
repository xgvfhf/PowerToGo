package com.example.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.databinding.ItemPaymentBinding

class PaymentHistoryAdapter(
    private val paymentList: List<Payment>
) : RecyclerView.Adapter<PaymentHistoryAdapter.PaymentViewHolder>() {

    inner class PaymentViewHolder(private val binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: Payment) {
            binding.amountTextView.text = "Amount: $${payment.amount / 100}"
            binding.statusTextView.text = "Status: ${payment.status}"
            binding.dateTextView.text = "Date: ${payment.createdAt}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(paymentList[position])
    }

    override fun getItemCount() = paymentList.size
}
