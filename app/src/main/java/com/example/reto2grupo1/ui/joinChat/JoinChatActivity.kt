package com.example.reto2grupo1.ui.joinChat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.reto2grupo1.data.repository.remote.RemoteChatListDataSource
import com.example.reto2grupo1.databinding.ActivityJoinpublicchatBinding
import com.example.reto2grupo1.ui.chatList.ChatListActivity
import com.example.reto2grupo1.utils.Resource

class JoinChatActivity : ComponentActivity(){

    private val chatListRepository = RemoteChatListDataSource()
    private lateinit var  joinChatListAdapter : JoinChatListAdapter

    private val viewModel: JoinChatViewModel by viewModels {
        JoinChatViewModelFactory(chatListRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityJoinpublicchatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        joinChatListAdapter = JoinChatListAdapter(this)
        binding.recyclerViewJoin.adapter = joinChatListAdapter
        viewModel.chats.observe(this, Observer {
            when(it.status){
                Resource.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) {
                        Log.i("PruebaChat", "Ha ocurrido un cambio en la lista")
                        Log.i("PruebaChat", viewModel.chats.value.toString())
                        joinChatListAdapter.submitList(it.data)
                        joinChatListAdapter.submitChatList(it.data)
                    }
                }
                Resource.Status.ERROR -> {
                    val errorMessage = it.message
                    if (errorMessage != null) {
                        if (errorMessage.contains("409")) {
                            Toast.makeText(this, "Ya esta unido a ese grupo", Toast.LENGTH_LONG)
                                .show()
                        } else if (errorMessage.contains("200")){
                            Toast.makeText(this, "Te has unido", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, ChatListActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                Resource.Status.LOADING -> {
                    // No implementado
                }
            }
        })
    }
    infix fun joinChat(chatId: Int?){
        Log.d("estoentra2","estoentra2");
        if (chatId != null) {
            viewModel.joinToChat(chatId)
        }
        val intent = Intent(this, ChatListActivity::class.java)
        startActivity(intent)
        finish()
    }
}