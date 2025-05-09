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

    private val backendUrl = "http://192.168.226.7:4242" // this is real ip(for testing with my phone) if u need an emulator replace to http://10.0.2.2:4242

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        Log.d("PaymentActivity", "Activity created and setContentView called")

        val payButton = findViewById<Button>(R.id.payButton)

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

    private fun createCheckoutSession() {
        val url = "$backendUrl/create-checkout-session"
        val requestQueue = Volley.newRequestQueue(this)

        val params = JSONObject()
        params.put("amount", 1500)

        Log.d("PaymentActivity", "Sending request to $url with params: $params")

        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, params,
            { response ->
                try {
                    val checkoutUrl = response.getString("url")
                    Log.d("PaymentActivity", "Checkout URL: $checkoutUrl")
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