package com.example.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.example.test.databinding.ActivityAdminBinding
import com.example.test.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONObject

class AdminActivity : AppCompatActivity() {

    private lateinit var account: Auth0

    private val backendUrl = "http://192.168.123.7:4242"
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var users: JSONArray = JSONArray()
    private lateinit var binding: ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_admin)
        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        listView = findViewById(R.id.userListView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        fetchUsers()

        listView.setOnItemClickListener { _, _, position, _ ->
            val user = users.getJSONObject(position)
            val userId = user.getString("userId")
            val isBanned = user.optBoolean("isBanned", false)

            val action = if (isBanned) "unban" else "ban"
            toggleBan(userId, action)
        }

        binding.buttonLogout.setOnClickListener {
            logout()
        }

    }

    private fun fetchUsers() {
        val queue = Volley.newRequestQueue(this)
        val url = "$backendUrl/users"

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                users = response
                adapter.clear()
                for (i in 0 until response.length()) {
                    val user = response.getJSONObject(i)
                    val status = if (user.optBoolean("isBanned", false)) "❌ BANNED" else "✅ ACTIVE"
                    val reminders = user.optInt("remindersSent", 0)
                    adapter.add("${user.getString("name")} | ${user.getString("email")} | $status | $reminders reminders")
                }
            },
            { error ->
                Log.e("AdminActivity", "Error fetching users: ${error.message}")
                Toast.makeText(this, "Error loading users", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(request)
    }
    private fun logout() {
        WebAuthProvider.logout(account)
            .withScheme("app")
            .start(this, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(payload: Void?) {
                    val intent = Intent(this@AdminActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    showSnackBar("Successfully logged out")
                }

                override fun onFailure(exception: AuthenticationException) {
                    showSnackBar("Logout failed: ${exception.message}")
                }
            })
    }
    private fun toggleBan(userId: String, action: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "$backendUrl/user-status"
        val params = JSONObject().apply {
            put("userId", userId)
            put("action", action)
        }

        val request = JsonObjectRequest(Request.Method.POST, url, params,
            {
                Toast.makeText(this, "User $action success", Toast.LENGTH_SHORT).show()
                fetchUsers()
            },
            { error ->
                Log.e("AdminActivity", "Error toggling user ban: ${error.message}")
                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(request)
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
    }
}
