package com.example.reto2grupo1.ui.chat

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
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
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.data.repository.local.RoomMessageDataSource
import com.example.reto2grupo1.data.repository.local.RoomUserDataSource
import com.example.reto2grupo1.data.repository.remote.RemoteChatDataSource
import com.example.reto2grupo1.data.service.LocationService
import com.example.reto2grupo1.data.service.SocketService
import com.example.reto2grupo1.databinding.ActivityChatBinding
import com.example.reto2grupo1.ui.AddUser.AddUserActivity
import com.example.reto2grupo1.ui.showUsers.ShowUsersActivity
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.ByteArrayOutputStream
import java.io.IOException

class ChatActivity : ComponentActivity() {
    var chatId : String = ""
    private val TAG = "ChatActivity"
    private var lastReceivedLocation: Location? = null
    private val FILE_PICK_REQUEST_CODE = 1
    private var imageByteArray: ByteArray? = null
    private lateinit var socketService: SocketService

    private lateinit var binding: ActivityChatBinding
    private var isBind = false
    private var imageBase64: String? = null
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    private lateinit var chatAdapter: ChatAdapter
    private val localMessageRepository = RoomMessageDataSource()
    private val messageRepository = RemoteChatDataSource()
    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(messageRepository)
    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Manejar la ubicación recibida desde el servicio
            val location = intent?.getParcelableExtra<Location>("location") ?: intent?.getParcelableExtra("location")
            if (location != null) {
                lastReceivedLocation = location
                Log.d("ChatActivityLocation", "Ubicación recibida: Latitud=${location.latitude}, Longitud=${location.longitude}")
            } else{
                Log.e("ChatActivityLocation", "Location null",)
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

                    // Actualiza la variable imageBase64 en el ViewModel
                    viewModel.updateImageBase64(imageBase64!!)
                }
            } else {
                Log.e(TAG, "Error al capturar la foto.")
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
    private fun selectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"  // Limita la selección a archivos PDF, puedes cambiarlo según tus necesidades
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, FILE_PICK_REQUEST_CODE)
    }
    private var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val localService = service as SocketService.LocalService
            socketService = localService.service
            isBind = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBind = false
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // El usuario ha seleccionado un archivo
            data?.data?.let { uri ->
                // Obtener el InputStream del archivo a partir de la URI utilizando contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    try {
                        // Convertir el InputStream en un String
                        val text = inputStream.bufferedReader().use { it.readText() }
                        Log.i("File", text)
                        socketService.onSendMessage(text,intent.getStringExtra("id").toString())
                    } catch (e: IOException) {
                        Toast.makeText(this, "Se ha producido un error al subir la foto", Toast.LENGTH_SHORT).show()
                    } finally {
                        // Cerrar el InputStream después de su uso para liberar los recursos
                        inputStream.close()
                    }
                } else {
                    // Manejar el caso en el que no se pudo obtener el InputStream
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentLocation = Intent(MyApp.context, LocationService::class.java)
        MyApp.context.startForegroundService(intentLocation)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatId = intent.getStringExtra("id").toString()
        chatAdapter = ChatAdapter()
        binding.chatView.adapter = chatAdapter

        val filter = IntentFilter("locationUpdate")
        registerReceiver(locationReceiver, filter)

        val intent = intent

        val chatName = intent.getStringExtra("name")
        binding.txtAddUser.text = chatName
        Log.i("idChat", chatId)

        binding.imageViewBack.setOnClickListener(){
            finish()
        }

        if (chatId != null) {
            viewModel.getChatContent(chatId.toInt())
        }
        connectToSocket(binding)
        onMessagesChange(binding)

        viewModel.imageBase64.observe(this, Observer { newImageBase64 ->
            socketService.onSendMessage(newImageBase64,intent.getStringExtra("id").toString())
        })

        binding.imageView8.setOnClickListener() {
            showPopup(it)
        }

        binding.txtAddUser.setOnClickListener(){
            val intent = Intent(this, ShowUsersActivity::class.java)
            intent.putExtra("id",chatId)
            startActivity(intent)
        }
    }

    private fun onMessagesChange(binding: ActivityChatBinding) {
            lifecycleScope.launch {
                val messagesResource = localMessageRepository.getChatMessages(chatId.toInt())
                when (messagesResource.status) {
                    Resource.Status.SUCCESS -> {
                        val messagesfromRepo = localMessageRepository.getChatMessages(chatId.toInt())
                        val messages = messagesfromRepo.data
                        chatAdapter.submitList(messages)
                        chatAdapter.notifyDataSetChanged()
                        binding.chatView.smoothScrollToPosition(chatAdapter.itemCount)
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
            binding.editTextUsername2.setText("")
            socketService.onSendMessage(message,intent.getStringExtra("id").toString())
            onMessagesChange(binding)
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
                        socketService.onSendMessage(message,intent.getStringExtra("id").toString())
                    } else {
                        Log.d("ChatActivity", "No hay ubicación disponible.")
                    }
                }
                R.id.file -> {
                    selectFile()
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
        unregisterReceiver(locationReceiver)
        val intent = Intent(MyApp.context, LocationService::class.java)
        MyApp.context.stopService(intent)
    }
    private fun obtenerUltimaUbicacion(): Location? {
        return lastReceivedLocation
    }
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, SocketService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

        EventBus.getDefault().register(this)
    }
    override fun onStop() {
        super.onStop()
        if (isBind) {
            unbindService(serviceConnection)
        }

        EventBus.getDefault().unregister(this)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNotificaionMessage(message : Message){
        onMessagesChange(binding)
    }
}