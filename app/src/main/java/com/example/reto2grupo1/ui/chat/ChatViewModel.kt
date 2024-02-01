package com.example.reto2grupo1.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.data.repository.ChatRepository
import com.example.reto2grupo1.data.socket.SocketEvents
import com.example.reto2grupo1.data.socket.SocketMessageReq
import com.example.reto2grupo1.data.socket.SocketMessageRes
import com.example.reto2grupo1.utils.Resource
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import io.socket.client.Socket


class ChatViewModelFactory(
    private val chatRepository: ChatRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras : CreationExtras):T{
        return ChatViewModel(chatRepository) as T
    }
}
class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val TAG = "ChatViewModel"

    private val _messages = MutableLiveData<Resource<List<Message>>>()
    val messages: LiveData<Resource<List<Message>>> get() = _messages

    private val _connected = MutableLiveData<Resource<Boolean>>()
    val connected: LiveData<Resource<Boolean>> get() = _connected

    private val _imageBase64 = MutableLiveData<String>()
    val imageBase64: LiveData<String>
        get() = _imageBase64

    fun updateImageBase64(base64: String) {
        _imageBase64.value = base64
    }

    private val SOCKET_HOST = "http://10.0.2.2:8085/"
    private val AUTHORIZATION_HEADER = "Authorization"
    private lateinit var mSocket: Socket
    private val SOCKET_ROOM = "1"

    fun startSocket(){
        Log.d("a","b")
        val socketOptions = createSocketOptions()
        mSocket = IO.socket(SOCKET_HOST, socketOptions)
        mSocket.on(SocketEvents.ON_CONNECT.value, onConnect())
        mSocket.on("connect_error", onConnectError())
        mSocket.on(SocketEvents.ON_DISCONNECT.value, onDisconnect())
        mSocket.on(SocketEvents.ON_MESSAGE_RECEIVED.value, onNewMessage())
        viewModelScope.launch {
            connect()
        }
    }

    private suspend fun connect(){
        withContext(Dispatchers.IO) {
            mSocket.connect()
        }
    }

    fun stopSocket(){
        viewModelScope.launch {
            disconnect()
        }
    }
    suspend fun disconnect(){
        withContext(Dispatchers.IO){
            mSocket.disconnect()
        }
    }
     suspend fun onDestroy() {

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
            Log.d(TAG, "no conectado $it")
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

    fun getChatContent(id: Int){
        viewModelScope.launch {
            val repoResponse  = getChatMessages(id)
            _messages.value = repoResponse
        }
    }
    private suspend fun getChatMessages(id: Int) : Resource<List<Message>>{
        return withContext(Dispatchers.IO){
            chatRepository.getChatMessages(id)
        }
    }

}