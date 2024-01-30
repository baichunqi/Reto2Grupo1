package com.example.reto2grupo1.ui.AddUser

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.reto2grupo1.data.repository.remote.RemoteAuthenticationRepository
import com.example.reto2grupo1.databinding.ActivityAddUserBinding
import com.example.reto2grupo1.utils.Resource

class AddUserActivity : ComponentActivity()  {

    private lateinit var addUserAdapter : AddUserAdapter
    private val authenticationRepository = RemoteAuthenticationRepository()
    private val viewModel: AddUserViewModel by viewModels{AddUserModelFactory(authenticationRepository)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        val chatId = intent.getStringExtra("id")
        Log.i("Id de chat", chatId.toString())
        addUserAdapter = AddUserAdapter()
        binding.usersView.adapter = addUserAdapter

        viewModel.users.observe(this, Observer{
            when (it.status){
                Resource.Status.SUCCESS ->{
                    if(!it.data.isNullOrEmpty()){
                        Log.i("Prueba User", viewModel.users.value.toString())
                        addUserAdapter.submitList(it.data)
                    } else{
                        Toast.makeText(this, "No hay usuarios", Toast.LENGTH_SHORT).show()
                    }
                }
                Resource.Status.ERROR ->{
                    Toast.makeText(this, "Error al cargar los usuarios", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.LOADING -> {

                }
            }
        })


        binding.imageViewBack.setOnClickListener() {
            finish()
        }
    }

}