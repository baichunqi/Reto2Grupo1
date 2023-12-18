package com.example.reto2grupo1.ui.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.reto2grupo1.databinding.ActivityChatBinding
import com.example.reto2grupo1.databinding.ActivityLoginBinding

class ChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}