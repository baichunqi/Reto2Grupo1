package com.example.reto2grupo1.ui.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.reto2grupo1.data.repository.remote.RemoteChatDataSource
import com.example.reto2grupo1.databinding.ActivityChatBinding
import com.example.reto2grupo1.databinding.ActivityLoginBinding
import com.example.reto2grupo1.ui.chatList.ChatListAdapter
import com.example.reto2grupo1.ui.chatList.ChatListViewModel
import com.example.reto2grupo1.ui.chatList.ChatListViewModelFactory

class ChatActivity : ComponentActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private val messageRepository = RemoteChatDataSource()

    private val viewModel: ChatViewModel by viewModels { ChatViewModelFactory(messageRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatAdapter = ChatAdapter()
        binding.chatView.adapter = chatAdapter

    }
}