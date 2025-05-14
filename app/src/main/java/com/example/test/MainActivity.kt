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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.provider.WebAuthProvider
import com.google.android.material.navigation.NavigationView
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val backendUrl = "http://192.168.123.7:4242"  // Заменить на твой IP

    private var userId: String? = null
    private var userName: String? = null
    private var userEmail: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем `userId` из `LoginActivity`
        userId = intent.getStringExtra("userId")
        userName = intent.getStringExtra("name")
        userEmail = intent.getStringExtra("email")

        setupDrawer()
        initViews()
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("name", userName)
                    intent.putExtra("email", userEmail)
                    startActivity(intent)
                }

                R.id.nav_history -> {
                    val intent = Intent(this, PaymentHistoryActivity::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                }

                R.id.nav_my_powerbanks -> {
                    val intent = Intent(this, MyPowerBanksActivity::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                }

                R.id.nav_logout -> {
                    logout()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }
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
    private fun logout() {
        val account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        WebAuthProvider.logout(account)
            .withScheme("app")
            .start(this, object : com.auth0.android.callback.Callback<Void?, AuthenticationException> {
                override fun onSuccess(payload: Void?) {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    Toast.makeText(this@MainActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(exception: AuthenticationException) {
                    Toast.makeText(this@MainActivity, "Logout failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            })
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
