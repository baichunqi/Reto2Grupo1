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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.reto2grupo1.R
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.local.RoomChatDataSource
import com.example.reto2grupo1.data.repository.remote.RemoteChatListDataSource
import com.example.reto2grupo1.databinding.ActivityChatListBinding
import com.example.reto2grupo1.ui.chat.ChatActivity
import com.example.reto2grupo1.ui.createGroup.CreateGroupActivity
import com.example.reto2grupo1.ui.joinChat.JoinChatActivity
import com.example.reto2grupo1.ui.register.RegisterActivity
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatListActivity  : ComponentActivity()  {

    private lateinit var chatListAdapter: ChatListAdapter
    private val chatListRepository = RemoteChatListDataSource()
    private var esPublico : Boolean = true
    private var chatRepository = RoomChatDataSource()
    private val viewModel: ChatListViewModel by viewModels { ChatListViewModelFactory(chatListRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        syncData()
        super.onCreate(savedInstanceState)
        val binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatListAdapter = ChatListAdapter(this)
        binding.recyclerView.adapter = chatListAdapter

        binding.imageView7.setOnClickListener() {
            val intent = Intent(this, RegisterActivity::class.java)
            finish()
            startActivity(intent)

        }

        binding.editTextSearch.addTextChangedListener(){
            chatListAdapter.filter(binding.editTextSearch.text.toString(), esPublico)
        }

        binding.imageView8.setOnClickListener() {
            showPopup(it)
        }

        binding.imageView9.setOnClickListener(){
            showPopupFilter(it, binding.editTextSearch.text.toString())
        }

        viewModel.chats.observe(this, Observer {
            when(it.status){
                Resource.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) {
                        Log.i("PruebaChat", "Ha ocurrido un cambio en la lista")
                        chatListAdapter.submitList(it.data)
                        chatListAdapter.submitChatList(it.data)
                        chatListAdapter.filter(binding.editTextSearch.text.toString(), esPublico)
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

//        lifecycleScope.launch {
//            val chatsResource = chatRepository.getChats()
//            when (chatsResource.status) {
//                Resource.Status.SUCCESS -> {
//                    val chats = chatsResource.data
//                    chatListAdapter.submitList(chats)
//                    chatListAdapter.submitChatList(chats)
//                    chatListAdapter.filter(binding.editTextSearch.text.toString(), esPublico)
//                    // Hacer algo con la lista de chats, como mostrarla en un RecyclerView
//                }
//                Resource.Status.ERROR -> {
//                    // Manejar el error, si es necesario
//                }
//                Resource.Status.LOADING -> {
//                    // Manejar el estado de carga, si es necesario
//                }
//            }
//        }


    }

    infix fun selectChat(chat : Chat){
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("id",chat.id.toString())
        intent.putExtra("name", chat.name)
        //Añadir la conexion
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
                    intent = Intent(this, JoinChatActivity::class.java)
                    startActivity(intent)

                }
            }
            true
        }
        popup.show()
    }

    fun showPopupFilter(v: View, filterText: String){
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_filter, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.publicGroup-> {
                    esPublico = true
                    chatListAdapter.filter(filterText, esPublico)
                }
                R.id.privateGroup-> {
                    esPublico = false
                    chatListAdapter.filter(filterText, esPublico)
                }
            }
            true
        }
        popup.show()

    }
    private fun syncData() {
        CoroutineScope(Dispatchers.IO).launch {
            // Obtener datos del repositorio remoto
            val remoteData = chatListRepository.getChatList()

            // Verificar si hay cambios antes de sincronizar
            if (remoteData.status == Resource.Status.SUCCESS) {
                val remoteChats = remoteData.data ?: emptyList()
                val localChats = chatRepository.getChats().data ?: emptyList()

                // Identificar chats que necesitan ser agregados o actualizados
                val chatsToAddOrUpdate = remoteChats.filter { remoteChat ->
                    !localChats.any { it.id == remoteChat.id }
                }

                // Agregar o actualizar chats en el repositorio local
                chatsToAddOrUpdate.forEach { chat ->
                    chatRepository.createChat(chat)
                }
            }
        }
    }



}