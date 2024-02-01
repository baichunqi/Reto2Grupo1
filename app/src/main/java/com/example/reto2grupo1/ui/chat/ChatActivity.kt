package com.example.reto2grupo1.ui.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.reto2grupo1.R
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.data.repository.local.RoomMessageDataSource
import com.example.reto2grupo1.data.repository.remote.RemoteChatDataSource
import com.example.reto2grupo1.databinding.ActivityChatBinding
import com.example.reto2grupo1.ui.AddUser.AddUserActivity
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatActivity : ComponentActivity() {
    var chatId : String = ""
    private val TAG = "ChatActivity"
    private lateinit var chatAdapter: ChatAdapter
    private val localMessageRepository = RoomMessageDataSource()
    private val messageRepository = RemoteChatDataSource()
    private val viewModel: ChatViewModel by viewModels { ChatViewModelFactory(messageRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatId = intent.getStringExtra("id").toString()
        chatAdapter = ChatAdapter(chatId)
        binding.chatView.adapter = chatAdapter


        viewModel.startSocket()

        val intent = intent

        val chatName = intent.getStringExtra("name")
        binding.txtAddUser.text = chatName
        Log.i("idChat", chatId.toString())

        binding.imageViewBack.setOnClickListener(){
            finish()
        }

        if (chatId != null) {
            viewModel.getChatContent(chatId.toInt())
        }
        connectToSocket(binding)
        onMessagesChange()

        syncData(chatId.toInt())

        viewModel.connected.observe(this,Observer{
         when (it.status){
             Resource.Status.SUCCESS -> {
                 Log.d(TAG, "conect observe success")

             }
             Resource.Status.ERROR -> {
                 Log.d(TAG, "conect observe error")
                 Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
             }
             Resource.Status.LOADING -> {
                 // de momento
                 Log.d(TAG, "conect observe loading")
                 val toast = Toast.makeText(this, "Cargando..", Toast.LENGTH_LONG)
                 toast.setGravity(Gravity.TOP, 0, 0)
                 toast.show()
             }
         }
        })


        binding.imageView8.setOnClickListener() {
            showPopup(it)
        }


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

            lifecycleScope.launch {
                val messagesResource = localMessageRepository.getChatMessages(chatId.toInt())
                when (messagesResource.status) {
                    Resource.Status.SUCCESS -> {
                        val messages = messagesResource.data
                        chatAdapter.submitList(messages)
                        chatAdapter.notifyDataSetChanged()
                        // Mostrar los mensajes en la interfaz de usuario
                    }
                    Resource.Status.ERROR -> {
                        // Manejar el error
                    }
                    Resource.Status.LOADING -> {
                        // Mostrar un indicador de carga
                    }
                }
            }

    }

    private fun connectToSocket(binding: ActivityChatBinding) {
        binding.imageView9.setOnClickListener() {
            Log.e("pulsado", "enviar pulsado")
            val message = binding.editTextUsername2.text.toString();
            Log.i("EnviMessage", message)
            Log.i("EnviMessage", intent.getStringExtra("id").toString())
            binding.editTextUsername2.setText("")
            viewModel.onSendMessage(message, intent.getStringExtra("id").toString())
        }
    }

    fun showPopup(v : View){
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_chat, popup.menu)
        popup.setOnMenuItemClickListener() { menuItem ->
            when(menuItem.itemId){
                R.id.addUser-> {
                    intent = Intent(this, AddUserActivity::class.java)
                    intent.putExtra("id",chatId)
                    startActivity(intent)
                }
                R.id.delUser-> {
                    Toast.makeText(this, "Borrar usuario", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        popup.show()
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.i("pruebaDestroy", "Desconectando")
        viewModel.stopSocket()
    }
    private fun syncData(num: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("sync", localMessageRepository.getChatMessages(chatId.toInt()).toString())
                // Obtener datos del repositorio remoto
                val remoteData = messageRepository.getChatMessages(num)

                // Verificar si la obtención de datos remotos fue exitosa
                if (remoteData.status == Resource.Status.SUCCESS) {
                    val remoteChatMessage = remoteData.data ?: emptyList()

                    // Obtener chats locales
                    val localChatMessageResource = localMessageRepository.getChatMessages(num)

                    // Verificar si la obtención de datos locales fue exitosa
                    if (localChatMessageResource.status == Resource.Status.SUCCESS) {
                        val localChats = localChatMessageResource.data ?: emptyList()

                        // Identificar chats nuevos y actualizados
                        val chatsToAddOrUpdate = remoteChatMessage.filter { remoteChat ->
                            localChats.none { it.id == remoteChat.id }
                        }

                        // Identificar chats a eliminar
                        val chatsToDelete = localChats.filter { localChat ->
                            remoteChatMessage.none { it.id == localChat.id }
                        }

                        // Agregar o actualizar chats en el repositorio local
                        chatsToAddOrUpdate.forEach { chat ->
                            localMessageRepository.createMessage(chat)
                        }
                        Log.i("idChat", localMessageRepository.getChatMessages(num).toString())

//                        // Eliminar chats en el repositorio local
//                        chatsToDelete.forEach { chat ->
//                            localMessageRepository.deleteChat(chat)
//                        }

                        // Actualizar la interfaz de usuario según el número proporcionado
                        withContext(Dispatchers.Main) {
                            onMessagesChange()
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

}