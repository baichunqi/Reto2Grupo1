package com.example.reto2grupo1.ui.AddUser

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.remote.RemoteAuthenticationRepository
import com.example.reto2grupo1.data.repository.remote.RemoteChatDataSource
import com.example.reto2grupo1.databinding.ActivityAddUserBinding
import com.example.reto2grupo1.utils.Resource

class AddUserActivity : ComponentActivity()  {
    var chatId : String = "0"
    private lateinit var addUserAdapter : AddUserAdapter
    private val authenticationRepository = RemoteAuthenticationRepository()
    private val chatRepository = RemoteChatDataSource()
    private val viewModel: AddUserViewModel by viewModels{AddUserModelFactory(authenticationRepository, chatRepository)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        chatId = intent.getStringExtra("id").toString()
        Log.i("Id de chat", chatId.toString())
        addUserAdapter = AddUserAdapter(this)
        binding.usersView.adapter = addUserAdapter

        viewModel.users.observe(this, Observer{
            when (it.status){
                Resource.Status.SUCCESS ->{
                    if(!it.data.isNullOrEmpty()){
                        addUserAdapter.submitList(it.data)
                    } else{
                        Toast.makeText(this, "No hay usuarios", Toast.LENGTH_SHORT).show()
                    }
                }
                Resource.Status.ERROR ->{
                    Toast.makeText(this, "Error al cargar los usuarios", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.LOADING -> {
                    Toast.makeText(this, "Cargando...", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.join.observe(this, Observer{
            when(it.status){
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, "Usuario añadido al grupo", Toast.LENGTH_SHORT).show()
                    finish()
                }
                Resource.Status.ERROR -> {
                    finish()
                }
                Resource.Status.LOADING -> {

                }
            }
        })



        binding.imageViewBack.setOnClickListener() {
            finish()
        }
    }
    infix fun selectUser(user : User){
        Log.i("prueba añadir", user.name)
        Log.i("prueba añadir", chatId)
        if (chatId != null) {
            viewModel.addUser(chatId.toInt(), user.id)
        }
    }
}