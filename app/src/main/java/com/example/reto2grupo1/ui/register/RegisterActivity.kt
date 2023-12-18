package com.example.reto2grupo1.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.reto2grupo1.databinding.ActivityLoginBinding
import com.example.reto2grupo1.databinding.ActivityRegisterBinding
import com.example.reto2grupo1.ui.chatList.ChatListActivity

class RegisterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)




        binding.imageView4.setOnClickListener() {
            val intent = Intent(this, ChatListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}