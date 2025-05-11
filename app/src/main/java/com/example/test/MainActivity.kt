package com.example.test

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.test.databinding.ActivityMainBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val backendUrl = "http://192.168.169.7:4242"  // Заменить на твой IP

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем `userId` из `LoginActivity`
        userId = intent.getStringExtra("userId")

        initViews()
    }

    private fun initViews() {
        binding.scanQrCode.setOnClickListener {
            showCamera()
        }
    }

    /**
     * Запуск QR-сканера
     */
    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR code")
        options.setBeepEnabled(true)
        options.setBarcodeImageEnabled(true)
        scanLauncher.launch(options)
    }

    /**
     * Обработка результата сканирования QR-кода
     */
    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
        } else {
            try {
                val scannedData = result.contents
                val jsonObject = JSONObject(scannedData)

                val stationId = jsonObject.optInt("stationId", -1)

                if (stationId != -1) {
                    checkAvailability(stationId)
                } else {
                    Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this, "Error parsing QR Code", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    /**
     * Проверка наличия свободных PowerBank'ов
     */
    private fun checkAvailability(stationId: Int) {
        val url = "$backendUrl/check-availability/$stationId"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val available = response.getBoolean("available")

                if (available) {
                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra("stationId", stationId)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No FREE PowerBanks available at station $stationId", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error checking availability: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(jsonObjectRequest)
    }
}
