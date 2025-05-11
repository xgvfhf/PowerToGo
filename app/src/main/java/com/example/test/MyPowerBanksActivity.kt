package com.example.test

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class MyPowerBanksActivity : AppCompatActivity() {

    private val backendUrl = "http://192.168.169.7:4242"  // Заменить на твой IP
    private var userId: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var returnButton: Button
    private lateinit var adapter: PowerBankAdapter
    private val powerBankList = mutableListOf<PowerBank>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_powerbanks)

        userId = intent.getStringExtra("userId")

        recyclerView = findViewById(R.id.recyclerView)
        returnButton = findViewById(R.id.returnButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PowerBankAdapter(powerBankList)
        recyclerView.adapter = adapter

        fetchRentedPowerBanks()

        returnButton.setOnClickListener {
            returnAllPowerBanks()
        }
    }

    /**
     * Получение всех арендованных PowerBanks
     */
    private fun fetchRentedPowerBanks() {
        val url = "$backendUrl/my-powerbanks?userId=$userId"
        val requestQueue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                powerBankList.clear()  // Очистим старые данные
                if (response.length() == 0) {
                    Toast.makeText(this, "No rented PowerBanks found.", Toast.LENGTH_SHORT).show()
                } else {
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val stationId = item.getInt("stationId")
                        val id = item.getString("_id")
                        powerBankList.add(PowerBank(id, stationId))
                    }
                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(request)
    }

    /**
     * Возврат всех арендованных PowerBanks
     */
    private fun returnAllPowerBanks() {
        val url = "$backendUrl/return-powerbanks"
        val requestQueue = Volley.newRequestQueue(this)

        val params = JSONObject()
        params.put("userId", userId)

        val request = JsonObjectRequest(Request.Method.POST, url, params,
            { response ->
                Toast.makeText(this, "All PowerBanks returned", Toast.LENGTH_SHORT).show()

                // Очистка списка после успешного возврата
                powerBankList.clear()
                adapter.notifyDataSetChanged()

            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(request)
    }
}
