package com.example.reto2grupo1.ui.showUsers

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.reto2grupo1.data.repository.remote.RemoteUserDataSource
import com.example.reto2grupo1.databinding.ActivityShowChatUsersBinding
import com.example.reto2grupo1.utils.Resource

class ShowUsersActivity : ComponentActivity() {
    var chatId :String = ""
    private lateinit var showUserAdapter: ShowUserAdapter
    private var userRepository = RemoteUserDataSource()

    private val viewModel: ShowUserViewModel by viewModels { ShowUserViewModelFactory(userRepository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityShowChatUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatId = intent.getStringExtra("id").toString()
        showUserAdapter=ShowUserAdapter(this)
        binding.RecyclerViewUsers.adapter = showUserAdapter
        binding.imageViewBack2.setOnClickListener(){
            finish()
        }

        if (chatId != null) {
            viewModel.getList(chatId.toInt())
        }
        viewModel.users.observe(this, Observer {
            when(it.status){
                Resource.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) {
                        Log.i("PruebaChat", "Ha ocurrido un cambio en la lista")
                        showUserAdapter.submitList(it.data)
                        showUserAdapter.submitChatList(it.data)
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Status.LOADING -> {
                    // No implementado
                }
            }
        })
    }

}