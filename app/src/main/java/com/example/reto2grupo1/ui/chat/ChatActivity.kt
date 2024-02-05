package com.example.reto2grupo1.ui.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.R
import com.example.reto2grupo1.data.repository.local.RoomMessageDataSource
import com.example.reto2grupo1.data.repository.remote.RemoteChatDataSource
import com.example.reto2grupo1.data.service.LocationService
import com.example.reto2grupo1.databinding.ActivityChatBinding
import com.example.reto2grupo1.ui.AddUser.AddUserActivity
import com.example.reto2grupo1.ui.showUsers.ShowUsersActivity
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ChatActivity : ComponentActivity() {
    var chatId : String = ""
    private val TAG = "ChatActivity"
    private var lastReceivedLocation: Location? = null
    private var imageByteArray: ByteArray? = null
    private var imageBase64: String? = null
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    private lateinit var chatAdapter: ChatAdapter
    private val localMessageRepository = RoomMessageDataSource()
    private val messageRepository = RemoteChatDataSource()
    private val viewModel: ChatViewModel by viewModels { ChatViewModelFactory(messageRepository)
    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Manejar la ubicación recibida desde el servicio
            Log.d("ChatActivityLocation", "Receiver: Location received.")
            val location = intent?.getParcelableExtra<Location>("location") ?: intent?.getParcelableExtra("location")
            if (location != null) {
                lastReceivedLocation = location
                Log.d("ChatActivityLocation", "Ubicación recibida: Latitud=${location.latitude}, Longitud=${location.longitude}")
            } else{
                Log.d("ChatActivityLocation", "Loation null",)
            }
        }
    }

    private val takePictureLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap?
                imageBitmap?.let {
                    imageByteArray = convertBitmapToByteArray(it)
                    imageBase64 = convertByteArrayToBase64(imageByteArray!!)
                    Log.d(TAG, "Contenido de imageBase64: $imageBase64")

                    // Actualiza la variable imageBase64 en el ViewModel
                    viewModel.updateImageBase64(imageBase64!!)
                }
            } else {
                Log.d(TAG, "Error al capturar la foto.")
            }
        }
    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    private fun convertByteArrayToBase64(byteArray: ByteArray) : String {
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentLocation = Intent(MyApp.context, LocationService::class.java)
        MyApp.context.startForegroundService(intentLocation)

        val binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatId = intent.getStringExtra("id").toString()
        chatAdapter = ChatAdapter(chatId)
        binding.chatView.adapter = chatAdapter

        val filter = IntentFilter("locationUpdate")
        registerReceiver(locationReceiver, filter)

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

        viewModel.imageBase64.observe(this, Observer { newImageBase64 ->
            Log.d(TAG, "ImageBase64 actualizado: $newImageBase64")
            viewModel.onSendMessage(newImageBase64, intent.getStringExtra("id").toString())
        })

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

        binding.txtAddUser.setOnClickListener(){
            var intent = Intent(this, ShowUsersActivity::class.java)
            intent.putExtra("id",chatId)
            startActivity(intent)
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
        binding.imageView10.setOnClickListener(){
            showPopupUtils(it)
        }
    }

    fun showPopupUtils(v : View){
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_utils_chat, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.image-> {
                    takePictureLauncher.launch(takePictureIntent)
                }
                R.id.location-> {
                    val lastLocation = obtenerUltimaUbicacion()
                    if (lastLocation != null) {
                        // Hacer algo con la última ubicación en respuesta al clic
                        Log.d("ChatActivity", "Última ubicación al hacer clic: Latitud=${lastLocation.latitude}, Longitud=${lastLocation.longitude}")
                        val message = lastLocation.latitude.toString() + " " + lastLocation.longitude.toString()
                        viewModel.onSendMessage(message, intent.getStringExtra("id").toString())
                    } else {
                        Log.d("ChatActivity", "No hay ubicación disponible.")
                    }
                }
            }
            true
        }
        popup.show()
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
                R.id.abandonar-> {
                    Toast.makeText(this, "Abandonando grupo", Toast.LENGTH_SHORT).show()
                    leaveChat()
                    finish()
                }
            }
            true
        }
        popup.show()
    }
    fun leaveChat(){
        intent.getStringExtra("id")?.let { viewModel.getOutChat(it.toInt()) }
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.i("pruebaDestroy", "Desconectando")
        viewModel.stopSocket()
        unregisterReceiver(locationReceiver)
        val intent = Intent(MyApp.context, LocationService::class.java)
        MyApp.context.stopService(intent)
    }
    private fun obtenerUltimaUbicacion(): Location? {
        return lastReceivedLocation
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

                        // Esto es para eliminar, pero no deberia de poder eliminar un mensaje, se deja aqui por si acaso(no hay funcion para eliminar, hay que crearlo en caso necesiario)
//                        val chatsToDelete = localChats.filter { localChat ->
//                            remoteChatMessage.none { it.id == localChat.id }
//                        }
//                        // Eliminar chats en el repositorio local
//                        chatsToDelete.forEach { chat ->
//                            localMessageRepository.deleteChat(chat)
//                        }
                        // Agregar o actualizar chats en el repositorio local
                        chatsToAddOrUpdate.forEach { chat ->
                            localMessageRepository.createMessage(chat)
                        }
                        Log.i("idChat", localMessageRepository.getChatMessages(num).toString())



                        // Actualizar la interfaz de usuario según el número proporcionado
                        withContext(Dispatchers.Main) {
                            onMessagesChange()
                        }
                    } else {
                        // Manejar el error al obtener datos locales si es necesario
                        Log.i("SyncChat", "Error con obtener mensajes local")

                    }
                } else {
                    // Manejar el error al obtener datos remotos si es necesario
                    Log.i("SyncChat", "Error con obtener mensajes remoto")

                }
            } catch (ex: Exception) {
                Log.e(TAG, "Error during data synchronization: ${ex.message}", ex)
                // Manejar el error de sincronización si es necesario
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}