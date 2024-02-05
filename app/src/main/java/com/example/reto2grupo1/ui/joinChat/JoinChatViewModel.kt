package com.example.reto2grupo1.ui.joinChat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.ChatListRepository
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JoinChatViewModelFactory(
    private val chatListRepository: ChatListRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras : CreationExtras):T{
        return JoinChatViewModel(chatListRepository) as T
    }
}

class JoinChatViewModel(
    private val chatListRepository: ChatListRepository
) : ViewModel() {

    private val _chats = MutableLiveData<Resource<List<Chat>>>()
    val chats: LiveData<Resource<List<Chat>>> get() = _chats

    private val _join = MutableLiveData<Resource<Boolean>>()
    val join : LiveData<Resource<Boolean>> get() = _join
    init {
        getChats()
    }

    fun getChats() {
        viewModelScope.launch {
            val  response = showPublicChasts()
            _chats.value = response
        }
    }
    suspend fun showPublicChasts(): Resource<List<Chat>>
    {
        return withContext(Dispatchers.IO){
            chatListRepository.getAllPublicChat()
        }
    }

    fun joinToChat(chatId: Int?){
        Log.d("estoentra3","estoentra3");

        viewModelScope.launch {
            val  response = joinChat(chatId)
            Log.d("estoentra4","estoentra4");

            _join.value = response
        }
    }
    suspend fun joinChat(chatId:Int?): Resource<Boolean> {
        return withContext(Dispatchers.IO){
            Log.d("estoentra5", "estoentra4 $chatId");

            chatListRepository.joinChat(chatId)
        }
    }

}