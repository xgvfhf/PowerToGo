package com.example.test

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class PaymentActivity : AppCompatActivity() {

    private val backendUrl = "http://192.168.169.7:4242" // Заменить на твой IP (для тестирования с телефоном)

    private var stationId: Int = -1  // Инициализация stationId
    private var userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        Log.d("PaymentActivity", "Activity created and setContentView called")

        val payButton = findViewById<Button>(R.id.payButton)

        // Извлекаем stationId и userId из intent
        stationId = intent.getIntExtra("stationId", -1)
        userId = intent.getStringExtra("userId")

        Log.d("PaymentActivity", "Received stationId: $stationId")

        if (stationId == -1) {
            Toast.makeText(this, "Invalid Station ID", Toast.LENGTH_SHORT).show()
            finish() // Закрываем Activity, если stationId не передан
        }

        if (payButton != null) {
            Log.d("PaymentActivity", "payButton found successfully")
            payButton.setOnClickListener {
                Log.d("PaymentActivity", "PAY button clicked")
                Toast.makeText(this, "Pay button clicked", Toast.LENGTH_SHORT).show()
                createCheckoutSession()
            }
        } else {
            Log.e("PaymentActivity", "payButton is NULL")
        }
    }

    /**
     * Создание сессии оплаты и переадресация на Stripe Checkout
     */
    private fun createCheckoutSession() {
        val url = "$backendUrl/create-checkout-session"
        val requestQueue = Volley.newRequestQueue(this)

        // Параметры для создания сессии
        val params = JSONObject()
        params.put("userId", userId)
        params.put("stationId", stationId)
        params.put("amount", 1500)  // Сумма в центах ($15.00)

        Log.d("PaymentActivity", "Sending request to $url with params: $params")

        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, params,
            { response ->
                try {
                    // Получаем URL для переадресации на Stripe
                    val checkoutUrl = response.getString("url")
                    Log.d("PaymentActivity", "Checkout URL: $checkoutUrl")

                    // Переход на страницу оплаты
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl))
                    startActivity(browserIntent)
                } catch (e: Exception) {
                    Log.e("PaymentActivity", "Error parsing response: ${e.message}")
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Log.e("PaymentActivity", "Error making request: ${error.message}")
                Toast.makeText(this, "Network Error: ${error.message}", Toast.LENGTH_LONG).show()
            })

        requestQueue.add(jsonRequest)
    }
}