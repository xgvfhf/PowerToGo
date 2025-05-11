package com.example.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = intent.getStringExtra("name")
        val userEmail = intent.getStringExtra("email")

        binding.userName.text = userName
        binding.userEmail.text = userEmail
    }
}
