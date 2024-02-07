package com.example.reto2grupo1.ui.changePassword

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.reto2grupo1.data.repository.remote.RemoteEmailDataSource
import com.example.reto2grupo1.databinding.ActivityChangePasswordBinding
import com.example.reto2grupo1.ui.changePassword.ChangePasswordViewModel
import java.util.UUID

class ChangePasswordActivity : ComponentActivity() {

    private val emailRepository = RemoteEmailDataSource()
    private val viewModel: ChangePasswordViewModel by viewModels {
        ChangePasswordViewModelFactory(emailRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendEmailButton.setOnClickListener {
            val email = binding.sendEmailEditText.text.toString()
            val subject = "Recuperacion de contraseña"
            val message = "Para cambiar su contraseña, haga click aqui: http://10.5.7.202/recover-password?email=$email " +
                    "Si usted no ha solicitado un cambio de contraseña, ignore este correo."
            viewModel.sendEmail(email, subject, message)
            Toast.makeText(this, "Solicitud enviada", Toast.LENGTH_SHORT).show()
        }
    }
}
