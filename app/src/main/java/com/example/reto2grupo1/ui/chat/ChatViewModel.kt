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

    private val _leave = MutableLiveData<Resource<Boolean>>()
    val leave: LiveData<Resource<Boolean>> get() = _leave

    private val _imageBase64 = MutableLiveData<String>()
    val imageBase64: LiveData<String>
        get() = _imageBase64

    fun updateImageBase64(base64: String) {
        _imageBase64.value = base64
    }

    private val SOCKET_HOST = "http://10.5.7.13:8085/"
    private val AUTHORIZATION_HEADER = "Authorization"
    private lateinit var mSocket: Socket


    fun getChatContent(id: Int){
        viewModelScope.launch {
            val repoResponse  = getChatMessages(id)
            _messages.value = repoResponse
        }
    }
    private suspend fun getChatMessages(id: Int) : Resource<List<Message>>{
        return withContext(Dispatchers.IO){
            val messages = chatRepository.getChatMessages(id)
            Resource.success(messages)
            chatRepository.getChatMessages(id)
        }
    }

    fun getOutChat(id: Int){
        viewModelScope.launch {
            val response = leaveChat(id)
            _leave.value = response
        }
    }
    private suspend fun leaveChat(id: Int): Resource<Boolean>{
        return  withContext(Dispatchers.IO){
            chatRepository.leaveChat(id)
        }
    }
}