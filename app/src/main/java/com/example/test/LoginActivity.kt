package com.example.test
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.example.test.databinding.ActivityLoginBinding
import com.example.test.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar;


class LoginActivity : AppCompatActivity() {
    // App and user status
    private lateinit var account: Auth0
    private var appJustLaunched = true
    private var userIsAuthenticated = false

    // Auth0 data
    private var user = User()
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        binding.buttonLogin.setOnClickListener { login() }

    }
    private fun login() {
        WebAuthProvider
            .login(account)
            .withScheme("app")
            .start(this, object : Callback<Credentials, AuthenticationException> {

                override fun onFailure(exception: AuthenticationException) {
                    // The user either pressed the “Cancel” button
                    // on the Universal Login screen or something unusual happened.
                    showSnackBar("You need to log in to use the app.")
                }

                override fun onSuccess(credentials: Credentials) {
                    // The user successfully logged in.
                    val idToken = credentials.idToken

                    if (idToken == null) {
                        showSnackBar("Login failed: No ID token received.")
                        return
                    }

                    user = User(idToken) // KEEPS USER INFO
                    userIsAuthenticated = true

                   // showSnackBar(getString(R.string.login_success_message, user.name))

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    // updateUI()
                }
            })
    }

    private fun showSnackBar(text: String) {
        Snackbar
            .make(
                binding.root,
                text,
                Snackbar.LENGTH_LONG
            ).show()
    }


}