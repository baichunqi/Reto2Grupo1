package com.example.reto2grupo1.ui.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import com.example.reto2grupo1.databinding.ActivityRegisterBinding
import com.example.reto2grupo1.ui.chatList.ChatListActivity

class RegisterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var defaultPass : Boolean = false
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                defaultPass = false
            } else {
                defaultPass = extras.getBoolean("defaultPass")
            }
        }

        if (!defaultPass) {
            binding.editTextNombre.isEnabled = false
            binding.editTextApellido.isEnabled = false
            binding.editTextDNI.isEnabled = false
            binding.spinnerCicloFormativo.isEnabled = false
            binding.editTextCurso.isEnabled = false
            binding.checkBoxFCTDUAL.isEnabled = false
            binding.buttonRegistro.isVisible = false

            binding.imageView4.setOnClickListener() {
                val intent = Intent(this, ChatListActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            binding.buttonCambioDeContraseA.isVisible = false

            binding.imageView4.setOnClickListener() {
                Toast.makeText(this, "debes revisar que los campos sean correctos para poder continuar", Toast.LENGTH_SHORT).show()
            }
        }


    }
}