package com.example.reto2grupo1.ui.deleteChat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.reto2grupo1.data.repository.remote.RemoteChatListDataSource
import com.example.reto2grupo1.databinding.ActivityDeleteChatBinding
import com.example.reto2grupo1.ui.chatList.ChatListViewModel
import com.example.reto2grupo1.ui.chatList.ChatListViewModelFactory
import com.example.reto2grupo1.ui.joinChat.JoinChatListAdapter
import com.example.reto2grupo1.utils.Resource

class DeleteChatActivity : ComponentActivity() {

    private lateinit var deleteChatAdapter: DeleteChatAdapter
    private val chatRepository = RemoteChatListDataSource()

    private val viewModel: ChatListViewModel by viewModels { ChatListViewModelFactory(chatRepository)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDeleteChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deleteChatAdapter = DeleteChatAdapter { chat ->
            chat.id?.let{viewModel.onDeleteChat(it)}
        }
      binding.chatList.adapter = deleteChatAdapter

        viewModel.chats.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    Log.d("Status", "success")
                    if (!resource.data.isNullOrEmpty()) {
                        deleteChatAdapter.submitList(resource.data)
                    }
                }
                Resource.Status.ERROR -> {
                    Log.d("Status", "error")
                    val errorMessage = when (resource.code) {
                        // Agrega más casos según los códigos de error que esperas
                        404 -> "Chat no encontrado"
                        500 -> "Error interno del servidor"
                        else -> resource.message ?: "Error desconocido"
                    }
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                    // de momento
                    Log.d("Status", "loading")
                    Toast.makeText(this, "Cargando..", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}