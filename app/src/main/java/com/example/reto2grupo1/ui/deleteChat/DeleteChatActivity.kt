package com.example.reto2grupo1.ui.deleteChat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.reto2grupo1.data.repository.remote.RemoteChatListDataSource
import com.example.reto2grupo1.databinding.ActivityDeleteChatBinding
import com.example.reto2grupo1.ui.chatList.ChatListActivity
import com.example.reto2grupo1.ui.chatList.ChatListViewModel
import com.example.reto2grupo1.ui.chatList.ChatListViewModelFactory
import com.example.reto2grupo1.utils.Resource

class DeleteChatActivity : ComponentActivity() {

    private lateinit var deleteChatAdapter: DeleteChatAdapter
    private val chatRepository = RemoteChatListDataSource()

    private val chatViewModel: ChatListViewModel by viewModels { ChatListViewModelFactory(chatRepository)}
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDeleteChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deleteChatAdapter = DeleteChatAdapter { chat ->
            chat.id?.let{chatViewModel.onDeleteChat(it)}
        }
      binding.chatList.adapter = deleteChatAdapter

        chatViewModel.chats.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    Log.d("Status", "success")
                    if (!resource.data.isNullOrEmpty()) {
                        deleteChatAdapter.submitList(resource.data)
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                    // de momento
                    Log.d("Status", "loading")
                    Toast.makeText(this, "Cargando..", Toast.LENGTH_LONG).show()
                }
            }
        })
        chatViewModel.deleted.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        chatViewModel.getChats()
                    }

                    Resource.Status.ERROR -> {
                        val errorMessage = resource.message
                        if (errorMessage != null) {
                            if (errorMessage.contains("403")) {
                                Toast.makeText(this, "Compruebe sus privilegios", Toast.LENGTH_LONG)
                                    .show()
                            } else if (errorMessage.contains("200")){
                                Toast.makeText(this, "Grupo borrado con exito", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, ChatListActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }

                    Resource.Status.LOADING -> {
                        Log.d("ChatListActivity", "Cargando datos...")
                    }
                }
            }
        })
    binding.imageView11.setOnClickListener(){
        finish()
    }
    }
}