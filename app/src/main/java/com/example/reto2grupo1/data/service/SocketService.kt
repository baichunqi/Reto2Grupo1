package com.example.reto2grupo1.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.data.repository.remote.RemoteChatDataSource
import com.example.reto2grupo1.data.socket.SocketEvents
import com.example.reto2grupo1.data.socket.SocketMessageReq
import com.example.reto2grupo1.data.socket.SocketMessageRes
import com.example.reto2grupo1.ui.chat.ChatActivity
import com.example.reto2grupo1.ui.chat.ChatViewModel
import com.example.reto2grupo1.ui.chat.ChatViewModelFactory
import com.example.reto2grupo1.utils.Resource
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SocketService : Service() {


    private val NOTIFICATION_ID = 321
    private val CHANNEL_ID = "my_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("services", "onStartCommand")
        val contentText = "Descargando contenido"
        startForeground(NOTIFICATION_ID, createNotification(contentText))
        startSocket()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "conectando con el servidor",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(contentText: String): Notification {
        val context = this
        val intent = Intent(context, ChatActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("obteniendo mensajes")
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private val TAG = "ChatViewModel"

    private val _messages = MutableLiveData<Resource<List<Message>>>()
    val messages: LiveData<Resource<List<Message>>> get() = _messages

    private val _connected = MutableLiveData<Resource<Boolean>>()
    val connected: LiveData<Resource<Boolean>> get() = _connected

    private val SOCKET_HOST = "http://10.0.2.2:8085/"
    private val AUTHORIZATION_HEADER = "Authorization"
    private lateinit var mSocket: Socket


    fun startSocket(){
        Log.d("a","b")
        val socketOptions = createSocketOptions()
        mSocket = IO.socket(SOCKET_HOST, socketOptions)
        mSocket.on(SocketEvents.ON_CONNECT.value, onConnect())
        mSocket.on("connect_error", onConnectError())
        mSocket.on(SocketEvents.ON_DISCONNECT.value, onDisconnect())
        mSocket.on(SocketEvents.ON_MESSAGE_RECEIVED.value, onNewMessage())
//        viewModelScope.launch {
//            connect()
//        }
    }

    private suspend fun connect(){
        withContext(Dispatchers.IO) {
            mSocket.connect()
        }
    }

    fun stopSocket(){
//        viewModelScope.launch {
//            disconnect()
//        }
    }
    suspend fun disconnect(){
        withContext(Dispatchers.IO){
            mSocket.disconnect()
        }
    }

    private fun createSocketOptions(): IO.Options {
        val options = IO.Options()
        val authToken : String? = MyApp.userPreferences.fetchAuthToken()
        Log.e("TokenTocreateSocket", authToken.toString())
        // Add custom header
        val headers = mutableMapOf<String, MutableList<String>>()
        // TODO el token tendria que salir de las sharedPrefernces para conectarse
        headers[AUTHORIZATION_HEADER] = mutableListOf("Bearer ${authToken.toString()}")

        options.extraHeaders = headers
        return options
    }

    private fun onConnect(): Emitter.Listener {
        return Emitter.Listener {
            // Manejar el mensaje recibido
            Log.d(TAG, "conectado")
            // no vale poner value por que da error al estar en otro hilo
            // IllegalStateException: Cannot invoke setValue on a background thread
            // en funcion asincrona obligado post
            _connected.postValue(Resource.success(true))
        }
    }
    private fun onConnectError(): Emitter.Listener {
        return Emitter.Listener {
            // Manejar el mensaje recibido
            Log.e(TAG, "no conectado")
            Log.d(TAG, "no conectado ${it.toString()}")
        }
    }
    private fun onDisconnect(): Emitter.Listener {
        return Emitter.Listener {
            // Manejar el mensaje recibido
            Log.d(TAG, "desconectado")
            _connected.postValue(Resource.success(false))
        }
    }

    private fun onNewMessage(): Emitter.Listener {
        Log.d("OnnewMeesaje","OnnewMeesaje")
        return Emitter.Listener {
            // en teoria deberia ser siempre jsonObject, obviamente si siempre lo gestionamos asi
            if (it[0] is JSONObject) {
                onNewMessageJsonObject(it[0])
            } else if (it[0] is String) {
                onNewMessageString(it[0])
            }
        }
    }

    private fun onNewMessageString(data: Any) {
        try {
            // Manejar el mensaje recibido
            val message = data as String
            Log.d(TAG, "mensaje recibido $message")
            // ojo aqui no estoy actualizando la lista. aunque no deberiamos recibir strings
        } catch (ex: Exception) {
            Log.e(TAG, ex.message!!)
        }
    }

    private fun onNewMessageJsonObject(data : Any) {
        try {
            val jsonObject = data as JSONObject
            val jsonObjectString = jsonObject.toString()
            val message = Gson().fromJson(jsonObjectString, SocketMessageRes::class.java)

            Log.d(TAG, message.authorName)
            Log.d(TAG, message.messageType.toString())

            updateMessageListWithNewMessage(message)
        } catch (ex: Exception) {
            Log.e(TAG, ex.message!!)
        }
    }

    private fun updateMessageListWithNewMessage(message: SocketMessageRes) {
        try {
            val incomingMessage = Message(message.authorId,message.message, message.authorName, message.room)
            val msgsList = _messages.value?.data?.toMutableList()
            if (msgsList != null) {
                msgsList.add(incomingMessage)
                _messages.postValue(Resource.success(msgsList))
            } else {
                _messages.postValue(Resource.success(listOf(incomingMessage)))
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.message!!)
        }
    }

    fun onSendMessage(message: String, id : String) {
        Log.d(TAG, "onSendMessage $message")
        // la sala esta hardcodeada..
        val socketMessage = SocketMessageReq(id, message)
        val jsonObject = JSONObject(Gson().toJson(socketMessage))
        Log.d("json", jsonObject.toString())
        mSocket.emit(SocketEvents.ON_SEND_MESSAGE.value, jsonObject)
    }

    override fun onDestroy() {
        Log.i("services", "onDestroy")
        stopSocket()
        super.onDestroy()
    }
}