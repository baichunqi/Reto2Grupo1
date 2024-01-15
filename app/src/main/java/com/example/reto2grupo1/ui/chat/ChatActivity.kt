package com.example.reto2grupo1.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.reto2grupo1.data.repository.remote.RemoteChatDataSource
import com.example.reto2grupo1.databinding.ActivityChatBinding
import com.example.reto2grupo1.databinding.ActivityLoginBinding
import com.example.reto2grupo1.ui.chatList.ChatListAdapter
import com.example.reto2grupo1.ui.chatList.ChatListViewModel
import com.example.reto2grupo1.ui.chatList.ChatListViewModelFactory
import com.example.reto2grupo1.utils.Resource

class ChatActivity : ComponentActivity() {

    private val TAG = "ChatActivity"
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

        onMessagesChange()
        connectToSocket(binding)
    }


    private fun onMessagesChange() {
        viewModel.messages.observe(this, Observer {
            Log.d(TAG, "messages change")
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Log.d(TAG, "messages observe success")
                    if (!it.data.isNullOrEmpty()) {
                        chatAdapter.submitList(it.data)
                        chatAdapter.notifyDataSetChanged()
                    }
                }
                Resource.Status.ERROR -> {
                    Log.d(TAG, "messages observe error")
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                    // de momento
                    Log.d(TAG, "messages observe loading")
                    val toast = Toast.makeText(this, "Cargando..", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
            }
        })
    }

    private fun connectToSocket(binding: ActivityChatBinding) {
            viewModel.startSocket()

        binding.imageView9.setOnClickListener() {
            val message = binding.editTextUsername2.text.toString();
            binding.editTextUsername2.setText("")
            viewModel.onSendMessage(message)
        }
    }

}