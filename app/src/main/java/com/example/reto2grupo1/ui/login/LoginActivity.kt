package com.example.reto2grupo1.ui.login

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.data.AuthenticationRequest
import com.example.reto2grupo1.data.repository.local.RoomAuthDataSource
import com.example.reto2grupo1.data.repository.local.RoomChatDataSource
import com.example.reto2grupo1.data.repository.local.RoomUserDataSource
import com.example.reto2grupo1.data.repository.remote.RemoteAuthenticationRepository
import com.example.reto2grupo1.databinding.ActivityLoginBinding
import com.example.reto2grupo1.ui.changePassword.ChangePasswordActivity
import com.example.reto2grupo1.ui.chatList.ChatListActivity
import com.example.reto2grupo1.ui.register.RegisterActivity
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private val authenticationRepository = RemoteAuthenticationRepository();
    private val localLoginRepository = RoomAuthDataSource();
    private val localUserRepository = RoomUserDataSource();
    private val localChat = RoomChatDataSource();
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
            if (isInternetAvailable(this)) {
                viewModel.onLoginClick(
                    binding.editTextUsername.text.toString(),
                    binding.editTextPassword.text.toString()
                )
            } else {
                // No hay conexión a Internet, intenta iniciar sesión desde la base de datos local
                loginLocally(
                    binding.editTextUsername.text.toString(),
                    binding.editTextPassword.text.toString()
                )
            }
        }

        binding.textViewPassword.setOnClickListener() {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
            finish()
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
                            syncData(binding.editTextPassword.text.toString(), data.email)
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
                            intent.putExtra("password", pass);
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

    private fun syncData(pass: String, email: String) {
        var authenticationRequest = AuthenticationRequest(email,pass);
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var user = authenticationRepository.myInfo().data
                localLoginRepository.saveUser(email, pass)
                if (user != null) {
                    localUserRepository.addUser(user)
                }

            } catch (ex: Exception) {
                Log.e(ContentValues.TAG, "Error durante sicronizacion: ${ex.message}", ex)
            }
        }
    }

    private fun loginLocally(email: String, password: String) {

        CoroutineScope(Dispatchers.IO).launch {

            try {
                Log.d("users",localUserRepository.getUsers().toString() )
                Log.d("allChats",localChat.getAllChats().toString() )

                val isLoggedIn = localLoginRepository.login(email, password)
                if (isLoggedIn) {
                    startActivity(Intent(this@LoginActivity, ChatListActivity::class.java))
                    finish()
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Inicio de sesión fallido. Verifica tus credenciales.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (ex: Exception) {
                Log.e(ContentValues.TAG, "Error durante el inicio de sesión local: ${ex.message}", ex)
            }
        }
    }
    @Suppress("DEPRECATION")
    private fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}