package com.example.test

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.test.databinding.ActivityPaymentHistoryBinding
import org.json.JSONObject

class PaymentHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentHistoryBinding
    private val backendUrl = "http://192.168.123.7:4242"  // Заменить на твой IP

    private var userId: String? = null
    private val paymentList = mutableListOf<Payment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId")

        setupRecyclerView()
        fetchPaymentHistory()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = PaymentHistoryAdapter(paymentList)
    }

    private fun fetchPaymentHistory() {
        val url = "$backendUrl/payments?userId=$userId"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                paymentList.clear()
                for (i in 0 until response.length()) {
                    val paymentObject = response.getJSONObject(i)
                    val payment = Payment(
                        id = paymentObject.getString("_id"),
                        amount = paymentObject.getInt("amount"),
                        status = paymentObject.getString("status"),
                        createdAt = paymentObject.getString("createdAt")
                    )
                    paymentList.add(payment)
                }
                binding.recyclerView.adapter?.notifyDataSetChanged()
            },
            { error ->
                Log.e("PaymentHistoryActivity", "Error fetching payment history: ${error.message}")
                Toast.makeText(this, "Error fetching payment history", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }
}
