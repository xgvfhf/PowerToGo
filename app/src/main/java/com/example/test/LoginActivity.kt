package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.example.test.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var account: Auth0
    private lateinit var binding: ActivityLoginBinding
    private var user: User? = null
    private val url = "http://192.168.123.7:4242"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        forceLogout()

        binding.buttonLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        WebAuthProvider.login(account)
            .withScheme("app")
            .start(this, object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(exception: AuthenticationException) {
                    showSnackBar("Login failed: ${exception.message}")
                }

                override fun onSuccess(credentials: Credentials) {
                    val idToken = credentials.idToken ?: return showSnackBar("Login failed: No ID token received.")

                    user = User(idToken)

                    checkBanStatus(user!!.id) { isBanned ->
                        if (isBanned) {
                            showSnackBar("Access denied: your account has been banned.")
                        } else {
                            syncUser("$url/register-user")
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("userId", user?.id)
                            intent.putExtra("name", user?.name)
                            intent.putExtra("email", user?.email)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            })
    }

    private fun syncUser(url: String){
        val queue = Volley.newRequestQueue(this)

        val params = JSONObject().apply {
            put("userId", user?.id)
            put("name", user?.name)
            put("email", user?.email)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                Log.d("RegisterUser", "User registered")
            },
            { error ->
                Log.e("RegisterUser", "Error: ${error.message}")
            }
        )

        queue.add(request)
    }
    private fun checkBanStatus(userId: String, callback: (Boolean) -> Unit) {
        val url = "$url/check-ban?userId=$userId"
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val isBanned = response.optBoolean("isBanned", false)
                callback(isBanned)
            },
            { error ->
                Log.e("checkBanStatus", "Network error: ${error.message}")
                callback(false) // если не получилось — считаем, что не забанен, чтобы не блокировать доступ по ошибке
            }
        )

        queue.add(request)
    }

    private fun forceLogout() {
        WebAuthProvider.logout(account)
            .withScheme("app")
            .start(this, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(payload: Void?) {
                    showSnackBar("Successfully logged out")
                }

                override fun onFailure(exception: AuthenticationException) {
                    showSnackBar("Logout failed: ${exception.message}")
                }
            })
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
    }
}
