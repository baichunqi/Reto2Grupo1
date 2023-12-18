package com.example.reto2grupo1.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.reto2grupo1.databinding.ActivityLoginBinding
import com.example.reto2grupo1.ui.chatList.ChatListActivity

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.buttonLogin.setOnClickListener() {
            if(binding.editTextUsername.text.isNullOrEmpty()){
                Toast.makeText(this, "El usuario no puede estar vacio",Toast.LENGTH_SHORT).show()
            }else if(binding.editTextPassword.text.isNullOrEmpty()){
                Toast.makeText(this, "La contraseña no puede estar vacia",Toast.LENGTH_SHORT).show()
            } else {
                intent = Intent(this, ChatListActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}