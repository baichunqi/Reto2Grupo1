package com.example.reto2grupo1.ui.chatList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.R
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.remote.RemoteChatListDataSource
import com.example.reto2grupo1.databinding.ActivityChatListBinding
import com.example.reto2grupo1.databinding.ActivityCreateChatBinding
import com.example.reto2grupo1.ui.chat.ChatActivity
import com.example.reto2grupo1.ui.createGroup.CreateGroupActivity
import com.example.reto2grupo1.ui.register.RegisterActivity
import com.example.reto2grupo1.utils.Resource


class ChatListActivity  : ComponentActivity()  {

    private lateinit var chatListAdapter: ChatListAdapter
    private val chatListRepository = RemoteChatListDataSource()

    private val viewModel: ChatListViewModel by viewModels { ChatListViewModelFactory(chatListRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatListAdapter = ChatListAdapter(this)
        binding.recyclerView.adapter = chatListAdapter

        binding.imageView7.setOnClickListener() {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.imageView8.setOnClickListener() {
            showPopup(it)
        }

        viewModel.chats.observe(this, Observer {
            when(it.status){
                Resource.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) {
                        Log.i("PruebaChat", "Ha ocurrido un cambio en la lista")
                        Log.i("PruebaChat", viewModel.chats.value.toString())
                        chatListAdapter.submitList(it.data)
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

    infix fun selectChat(chat : Chat){
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("id",chat.id.toString())
        intent.putExtra("name", chat.name)
        startActivity(intent)
    }

    fun showPopup(v : View){
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.CrearGrupo-> {
                    intent = Intent(this, CreateGroupActivity::class.java)
                    startActivity(intent)
                }
                R.id.UnirseGrupo-> {
                    Toast.makeText(this, "Ayuda", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        popup.show()
    }
}