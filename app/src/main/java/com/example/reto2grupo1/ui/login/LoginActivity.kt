package com.example.reto2grupo1.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.data.repository.remote.RemoteAuthenticationRepository
import com.example.reto2grupo1.databinding.ActivityLoginBinding
import com.example.reto2grupo1.ui.chatList.ChatListActivity
import com.example.reto2grupo1.ui.register.RegisterActivity
import com.example.reto2grupo1.utils.Resource

class LoginActivity : ComponentActivity() {

    private val authenticationRepository = RemoteAuthenticationRepository();

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            authenticationRepository
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // cargamos el XML en la actividad
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            val dato1 = extras.getString("email")
            val dato2 = extras.getString("password")
            binding.editTextUsername.setText(dato1)
            binding.editTextPassword.setText(dato2)
        }

        val savedUsername = MyApp.userPreferences.fetchAuthLogin()
        val savedPassword = MyApp.userPreferences.fetchAuthPassword()
        if (!savedUsername.isNullOrBlank() && !savedPassword.isNullOrBlank()) {
            binding.editTextUsername.setText(savedUsername)
            binding.editTextPassword.setText(savedPassword)
            binding.checkBox2.isChecked = true
        }
        if (!binding.checkBox2.isChecked){
            binding.editTextUsername.setText("")
            binding.editTextPassword.setText("")
            MyApp.userPreferences.restartPreference()
            binding.checkBox2.isChecked = false

        }

        binding.buttonLogin.setOnClickListener() {

            viewModel.onLoginClick(
                binding.editTextUsername.text.toString(),
                binding.editTextPassword.text.toString()
            )
        }
        viewModel.login.observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    it.data?.let { data ->
                        Log.e("Antes de guardar", "antes de guardar")
                        MyApp.userPreferences.restartPreference()
    Log.e("Token", data.accessToken)
                        if (binding.checkBox2.isChecked) {
                            MyApp.userPreferences.saveAuthTokenWithPs(
                                binding.editTextPassword.text.toString(),
                                data.email,
                                data.accessToken,

                            )
                        } else if (!binding.checkBox2.isChecked) {
                            MyApp.userPreferences.restartPreference()
                            MyApp.userPreferences.saveAuthToken(
                                data.email,
                                data.accessToken
                            )
                        }
                        Log.e("Despues de guardar", "Despues de guardar")
                            val pass = binding.editTextPassword.text.toString()
                        if (pass == "Elorrieta00"){
                        val intent = Intent(this, RegisterActivity::class.java).apply {

                            Log.e("PruebaInicia", "Cargando los chats")
                        }
                        startActivity(intent)
                        finish()
                        } else{
                            val intentChat = Intent(this, ChatListActivity::class.java).apply {

                                Log.e("PruebaInicia", "Cargando intent chats")

                            }
                            startActivity(intentChat)
                            finish()
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    val errorMessage = it.message ?: "Unknown error"
                    if (errorMessage.contains("400")) {
                        Toast.makeText(this, "Username o Password incorrecto", Toast.LENGTH_LONG)
                            .show()
                    } else if (errorMessage.contains("401")) {
                        Toast.makeText(this, "No autorizado", Toast.LENGTH_LONG).show()
                        // Otro manejo de errores

                    } else if (errorMessage.contains("404")) {
                        Toast.makeText(this, "Error con el servidor", Toast.LENGTH_LONG).show()
                        // Otro manejo de errores
                    } else {
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                    //Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()

                }
                Resource.Status.LOADING -> {
                    // de momento
                }
            }
        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}