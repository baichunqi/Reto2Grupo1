package com.example.reto2grupo1.ui.chatList

import android.content.ContentValues.TAG
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
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.R
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.data.repository.local.RoomChatDataSource
import com.example.reto2grupo1.data.repository.remote.RemoteChatListDataSource
import com.example.reto2grupo1.data.repository.remote.RemoteCreateChatDataSource
import com.example.reto2grupo1.data.service.SocketService
import com.example.reto2grupo1.databinding.ActivityChatListBinding
import com.example.reto2grupo1.ui.changePassword.ChangePasswordActivity
import com.example.reto2grupo1.ui.chat.ChatActivity
import com.example.reto2grupo1.ui.createGroup.CreateGroupActivity
import com.example.reto2grupo1.ui.createGroup.CreateGroupViewModel
import com.example.reto2grupo1.ui.createGroup.CreateGroupViewModelFactory
import com.example.reto2grupo1.ui.deleteChat.DeleteChatActivity
import com.example.reto2grupo1.ui.joinChat.JoinChatActivity
import com.example.reto2grupo1.ui.login.LoginActivity
import com.example.reto2grupo1.ui.register.RegisterActivity
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ChatListActivity  : ComponentActivity()  {

    private lateinit var chatListAdapter: ChatListAdapter
    private val chatListRepository = RemoteChatListDataSource()
    private var esPublico : Boolean = true
    private var chatRepository = RoomChatDataSource()
    private val createGroupRepository = RemoteCreateChatDataSource()
    private val viewModel: ChatListViewModel by viewModels { ChatListViewModelFactory(chatListRepository)
    }
    private val createChatViewModel: CreateGroupViewModel by viewModels { CreateGroupViewModelFactory(createGroupRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {

        syncData()
        super.onCreate(savedInstanceState)
        val binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val socketIntent = Intent(MyApp.context, SocketService::class.java)
        MyApp.context.startForegroundService(socketIntent)

        chatListAdapter = ChatListAdapter(this)
        binding.recyclerView.adapter = chatListAdapter

        binding.imageView7.setOnClickListener() {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.editTextSearch.addTextChangedListener(){
            chatListAdapter.filter(binding.editTextSearch.text.toString(), esPublico)
        }

        binding.imageView8.setOnClickListener() {
            showChatPopup(it)
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
        viewModel.deleted.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        viewModel.getChats()
                    }

                    Resource.Status.ERROR -> {
                        Toast.makeText(this, it.message ?: "Error desconocido", Toast.LENGTH_LONG)
                            .show()
                        Log.e("ChatListActivity", "Error al cargar datos: ${it.message}")
                    }

                    Resource.Status.LOADING -> {
                        // de momento
                    }
                }
            }
        })

        lifecycleScope.launch {
            val chatsResource = chatRepository.getChats()
            when (chatsResource.status) {
                Resource.Status.SUCCESS -> {
                    val chats = chatsResource.data
                    chatListAdapter.submitList(chats)
                    chatListAdapter.submitChatList(chats)
                    chatListAdapter.filter(binding.editTextSearch.text.toString(), esPublico)
                    // Hacer algo con la lista de chats, como mostrarla en un RecyclerView
                }
                Resource.Status.ERROR -> {
                    // Manejar el error, si es necesario
                }
                Resource.Status.LOADING -> {
                    // Manejar el estado de carga, si es necesario
                }
            }
        }

        createChatViewModel.createChatResult.observe(this, Observer {
            Log.e("PruebasDia1", "ha ocurrido add en la lista de favs")

            if (it != null) {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        viewModel.getChats()
                    }

                    Resource.Status.ERROR -> {
                        Toast.makeText(this, it.message ?: "Error desconocido", Toast.LENGTH_LONG)
                            .show()
                        Log.e("ListSongsActivity", "Error al cargar datos: ${it.message}")
                    }

                    Resource.Status.LOADING -> {
                        Log.d("ListSongsActivity", "Cargando datos...")
                    }
                }
            }
        })

    }

    infix fun selectChat(chat : Chat){
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("id",chat.id.toString())
        intent.putExtra("name", chat.name)
        //Añadir la conexion
        startActivity(intent)
    }

    fun showChatPopup(v : View){
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
                R.id.BorrarGrupo-> {
                    intent = Intent(this, DeleteChatActivity::class.java)
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
            try {
                // Obtener datos del repositorio remoto
                val remoteData = chatListRepository.getChatList()

                // Verificar si la obtención de datos remotos fue exitosa
                if (remoteData.status == Resource.Status.SUCCESS) {
                    val remoteChats = remoteData.data ?: emptyList()

                    // Obtener chats locales
                    val localChatsResource = chatRepository.getChats()

                    // Verificar si la obtención de datos locales fue exitosa
                    if (localChatsResource.status == Resource.Status.SUCCESS) {
                        val localChats = localChatsResource.data ?: emptyList()

                        // Identificar chats nuevos y actualizados
                        val chatsToAddOrUpdate = remoteChats.filter { remoteChat ->
                            localChats.none { it.id == remoteChat.id }
                        }

                        // Identificar chats a eliminar
                        val chatsToDelete = localChats.filter { localChat ->
                            remoteChats.none { it.id == localChat.id }
                        }

                        // Agregar o actualizar chats en el repositorio local
                        chatsToAddOrUpdate.forEach { chat ->
                            chatRepository.createChat(chat)
                        }

                        // Eliminar chats en el repositorio local
                        chatsToDelete.forEach { chat ->
                            chatRepository.deleteChat(chat)
                        }
                    } else {
                        // Manejar el error al obtener datos locales si es necesario
                    }
                } else {
                    // Manejar el error al obtener datos remotos si es necesario
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Error during data synchronization: ${ex.message}", ex)
                // Manejar el error de sincronización si es necesario
            }
        }
    }
    override fun onResume(){
        super.onResume()
        viewModel.getChats()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        finish()
        startActivity(intent)
    }
}