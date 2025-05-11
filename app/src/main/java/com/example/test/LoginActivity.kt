package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.example.test.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var account: Auth0
    private lateinit var binding: ActivityLoginBinding
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

       // forceLogout()

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
                    val idToken = credentials.idToken

                    if (idToken == null) {
                        showSnackBar("Login failed: No ID token received.")
                        return
                    }

                    user = User(idToken)

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("userId", user?.id)
                    intent.putExtra("name", user?.name)
                    intent.putExtra("email", user?.email)
                    startActivity(intent)
                    finish()
                }
            })
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
